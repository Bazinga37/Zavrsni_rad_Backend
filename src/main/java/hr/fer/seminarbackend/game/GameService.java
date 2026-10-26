package hr.fer.seminarbackend.game;

import hr.fer.seminarbackend.ws.Ws;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Runs games. The server drives the whole game: every question goes through
 * countdown -> reading -> answering, then the round result is shown before the next question.
 */
@Service
public class GameService {
    public static final String GAME_ID = "gameId";
    public static final String PLAYER_ID = "playerId";

    static final int QUESTIONS_PER_GAME = 10;
    static final long COUNTDOWN_MS = 5_000;
    static final long READING_MS = 5_000;
    /** Screen highlights the correct answer (3 s), then shows the standings (5 s). */
    static final long RESULT_MS = 8_000;
    /** Extra time for answers that were sent just before the deadline. */
    static final long GRACE_MS = 500;
    static final int MAX_POINTS = 1000;

    private final GameStore store;
    private final Ws ws;
    private final Map<Long, ActiveGame> games = new ConcurrentHashMap<>();
    private final ScheduledExecutorService timers =
            Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform().name("game-timer").daemon().factory());

    public GameService(GameStore store, Ws ws) {
        this.store = store;
        this.ws = ws;
    }

    void start(Lobby lobby) {
        Lobby.Slot a = lobby.players.get(0);
        Lobby.Slot b = lobby.players.get(1);
        List<Question> questions = store.randomQuestions(QUESTIONS_PER_GAME);
        if (questions.isEmpty()) {
            for (WebSocketSession s : List.of(lobby.screen, a.session(), b.session())) ws.error(s, "No questions available");
            return;
        }
        long idA = store.getOrCreatePlayer(a.username());
        long idB = store.getOrCreatePlayer(b.username());
        long gameId = store.createGame(idA, idB, questions);

        ActiveGame game = new ActiveGame(gameId, questions, lobby.screen,
                new ActiveGame.Player(idA, a.username(), a.session()),
                new ActiveGame.Player(idB, b.username(), b.session()));
        games.put(gameId, game);

        synchronized (game) {
            tag(game.screen, gameId, null);
            for (ActiveGame.Player p : game.players()) tag(p.session, gameId, p.id);
            broadcast(game, "GAME_START", Map.of("gameId", gameId));
            nextQuestion(game);
        }
    }

    public void answer(long gameId, long playerId, long answerId) {
        ActiveGame game = games.get(gameId);
        if (game == null) return;
        synchronized (game) {
            if (game.phase != ActiveGame.Phase.QUESTION || game.answers.containsKey(playerId)) return;
            Question question = game.current();
            Question.Answer answer = question.answer(answerId);
            ActiveGame.Player player = game.player(playerId);
            if (answer == null || player == null) return;

            // Speed is measured from the moment answering opened; early answers count as instant.
            long durationMs = question.duration() * 1000L;
            int responseMs = (int) Math.clamp(System.currentTimeMillis() - game.activateAt, 0, durationMs);
            int points = answer.correct()
                    ? (int) Math.round(MAX_POINTS * (1 - responseMs / (2.0 * durationMs)))
                    : 0;

            game.answers.put(playerId, answerId);
            player.score += points;
            store.saveAnswer(game.id, playerId, question.id(), answerId, responseMs, points, answer.correct());

            if (game.answers.size() == 2) endRound(game, game.index);
        }
    }

    /** Reconnects a player (username given) or the screen (username null) to a running game. */
    public void rejoin(WebSocketSession session, long gameId, String username) {
        ActiveGame game = games.get(gameId);
        if (game == null) {
            ws.error(session, "Game not found");
            return;
        }
        synchronized (game) {
            Long playerId = null;
            if (username == null) {
                game.screen = session;
            } else {
                ActiveGame.Player player = game.playerByName(username);
                if (player == null) {
                    ws.error(session, "Player not in this game");
                    return;
                }
                player.session = session;
                playerId = player.id;
            }
            tag(session, gameId, playerId);
            ws.send(session, "REJOIN_OK", Map.of("gameId", gameId));
            ws.send(session, "QUESTION", questionPayload(game, playerId));
            if (game.phase == ActiveGame.Phase.RESULT) ws.send(session, "ROUND_RESULT", resultPayload(game));
        }
    }

    // --- game flow; all methods below are called while holding the game's lock ---

    private void nextQuestion(ActiveGame game) {
        game.index++;
        if (game.index >= game.questions.size()) {
            finish(game);
            return;
        }
        long now = System.currentTimeMillis();
        game.phase = ActiveGame.Phase.QUESTION;
        game.answers.clear();
        game.showAt = now + COUNTDOWN_MS;
        game.activateAt = game.showAt + READING_MS;
        game.endAt = game.activateAt + game.current().duration() * 1000L;

        broadcast(game, "QUESTION", questionPayload(game, null));
        int index = game.index;
        schedule(game, game.endAt - now + GRACE_MS, () -> endRound(game, index));
    }

    private void endRound(ActiveGame game, int index) {
        // Guards against a timer firing after the round was already ended by the second answer.
        if (game.phase != ActiveGame.Phase.QUESTION || game.index != index) return;
        if (game.timer != null) game.timer.cancel(false);
        game.phase = ActiveGame.Phase.RESULT;

        Question question = game.current();
        for (ActiveGame.Player p : game.players()) {
            if (!game.answers.containsKey(p.id)) {
                store.saveAnswer(game.id, p.id, question.id(), null, null, 0, false);
            }
        }
        broadcast(game, "ROUND_RESULT", resultPayload(game));
        schedule(game, RESULT_MS, () -> {
            if (game.phase == ActiveGame.Phase.RESULT && game.index == index) nextQuestion(game);
        });
    }

    private void finish(ActiveGame game) {
        game.phase = ActiveGame.Phase.FINISHED;
        store.finishGame(game.id, game.one.score, game.two.score);
        broadcast(game, "GAME_END", scores(game));
        games.remove(game.id);
    }

    private void schedule(ActiveGame game, long delayMs, Runnable action) {
        game.timer = timers.schedule(() -> {
            synchronized (game) {
                action.run();
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    // --- messages ---

    /** Times are sent as "remaining ms" so the client never compares its clock with the server's. */
    private Map<String, Object> questionPayload(ActiveGame game, Long playerId) {
        Question q = game.current();
        long now = System.currentTimeMillis();
        Map<String, Object> payload = new HashMap<>();
        payload.put("index", game.index + 1);
        payload.put("total", game.questions.size());
        payload.put("question", q.text());
        payload.put("answers", q.answers().stream()
                .map(a -> Map.of("answerId", a.id(), "content", a.content()))
                .toList());
        payload.put("duration", q.duration());
        payload.put("countdownMs", Math.max(0, game.showAt - now));
        payload.put("readingMs", Math.max(0, game.activateAt - Math.max(now, game.showAt)));
        payload.put("answerMs", Math.max(0, game.endAt - Math.max(now, game.activateAt)));
        if (playerId != null) payload.put("selectedAnswerId", game.answers.get(playerId));
        return payload;
    }

    private Map<String, Object> resultPayload(ActiveGame game) {
        Map<String, Object> payload = scores(game);
        payload.put("correctAnswerId", game.current().correctAnswerId());
        return payload;
    }

    private Map<String, Object> scores(ActiveGame game) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerOne", game.one.username);
        payload.put("playerTwo", game.two.username);
        payload.put("scoreOne", game.one.score);
        payload.put("scoreTwo", game.two.score);
        return payload;
    }

    private void broadcast(ActiveGame game, String type, Object payload) {
        ws.send(game.screen, type, payload);
        ws.send(game.one.session, type, payload);
        ws.send(game.two.session, type, payload);
    }

    private static void tag(WebSocketSession session, long gameId, Long playerId) {
        session.getAttributes().put(GAME_ID, gameId);
        if (playerId != null) session.getAttributes().put(PLAYER_ID, playerId);
    }
}

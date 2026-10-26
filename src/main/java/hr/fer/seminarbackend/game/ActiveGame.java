package hr.fer.seminarbackend.game;

import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/** In-memory state of a running game. Every access is guarded by synchronized(game) in GameService. */
class ActiveGame {
    enum Phase { QUESTION, RESULT, FINISHED }

    static class Player {
        final long id;
        final String username;
        WebSocketSession session;
        int score;

        Player(long id, String username, WebSocketSession session) {
            this.id = id;
            this.username = username;
            this.session = session;
        }
    }

    final long id;
    final List<Question> questions;
    final Player one;
    final Player two;
    WebSocketSession screen;

    int index = -1;
    Phase phase;
    /** Server timestamps (ms) for the current question: shown after the countdown, answerable after reading. */
    long showAt;
    long activateAt;
    long endAt;
    /** playerId -> answerId for the current question. */
    final Map<Long, Long> answers = new HashMap<>();
    ScheduledFuture<?> timer;

    ActiveGame(long id, List<Question> questions, WebSocketSession screen, Player one, Player two) {
        this.id = id;
        this.questions = questions;
        this.screen = screen;
        this.one = one;
        this.two = two;
    }

    Question current() {
        return questions.get(index);
    }

    List<Player> players() {
        return List.of(one, two);
    }

    Player player(long playerId) {
        return one.id == playerId ? one : two.id == playerId ? two : null;
    }

    Player playerByName(String username) {
        return one.username.equalsIgnoreCase(username) ? one
                : two.username.equalsIgnoreCase(username) ? two : null;
    }
}

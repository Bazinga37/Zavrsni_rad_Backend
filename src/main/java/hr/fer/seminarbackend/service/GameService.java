package hr.fer.seminarbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.fer.seminarbackend.mapper.AnswerMapper;
import hr.fer.seminarbackend.model.*;
import hr.fer.seminarbackend.model.DTO.AnswerDto;
import hr.fer.seminarbackend.repository.AnswerRepository;
import hr.fer.seminarbackend.repository.GameRepository;
import hr.fer.seminarbackend.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerService playerService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AnswerMapper answerMapper;
    private final ObjectMapper mapper;

    private final Map<Long, ActiveGameSession> activeGames = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(4, Thread.ofVirtual().factory());
    private final Map<Long, ScheduledFuture<?>> activeTimers = new ConcurrentHashMap<>();

    @Transactional
    public ActiveGameSession startGame(String usernameOne, String usernameTwo,
                          WebSocketSession session1, WebSocketSession session2,
                          WebSocketSession screenSession) {
        Player playerOne = playerService.getOrCreatePlayer(usernameOne);
        Player playerTwo = playerService.getOrCreatePlayer(usernameTwo);

        Game newGame = Game.builder().
                playerOne(playerOne).
                playerTwo(playerTwo).
                scoreOne(0.0).
                scoreTwo(0.0).
                createdAt(OffsetDateTime.now()).
                finished(false).
                build();

        Game savedGame = gameRepository.save(newGame);

        ActiveGameSession session = new ActiveGameSession(savedGame, session1, session2, screenSession,
                usernameOne, usernameTwo);

        session.setQuestions(questionRepository.findRandomQuestions(10));

        activeGames.put(savedGame.getGameId(), session);
        return session;
    }

    public void startNextQuestion(Long gameId){
        ActiveGameSession session = activeGames.get(gameId);
        Question question = session.getCurrentQuestion();
        if (question == null) return;

        List<Answer> answers = new ArrayList<>(answerRepository.findAllByQuestion(question));
        Collections.shuffle(answers);

        List<AnswerDto> answersDto = answers.stream()
                .map(answer -> {
                    AnswerDto answerDto = answerMapper.toAnswerDto(answer);
                    answerDto.setCorrect(null);
                    return answerDto;
                }).collect(Collectors.toList());

        // countdownMs must match the 5-second hardcoded frontend CountdownComponent.
        // readingPhaseMs is the window after the countdown where the question is shown
        // before answer buttons activate. activationTime is the moment answering opens.
        long countdownMs = 5000;
        long readingPhaseMs = 5000;
        long activationTime = System.currentTimeMillis() + countdownMs + readingPhaseMs;
        session.setQuestionStartTime(activationTime);
        session.setAcceptingAnswers(true);
        session.getPlayersWhoAnswered().clear();
        session.getPlayerAnswers().clear();

        Map<String, Object> roundDto = Map.of(
                "type", ServerMessage.MessageType.QUESTION,
                "question", question.getText(),
                "answers", answersDto,
                "duration", question.getDuration(),
                "activationTime", activationTime,
                "activationDelay", readingPhaseMs,
                "index", session.getCurrentQuestionIndex() + 1
        );

        session.setLastQuestionBroadcast(roundDto);
        session.setLastRoundResultBroadcast(null);

        broadcastToGame(gameId, roundDto);

        long questionDurationMs = question.getDuration() * 1000L;
        long totalWaitTimeMs = countdownMs + readingPhaseMs + questionDurationMs;

        ScheduledFuture<?> timeoutTask = scheduler.schedule(() -> {
            forceRoundEnd(gameId);
        }, totalWaitTimeMs, TimeUnit.MILLISECONDS);

        activeTimers.put(gameId, timeoutTask);
    }

    public void submitAnswer(Long gameId, Long playerId, Long answerId) {
        ActiveGameSession session = activeGames.get(gameId);
        if (session == null || !session.isAcceptingAnswers()) return;

        long now = System.currentTimeMillis();
        // Clamp to 0 so answers that arrive a few ms before activationTime (due to
        // client/server clock skew) are accepted and scored as maximum-speed answers.
        long reactionTime = Math.max(0L, now - session.getQuestionStartTime());

        if (session.getPlayersWhoAnswered().add(playerId)) {
            session.getPlayerAnswers().put(playerId, answerId);
            Answer answer = answerRepository.findById(answerId).orElse(null);
            if (answer == null) return;

            Question currentQuestion = session.getCurrentQuestion();

            boolean correct = answer.isCorrect() &&
                    answer.getQuestion().getQuestionId().equals(currentQuestion.getQuestionId());

            if (correct) {
                double maxPoints = 1000.0;
                double durationMs = session.getCurrentQuestion().getDuration() * 1000.0;
                double points = maxPoints * (1 - ((reactionTime / durationMs) / 2));
                session.addScore(playerId, points);
            }

            if (session.getPlayersWhoAnswered().size() == 2) {
                cancelTimer(gameId);
                endRound(session);
            }
        }
    }

    private void forceRoundEnd(Long gameId) {
        ActiveGameSession session = activeGames.get(gameId);
        if (session != null && session.isAcceptingAnswers()) {
            endRound(session);
        }
    }

    @Transactional
    void endRound(ActiveGameSession session) {
        session.setAcceptingAnswers(false);

        Question currentQuestion = session.getCurrentQuestion();

        Long correctAnswerId = answerRepository.findByQuestionAndCorrectTrue(currentQuestion)
                .map(Answer::getAnswerId)
                .orElse(-1L);

        Map<String, Object> results = Map.of(
                "type", ServerMessage.MessageType.ROUND_RESULT,
                "playerOne", session.getPlayerOneUsername(),
                "playerTwo", session.getPlayerTwoUsername(),
                "scoreOne", session.getScoreOne(),
                "scoreTwo", session.getScoreTwo(),
                "correctAnswerId", correctAnswerId
        );
        session.setLastRoundResultBroadcast(results);
        broadcastToGame(session.getGameId(), results);

        session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);
    }

    @Transactional
    public void handleNextQuestionRequest(Long gameId) {
        ActiveGameSession session = activeGames.get(gameId);
        if (session == null) return;
        if (session.isAcceptingAnswers()) return;

        if (session.getCurrentQuestionIndex() < session.getQuestions().size()) {
            startNextQuestion(gameId);
        } else {
            broadcastToGame(gameId, Map.of("type", ServerMessage.MessageType.GAME_END));
            finishAndSaveGame(gameId);
        }
    }

    private void cancelTimer(Long gameId) {
        ScheduledFuture<?> timer = activeTimers.remove(gameId);
        if (timer != null) timer.cancel(false);
    }

    public void broadcastToGame(Long gameId, Object payload){
        ActiveGameSession session = activeGames.get(gameId);
        if (session == null) return;
        try {
            TextMessage message = new TextMessage(mapper.writeValueAsString(payload));
            WebSocketSession p1 = session.getPlayerOneSession();
            WebSocketSession p2 = session.getPlayerTwoSession();
            WebSocketSession sc = session.getScreenSession();
            if (p1 != null && p1.isOpen()) p1.sendMessage(message);
            if (p2 != null && p2.isOpen()) p2.sendMessage(message);
            if (sc != null && sc.isOpen()) sc.sendMessage(message);
        } catch (Exception e){
            System.err.println("Failed to broadcast to game " + gameId);
        }
    }

    public void rejoinPlayer(Long gameId, String username, WebSocketSession newSession) {
        ActiveGameSession session = activeGames.get(gameId);
        if (session == null) {
            sendErrorToSession(newSession, "Game not found");
            return;
        }

        Long playerId;
        if (session.getPlayerOneUsername().equalsIgnoreCase(username)) {
            session.setPlayerOneSession(newSession);
            playerId = session.getPlayerOneId();
        } else if (session.getPlayerTwoUsername().equalsIgnoreCase(username)) {
            session.setPlayerTwoSession(newSession);
            playerId = session.getPlayerTwoId();
        } else {
            sendErrorToSession(newSession, "Player not in this game");
            return;
        }

        newSession.getAttributes().put("GAME_ID", gameId);
        newSession.getAttributes().put("PLAYER_ID", playerId);

        boolean alreadyAnswered = session.getPlayersWhoAnswered().contains(playerId);
        Long selectedAnswerId = session.getPlayerAnswers().get(playerId);
        Map<String, Object> rejoinPayload = new HashMap<>();
        rejoinPayload.put("alreadyAnswered", alreadyAnswered);
        rejoinPayload.put("selectedAnswerId", selectedAnswerId);
        sendToSession(newSession, new ServerMessage<>(ServerMessage.MessageType.REJOIN_OK, rejoinPayload));
        resendGameState(session, newSession);
    }

    public void rejoinScreen(Long gameId, WebSocketSession newSession) {
        ActiveGameSession session = activeGames.get(gameId);
        if (session == null) {
            sendErrorToSession(newSession, "Game not found");
            return;
        }

        session.setScreenSession(newSession);
        newSession.getAttributes().put("GAME_ID", gameId);

        sendToSession(newSession, new ServerMessage<>(ServerMessage.MessageType.REJOIN_OK, null));
        resendGameState(session, newSession);
    }

    private void resendGameState(ActiveGameSession session, WebSocketSession target) {
        if (session.isAcceptingAnswers() && session.getLastQuestionBroadcast() != null) {
            sendToSession(target, session.getLastQuestionBroadcast());
        } else if (!session.isAcceptingAnswers()) {
            // Send question first so the frontend can render it, then send the result on top.
            if (session.getLastQuestionBroadcast() != null) {
                sendToSession(target, session.getLastQuestionBroadcast());
            }
            if (session.getLastRoundResultBroadcast() != null) {
                sendToSession(target, session.getLastRoundResultBroadcast());
            }
        }
    }

    private void sendToSession(WebSocketSession session, Object payload) {
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(mapper.writeValueAsString(payload)));
            }
        } catch (Exception e) {
            System.err.println("Failed to send to session " + (session != null ? session.getId() : "null") + ": " + e.getMessage());
        }
    }

    private void sendErrorToSession(WebSocketSession session, String message) {
        sendToSession(session, new ServerMessage<>(ServerMessage.MessageType.ERROR,
                Map.of("message", message)));
    }

    @Transactional
    public void finishAndSaveGame(Long gameId) {
        ActiveGameSession session = activeGames.remove(gameId);

        if (session == null) throw new RuntimeException("Game session not found!");

        Game game = gameRepository.findById(gameId).
                orElseThrow(() -> new EntityNotFoundException("Game record not found!"));

        game.setScoreOne(session.getScoreOne());
        game.setScoreTwo(session.getScoreTwo());
        game.setFinished(true);

        gameRepository.save(game);
    }
}

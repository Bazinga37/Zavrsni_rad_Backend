package hr.fer.seminarbackend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class ActiveGameSession {
    private final Long gameId;
    private final Long playerOneId;
    private final Long playerTwoId;
    private final String playerOneUsername;
    private final String playerTwoUsername;
    private final OffsetDateTime startTime;

    private WebSocketSession playerOneSession;
    private WebSocketSession playerTwoSession;
    private WebSocketSession screenSession;

    private Double scoreOne = 0.0;
    private Double scoreTwo = 0.0;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private long questionStartTime;
    private boolean acceptingAnswers = false;

    private final Set<Long> playersWhoAnswered = ConcurrentHashMap.newKeySet();
    private final Map<Long, Long> playerAnswers = new ConcurrentHashMap<>();

    private Map<String, Object> lastQuestionBroadcast;
    private Map<String, Object> lastRoundResultBroadcast;

    public ActiveGameSession(Game game,
                             WebSocketSession playerOneSession,
                             WebSocketSession playerTwoSession,
                             WebSocketSession screenSession,
                             String playerOneUsername,
                             String playerTwoUsername) {
        this.gameId = game.getGameId();
        this.playerOneId = game.getPlayerOne().getPlayerId();
        this.playerTwoId = game.getPlayerTwo().getPlayerId();
        this.playerOneUsername = playerOneUsername;
        this.playerTwoUsername = playerTwoUsername;
        this.playerOneSession = playerOneSession;
        this.playerTwoSession = playerTwoSession;
        this.screenSession = screenSession;
        this.startTime = game.getCreatedAt();
    }

    public void addScore(Long playerId, double pointsToAdd) {
        if (playerId.equals(this.playerOneId)) this.scoreOne += pointsToAdd;
        else if (playerId.equals(this.playerTwoId)) this.scoreTwo += pointsToAdd;
    }

    public Question getCurrentQuestion() {
        if(questions != null && currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }
}

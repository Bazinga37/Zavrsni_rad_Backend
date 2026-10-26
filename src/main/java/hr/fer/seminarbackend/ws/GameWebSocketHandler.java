package hr.fer.seminarbackend.ws;

import hr.fer.seminarbackend.game.GameService;
import hr.fer.seminarbackend.game.LobbyService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Client messages are {"type": ..., "payload": {...}}:
 * CONNECT {role: SCREEN} | CONNECT {role: PLAYER, pin, username} | VALIDATE_PIN {pin}
 * | ANSWER {answerId} | REJOIN {role: SCREEN, gameId} | REJOIN {role: PLAYER, gameId, username}
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final Ws ws;
    private final LobbyService lobbies;
    private final GameService games;

    public GameWebSocketHandler(ObjectMapper mapper, Ws ws, LobbyService lobbies, GameService games) {
        this.mapper = mapper;
        this.ws = ws;
        this.lobbies = lobbies;
        this.games = games;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        JsonNode msg;
        try {
            msg = mapper.readTree(message.getPayload());
        } catch (JacksonException e) {
            ws.error(session, "Invalid message");
            return;
        }
        JsonNode payload = msg.path("payload");

        boolean screen = payload.path("role").asString("").equals("SCREEN");

        switch (msg.path("type").asString("")) {
            case "CONNECT" -> {
                if (screen) lobbies.open(session);
                else lobbies.join(session, payload.path("pin").asString(""), payload.path("username").asString("").trim());
            }
            case "VALIDATE_PIN" -> lobbies.validatePin(session, payload.path("pin").asString(""));
            case "ANSWER" -> {
                Object gameId = session.getAttributes().get(GameService.GAME_ID);
                Object playerId = session.getAttributes().get(GameService.PLAYER_ID);
                if (gameId != null && playerId != null) {
                    games.answer((Long) gameId, (Long) playerId, payload.path("answerId").asLong(-1));
                }
            }
            case "REJOIN" -> games.rejoin(session, payload.path("gameId").asLong(-1),
                    screen ? null : payload.path("username").asString("").trim());
            default -> ws.error(session, "Unknown message type");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        lobbies.onClose(session);
    }
}

package hr.fer.seminarbackend.handler;

import tools.jackson.databind.ObjectMapper;
import hr.fer.seminarbackend.model.ClientMessage;
import hr.fer.seminarbackend.model.ServerMessage;
import hr.fer.seminarbackend.model.payloads.ErrorPayload;
import hr.fer.seminarbackend.model.payloads.RejoinRequestPayload;
import hr.fer.seminarbackend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class RejoinHandler implements MessageHandler {

    private final ObjectMapper mapper;
    private final GameService gameService;

    @Override
    public ClientMessage.MessageType supports() {
        return ClientMessage.MessageType.REJOIN;
    }

    @Override
    public void handle(WebSocketSession session, ClientMessage message) throws Exception {
        if (message.getPayload() == null || message.getPayload().isBlank()) {
            sendError(session, "Missing rejoin payload");
            return;
        }

        RejoinRequestPayload req = mapper.readValue(message.getPayload(), RejoinRequestPayload.class);

        if (req.getGameId() == null) {
            sendError(session, "Missing gameId");
            return;
        }

        if (message.getRole() == ClientMessage.Role.PLAYER) {
            if (req.getUsername() == null || req.getUsername().isBlank()) {
                sendError(session, "Missing username for player rejoin");
                return;
            }
            gameService.rejoinPlayer(req.getGameId(), req.getUsername().trim(), session);

        } else if (message.getRole() == ClientMessage.Role.SCREEN) {
            gameService.rejoinScreen(req.getGameId(), session);

        } else {
            sendError(session, "Invalid role for rejoin");
        }
    }

    private void sendError(WebSocketSession session, String msg) throws Exception {
        session.sendMessage(new TextMessage(
                mapper.writeValueAsString(new ServerMessage<>(ServerMessage.MessageType.ERROR, new ErrorPayload(msg)))
        ));
    }
}

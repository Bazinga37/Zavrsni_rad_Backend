package hr.fer.seminarbackend.handler;

import tools.jackson.databind.ObjectMapper;
import hr.fer.seminarbackend.model.ClientMessage;
import hr.fer.seminarbackend.model.Lobby;
import hr.fer.seminarbackend.model.ServerMessage;
import hr.fer.seminarbackend.model.WebSocketState;
import hr.fer.seminarbackend.model.payloads.ErrorPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class ValidatePinHandler implements MessageHandler {

    private final WebSocketState state;
    private final ObjectMapper mapper;

    @Override
    public ClientMessage.MessageType supports() {
        return ClientMessage.MessageType.VALIDATE_PIN;
    }

    @Override
    public void handle(WebSocketSession session, ClientMessage message) throws Exception {
        String pin = message.getPayload();

        synchronized (state.getLock()) {
            Lobby lobby = state.getLobby();
            if (lobby == null || lobby.getScreen() == null || !lobby.getScreen().isOpen()
                    || lobby.isStarted() || !lobby.getPin().equals(pin)) {
                send(session, new ServerMessage<>(ServerMessage.MessageType.ERROR, new ErrorPayload("Game not found")));
                return;
            }
        }

        send(session, new ServerMessage<>(ServerMessage.MessageType.PIN_OK, null));
    }

    private void send(WebSocketSession session, Object msg) throws Exception {
        session.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
    }
}

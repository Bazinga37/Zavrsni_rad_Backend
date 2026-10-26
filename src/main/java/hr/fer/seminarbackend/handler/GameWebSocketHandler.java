package hr.fer.seminarbackend.handler;

import tools.jackson.databind.ObjectMapper;
import hr.fer.seminarbackend.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final MessageHandlerRegistry registry;
    private final WebSocketState state;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("WS CONNECTED: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("WS MESSAGE: " + message.getPayload());
        ClientMessage msg = mapper.readValue(message.getPayload(), ClientMessage.class);

        if (msg.getMessageType() == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        var handler = registry.get(msg.getMessageType());
        if (handler == null) {
            session.sendMessage(new TextMessage("{\"type\":\"ERROR\",\"message\":\"Unknown messageType\"}"));
            return;
        }

        handler.handle(session, msg);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WS CLOSED: " + session.getId() + " " + status);
        Lobby lobby;
        boolean screenDisconnected = false;
        boolean playedDisconnected = false;

        synchronized (state.getLock()) {
            lobby = state.getLobby();
            if (lobby == null) return;

            if (session == lobby.getScreen()) {
                lobby.setScreen(null);
                lobby.setStarted(false);
                screenDisconnected = true;

                // Optional: when screen disconnects, reset whole lobby
                // state.setLobby(null);
            } else {
                playedDisconnected = lobby.removePlayer(session.getId());
                System.out.println("Player removed: " + playedDisconnected);
                // Optional: if game hasn't started yet and a player leaves, you can un-start
                // if (removedUsername != null && lobby.isStarted()) lobby.setStarted(false);
            }
        }

        // notify remaining parties outside lock
        if (playedDisconnected) {
            notifyScreenLobbyStateSafe();
        }

        if (screenDisconnected) {
            // Optional: kick players if screen leaves
            // closeAllPlayersSafe();
        }
    }

    private void notifyScreenLobbyStateSafe() throws Exception {
        Lobby lobby;
        int count;
        boolean started;

        synchronized (state.getLock()) {
            lobby = state.getLobby();
            System.out.println("Closing connection!");
            if (lobby == null || lobby.getScreen() == null || !lobby.getScreen().isOpen()) return;
            count = lobby.playerCount();
            started = lobby.isStarted();
        }

        lobby.getScreen().sendMessage(new TextMessage("""
            {"type":"LOBBY_STATE","players":%d,"started":%s}
        """.formatted(count, started)));
    }
}


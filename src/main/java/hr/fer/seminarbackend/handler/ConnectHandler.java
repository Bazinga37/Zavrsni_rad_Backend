package hr.fer.seminarbackend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.fer.seminarbackend.model.*;
import hr.fer.seminarbackend.model.payloads.ErrorPayload;
import hr.fer.seminarbackend.model.payloads.GameStartPayload;
import hr.fer.seminarbackend.model.payloads.JoinOkPayload;
import hr.fer.seminarbackend.model.payloads.LobbyStatePayload;
import hr.fer.seminarbackend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class ConnectHandler implements MessageHandler {

    private final WebSocketState state;
    private final ObjectMapper mapper;
    private final GameService gameService;

    @Override
    public ClientMessage.MessageType supports() {
        return ClientMessage.MessageType.CONNECT;
    }

    @Override
    public void handle(WebSocketSession session, ClientMessage message) throws Exception {
        if (message.getRole() == ClientMessage.Role.SCREEN) {
            handleScreenConnect(session);
            return;
        }

        if (message.getRole() == ClientMessage.Role.PLAYER) {
            handlePlayerConnect(session, message.getPayload());
            return;
        }

        session.close(CloseStatus.BAD_DATA);
    }

    private void handleScreenConnect(WebSocketSession session) throws Exception {
        synchronized (state.getLock()) {
            if (state.getLobby() == null) {
                state.setLobby(new Lobby());
            }
            state.getLobby().setScreen(session);
        }

        send(session, new ServerMessage<>(ServerMessage.MessageType.SCREEN_OK, null));
        notifyLobbyStateToScreen();
    }

    private void handlePlayerConnect(WebSocketSession session, String username) throws Exception {
        if (username == null || username.isBlank()) {
            send(session, new ServerMessage<>(ServerMessage.MessageType.ERROR, new ErrorPayload("Username required")));
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        username = username.trim();

        Lobby lobby;
        boolean shouldStart = false;

        synchronized (state.getLock()) {
            lobby = state.getLobby();

            if (lobby == null || lobby.getScreen() == null || !lobby.getScreen().isOpen()) {
                send(session, new ServerMessage<>(ServerMessage.MessageType.ERROR, new ErrorPayload("No screen connected")));
                session.close(CloseStatus.BAD_DATA);
                return;
            }

            if (lobby.isStarted()) {
                send(session, new ServerMessage<>(ServerMessage.MessageType.ERROR, new ErrorPayload("Game already started")));
                session.close(CloseStatus.BAD_DATA);
                return;
            }

            if (lobby.containsUsername(username.toLowerCase())) {
                send(session, new ServerMessage<>(ServerMessage.MessageType.USERNAME_TAKEN, null));
                session.close(CloseStatus.BAD_DATA);
                return;
            }

            if (!lobby.hasSpace()) {
                send(session, new ServerMessage<>(ServerMessage.MessageType.ERROR, new ErrorPayload("Lobby full")));
                session.close(CloseStatus.BAD_DATA);
                return;
            }

            lobby.addPlayer(username, session);

            if (lobby.playerCount() == 2) {
                lobby.setStarted(true);
                shouldStart = true;
            }
        }

        send(session, new ServerMessage<>(ServerMessage.MessageType.JOIN_OK, new JoinOkPayload(username)));

        notifyLobbyStateToScreen();

        if (shouldStart) {
            broadcastGameStart(lobby);
        }
    }

    private void notifyLobbyStateToScreen() throws Exception {
        Lobby lobby;
        int count;
        boolean started;

        synchronized (state.getLock()) {
            lobby = state.getLobby();
            if (lobby == null || lobby.getScreen() == null || !lobby.getScreen().isOpen()) return;

            count = lobby.playerCount();
            started = lobby.isStarted();
        }

        send(lobby.getScreen(), new ServerMessage<>(
                ServerMessage.MessageType.LOBBY_STATE,
                new LobbyStatePayload(count, started)
        ));
    }

    private void broadcastGameStart(Lobby lobby) throws Exception {

        ActiveGameSession activeSession = gameService.startGame(
                lobby.getPlayers().get(0).username(),
                lobby.getPlayers().get(1).username(),
                lobby.getPlayers().get(0).session(),
                lobby.getPlayers().get(1).session(),
                lobby.getScreen()
        );

        tagSession(activeSession.getPlayerOneSession(), activeSession.getGameId(), activeSession.getPlayerOneId());
        tagSession(activeSession.getPlayerTwoSession(), activeSession.getGameId(), activeSession.getPlayerTwoId());
        tagSession(activeSession.getScreenSession(), activeSession.getGameId(), null);

        var message = new ServerMessage<>(ServerMessage.MessageType.GAME_START,
                new GameStartPayload(activeSession.getGameId()));

        send(activeSession.getScreenSession(), message);
        send(activeSession.getPlayerOneSession(), message);
        send(activeSession.getPlayerTwoSession(), message);

        gameService.startNextQuestion(activeSession.getGameId());

        synchronized (state.getLock()) {
            state.setLobby(null);
        }
    }

    private void tagSession(WebSocketSession session, Long gameId, Long playerId) {
        if(session != null) {
            session.getAttributes().put("GAME_ID", gameId);
            if(playerId != null) {
                session.getAttributes().put("PLAYER_ID", playerId);
            }
        }
    }

    private void send(WebSocketSession session, Object msg) throws Exception {
        session.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
    }
}

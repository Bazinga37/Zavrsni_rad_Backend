package hr.fer.seminarbackend.game;

import hr.fer.seminarbackend.ws.Ws;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/** Lobbies waiting for players, keyed by PIN. A game starts as soon as the second player joins. */
@Service
public class LobbyService {
    private static final String PIN = "pin";
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final GameService games;
    private final Ws ws;
    private final Map<String, Lobby> lobbies = new HashMap<>();

    public LobbyService(GameService games, Ws ws) {
        this.games = games;
        this.ws = ws;
    }

    public synchronized void open(WebSocketSession screen) {
        String pin;
        do {
            pin = String.format("%05d", RANDOM.nextInt(100_000));
        } while (lobbies.containsKey(pin));
        lobbies.put(pin, new Lobby(pin, screen));
        screen.getAttributes().put(PIN, pin);
        ws.send(screen, "SCREEN_OK", Map.of("pin", pin));
        ws.send(screen, "LOBBY_STATE", Map.of("players", 0));
    }

    public synchronized void validatePin(WebSocketSession session, String pin) {
        if (lobbies.containsKey(pin)) ws.send(session, "PIN_OK", null);
        else ws.error(session, "Game not found");
    }

    public void join(WebSocketSession session, String pin, String username) {
        Lobby lobby;
        int count;
        synchronized (this) {
            lobby = lobbies.get(pin);
            String error = lobby == null ? "Game not found"
                    : username.isEmpty() ? "Enter a nickname."
                    : username.length() > MAX_USERNAME_LENGTH ? "Nickname is too long."
                    : lobby.players.stream().anyMatch(p -> p.username().equalsIgnoreCase(username))
                    ? "Username already taken, please try again!"
                    : null;
            if (error != null) {
                ws.error(session, error);
                return;
            }
            lobby.players.add(new Lobby.Slot(username, session));
            session.getAttributes().put(PIN, pin);
            count = lobby.players.size();
            if (count == 2) lobbies.remove(pin);
        }
        ws.send(session, "JOIN_OK", Map.of("username", username));
        ws.send(lobby.screen, "LOBBY_STATE", Map.of("players", count));
        if (count == 2) games.start(lobby);
    }

    public synchronized void onClose(WebSocketSession session) {
        String pin = (String) session.getAttributes().get(PIN);
        Lobby lobby = pin == null ? null : lobbies.get(pin);
        if (lobby == null) return; // not in a lobby, or the game already started

        if (lobby.screen == session) {
            lobbies.remove(pin);
            for (Lobby.Slot p : lobby.players) ws.error(p.session(), "Screen disconnected");
        } else if (lobby.players.removeIf(p -> p.session() == session)) {
            ws.send(lobby.screen, "LOBBY_STATE", Map.of("players", lobby.players.size()));
        }
    }
}

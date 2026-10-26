package hr.fer.seminarbackend.game;

import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

/** A screen waiting for two players to join with its PIN. */
class Lobby {
    record Slot(String username, WebSocketSession session) {}

    final String pin;
    final WebSocketSession screen;
    final List<Slot> players = new ArrayList<>(2);

    Lobby(String pin, WebSocketSession screen) {
        this.pin = pin;
        this.screen = screen;
    }
}

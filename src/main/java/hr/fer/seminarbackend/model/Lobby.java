package hr.fer.seminarbackend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Lobby {
    private final String pin = String.format("%05d", ThreadLocalRandom.current().nextInt(100_000));
    @Setter
    private WebSocketSession screen;
    private final List<PlayerSlot> players = new ArrayList<>(2);
    @Setter
    private boolean started = false;

    public record PlayerSlot(String username, WebSocketSession session) {}

    public boolean hasSpace(){
        return players.size() < 2;
    }

    public boolean containsUsername(String username){
        return players.stream().anyMatch(p -> p.username.equalsIgnoreCase(username));
    }

    public void addPlayer(String username, WebSocketSession session){
        if (players.size() >= 2) throw new IllegalStateException("Cannot add more players, lobby is full!");
        players.add(new PlayerSlot(username, session));
    }

    public boolean removePlayer(String sessionId){
        return players.removeIf(p -> p.session().getId().equals(sessionId));
    }

    public int playerCount(){
        return players.size();
    }
}

package hr.fer.seminarbackend.service;

import hr.fer.seminarbackend.model.Player;
import hr.fer.seminarbackend.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository repository;

    public Player getOrCreatePlayer(String username){
        String sanitizedUsername = username.toLowerCase().trim();

        return repository.findByUsernameIgnoreCase(sanitizedUsername)
                .orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setUsername(sanitizedUsername);
                    return repository.save(newPlayer);
                });
    }
}

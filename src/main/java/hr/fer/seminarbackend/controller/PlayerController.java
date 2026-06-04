package hr.fer.seminarbackend.controller;

import hr.fer.seminarbackend.model.DTO.PlayerDto;
import hr.fer.seminarbackend.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/player")//svi endpointovi zapocinju sa /player
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService service;

    @GetMapping("/{username}")
    public ResponseEntity<PlayerDto> getPlayer(@PathVariable String username){
        return new ResponseEntity<>(service.getByUsername(username), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<PlayerDto> createPlayer(@RequestBody String username){
        return new ResponseEntity<>(service.savePlayer(username), HttpStatus.CREATED);
    }
}

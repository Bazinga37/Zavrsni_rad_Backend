package hr.fer.seminarbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerMessage<T> {

    public enum MessageType {
        SCREEN_OK,
        JOIN_OK,
        USERNAME_TAKEN,
        LOBBY_STATE,
        GAME_START,
        ERROR,

        QUESTION,
        ROUND_RESULT,
        GAME_END,
        REJOIN_OK
    }

    private MessageType type;
    private T payload;
}

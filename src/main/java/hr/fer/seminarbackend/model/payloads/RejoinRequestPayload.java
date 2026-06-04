package hr.fer.seminarbackend.model.payloads;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RejoinRequestPayload {
    private Long gameId;
    private String username;
}

package hr.fer.seminarbackend.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/** Sends {"type": ..., "payload": ...} messages to clients. */
@Component
public class Ws {
    private static final Logger log = LoggerFactory.getLogger(Ws.class);

    private final ObjectMapper mapper;

    public Ws(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void send(WebSocketSession session, String type, Object payload) {
        if (session == null || !session.isOpen()) return;
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("payload", payload);
        TextMessage text = new TextMessage(mapper.writeValueAsString(message));
        // A session must not be written to from two threads at once (WS threads + game timers).
        synchronized (session) {
            try {
                session.sendMessage(text);
            } catch (Exception e) {
                log.warn("Failed to send {} to session {}: {}", type, session.getId(), e.getMessage());
            }
        }
    }

    public void error(WebSocketSession session, String message) {
        send(session, "ERROR", Map.of("message", message));
    }
}

# UPRO Duel – backend

Spring Boot 4 (Java 21) WebSocket server for UPRO Duel. PostgreSQL schema is managed by Flyway
(`src/main/resources/db/migration`).

## Run locally

```bash
docker run -d --name upro-pg -e POSTGRES_USER=upro -e POSTGRES_PASSWORD=upro -e POSTGRES_DB=upro -p 5432:5432 postgres:17
./mvnw spring-boot:run
```

The WebSocket endpoint is `ws://localhost:8080/ws`.

## Configuration (environment variables)

| Variable | Default |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/upro` |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | `upro` / `upro` |
| `ALLOWED_ORIGINS` | `*` (comma-separated origin patterns) |
| `PORT` | `8080` |

## Protocol

Every message, in both directions, is `{"type": ..., "payload": {...}}`.

Client → server: `CONNECT {role: SCREEN}`, `VALIDATE_PIN {pin}`, `CONNECT {role: PLAYER, pin, username}`,
`ANSWER {answerId}`, `REJOIN {role, gameId, username?}`.

Server → client: `SCREEN_OK {pin}`, `LOBBY_STATE {players}`, `PIN_OK`, `JOIN_OK {username}`, `GAME_START {gameId}`,
`QUESTION {index, total, question, answers, duration, countdownMs, readingMs, answerMs, selectedAnswerId?}`,
`ROUND_RESULT {correctAnswerId, playerOne, playerTwo, scoreOne, scoreTwo}`, `GAME_END {playerOne, playerTwo, scoreOne, scoreTwo}`,
`REJOIN_OK {gameId}`, `ERROR {message}`.

The server drives the game: each question has a 5 s countdown, 5 s reading phase and the answering phase;
the round ends when both players answered or time ran out, and the next question follows 8 s later.

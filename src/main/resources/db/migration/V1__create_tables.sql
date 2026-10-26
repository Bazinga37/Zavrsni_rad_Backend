CREATE TABLE player (
    player_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username  VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE question (
    question_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    question    TEXT    NOT NULL,
    duration    INTEGER NOT NULL DEFAULT 15
);

CREATE TABLE answer (
    answer_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    question_id BIGINT       NOT NULL REFERENCES question ON DELETE CASCADE,
    content     VARCHAR(500) NOT NULL,
    is_correct  BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE game (
    game_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    player_one BIGINT      NOT NULL REFERENCES player,
    player_two BIGINT      NOT NULL REFERENCES player,
    score_one  INTEGER     NOT NULL DEFAULT 0,
    score_two  INTEGER     NOT NULL DEFAULT 0,
    finished   BOOLEAN     NOT NULL DEFAULT FALSE
);

-- Questions asked in a game, in order.
CREATE TABLE game_question (
    game_id     BIGINT  NOT NULL REFERENCES game ON DELETE CASCADE,
    question_id BIGINT  NOT NULL REFERENCES question,
    position    INTEGER NOT NULL,
    PRIMARY KEY (game_id, question_id),
    UNIQUE (game_id, position)
);

-- One row per player per question; answer_id is NULL when the player did not answer in time.
CREATE TABLE game_player_answer (
    game_id     BIGINT      NOT NULL,
    player_id   BIGINT      NOT NULL REFERENCES player,
    question_id BIGINT      NOT NULL,
    answer_id   BIGINT REFERENCES answer,
    response_ms INTEGER,
    points      INTEGER     NOT NULL DEFAULT 0,
    is_correct  BOOLEAN     NOT NULL DEFAULT FALSE,
    answered_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (game_id, player_id, question_id),
    FOREIGN KEY (game_id, question_id) REFERENCES game_question ON DELETE CASCADE
);

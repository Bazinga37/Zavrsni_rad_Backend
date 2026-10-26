package hr.fer.seminarbackend.game;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** All database access: questions are read, games and answers are recorded. */
@Repository
public class GameStore {
    private final JdbcClient jdbc;

    public GameStore(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Random questions, each with its answers in random order. */
    public List<Question> randomQuestions(int count) {
        record Row(long id, String text, int duration) {}
        List<Row> rows = jdbc.sql("SELECT question_id, question, duration FROM question ORDER BY random() LIMIT ?")
                .param(count)
                .query((rs, i) -> new Row(rs.getLong(1), rs.getString(2), rs.getInt(3)))
                .list();
        if (rows.isEmpty()) return List.of();

        Map<Long, List<Question.Answer>> answers = jdbc.sql(
                        "SELECT question_id, answer_id, content, is_correct FROM answer WHERE question_id IN (:ids)")
                .param("ids", rows.stream().map(Row::id).toList())
                .query((rs, i) -> Map.entry(rs.getLong(1),
                        new Question.Answer(rs.getLong(2), rs.getString(3), rs.getBoolean(4))))
                .list().stream()
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toCollection(ArrayList::new))));

        return rows.stream().map(r -> {
            List<Question.Answer> shuffled = answers.getOrDefault(r.id(), new ArrayList<>());
            Collections.shuffle(shuffled);
            return new Question(r.id(), r.text(), r.duration(), List.copyOf(shuffled));
        }).toList();
    }

    /** Usernames are case-insensitive, so they are stored lowercase. */
    public long getOrCreatePlayer(String username) {
        return jdbc.sql("""
                        INSERT INTO player (username) VALUES (?)
                        ON CONFLICT (username) DO UPDATE SET username = EXCLUDED.username
                        RETURNING player_id""")
                .param(username.toLowerCase())
                .query(Long.class).single();
    }

    @Transactional
    public long createGame(long playerOne, long playerTwo, List<Question> questions) {
        long gameId = jdbc.sql("INSERT INTO game (player_one, player_two) VALUES (?, ?) RETURNING game_id")
                .params(playerOne, playerTwo)
                .query(Long.class).single();
        for (int i = 0; i < questions.size(); i++) {
            jdbc.sql("INSERT INTO game_question (game_id, question_id, position) VALUES (?, ?, ?)")
                    .params(gameId, questions.get(i).id(), i + 1)
                    .update();
        }
        return gameId;
    }

    /** answerId and responseMs are null when the player did not answer in time. */
    public void saveAnswer(long gameId, long playerId, long questionId,
                           Long answerId, Integer responseMs, int points, boolean correct) {
        jdbc.sql("""
                        INSERT INTO game_player_answer
                            (game_id, player_id, question_id, answer_id, response_ms, points, is_correct)
                        VALUES (?, ?, ?, ?, ?, ?, ?)""")
                .params(gameId, playerId, questionId, answerId, responseMs, points, correct)
                .update();
    }

    public void finishGame(long gameId, int scoreOne, int scoreTwo) {
        jdbc.sql("UPDATE game SET score_one = ?, score_two = ?, finished = TRUE WHERE game_id = ?")
                .params(scoreOne, scoreTwo, gameId)
                .update();
    }
}

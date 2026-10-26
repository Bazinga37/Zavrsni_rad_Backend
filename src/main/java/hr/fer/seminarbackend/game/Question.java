package hr.fer.seminarbackend.game;

import java.util.List;

public record Question(long id, String text, int duration, List<Answer> answers) {

    public record Answer(long id, String content, boolean correct) {}

    public Answer answer(long answerId) {
        return answers.stream().filter(a -> a.id() == answerId).findFirst().orElse(null);
    }

    public long correctAnswerId() {
        return answers.stream().filter(Answer::correct).mapToLong(Answer::id).findFirst().orElse(-1);
    }
}

package dtu.group8.server.model;

public class QuizQuestion {
    private final String question;
    private final String answer;

    public QuizQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public boolean checkAnswer(String answer) {
        return (this.answer.trim().equalsIgnoreCase(answer.trim()));
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}

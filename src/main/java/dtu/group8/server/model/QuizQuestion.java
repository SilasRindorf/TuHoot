package dtu.group8.server.model;

public class QuizQuestion {
    private String question;
    private String answer;

    public QuizQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public boolean checkAnswer(String answer) {
        return (this.answer.trim().toLowerCase().equals(answer.trim().toLowerCase()));
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}

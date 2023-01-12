package dtu.group8.server.model;

public class QuizQuestion {
    private final String question;
    private final String answer;
    private int amountOfCorrectAnswers;

    public QuizQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.amountOfCorrectAnswers = 0;
    }

    public boolean checkAnswer(String answer) {
        boolean correctAnswer = this.answer.trim().equalsIgnoreCase(answer.trim());
        if (correctAnswer) {
            amountOfCorrectAnswers++;
        }
        return correctAnswer;
    }

    public int getAmountOfCorrectAnswers() {
        return amountOfCorrectAnswers;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}

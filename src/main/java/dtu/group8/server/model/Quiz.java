package dtu.group8.server.model;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Quiz {
    public ArrayList<QuizQuestion> questions = new ArrayList<QuizQuestion>();
    private QuizQuestion currentQuestion;

    public QuizQuestion getCurrentQuestion() {
        return currentQuestion;
    }

    public int quizSize() {
        return questions.size();
    }

    public String getQuestion(int index) {
        return questions.get(index).getQuestion();
    }

    public String getAnswer(int index) {
        return questions.get(index).getAnswer();
    }

    public boolean checkAnswer(int index, String answer) {
        return questions.get(index).checkAnswer(answer);
    }

    public void selectRandomQuestion() {
        if (questions.size() == 0) {
            return;
        }
        int randomNum = ThreadLocalRandom.current().nextInt(0, questions.size());
        currentQuestion = questions.get(randomNum);
        questions.remove(randomNum);
    }

}



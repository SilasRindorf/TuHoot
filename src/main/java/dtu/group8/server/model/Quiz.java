package dtu.group8.server.model;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Quiz {
    public ArrayList<QuizQuestion> questions = new ArrayList<QuizQuestion>();

    public QuizQuestion getCurrentQuestion() {
        return currentQuestion;
    }

    private QuizQuestion currentQuestion;

    public void selectRandomQuestion(){
        int randomNum = ThreadLocalRandom.current().nextInt(0, questions.size());
        currentQuestion = questions.get(randomNum);
        questions.remove(randomNum);
    }

}



package dtu.group8.server.model;

import java.util.ArrayList;

public class Quiz {
    public ArrayList<QuizQuestion> questions = new ArrayList<QuizQuestion>();

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

    public int getAmountOfCorrectAnswers(int index) {
        return questions.get(index).getAmountOfCorrectAnswers();
    }



    /*public void selectRandomQuestion(){
        if (questions.size() == 0){
            return;
        }
        int randomNum = ThreadLocalRandom.current().nextInt(0, questions.size());
        currentQuestion = questions.get(randomNum);
        questions.remove(randomNum);
    }*/

}



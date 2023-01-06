package dtu.group8.server.model;

import java.util.ArrayList;

public class Quiz {
    public ArrayList<QuizQuestion> questions = new ArrayList<QuizQuestion>();


    public void addSomeRandomQuizzes() {
        QuizQuestion q1 = new QuizQuestion("How many days is a leap year", "366");
        this.questions.add(q1);
    }

}


class QuizQuestion{
     String question;
     String answer;

    public QuizQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public boolean checkAnswer(String answer) {
        return (this.answer.equals(answer.trim().toLowerCase()));
    }
}
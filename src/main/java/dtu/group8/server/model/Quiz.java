package dtu.group8.server.model;

import java.util.ArrayList;

public class Quiz {
    public ArrayList<QuizQuestion> questions;
    public Quiz() {
        this.questions = new ArrayList<QuizQuestion>();
    }

}


class QuizQuestion{
    String question;
    String answer;
    public boolean checkAnswer(String answer) {
        return (this.answer.equals(answer.trim().toLowerCase()));
    }
}
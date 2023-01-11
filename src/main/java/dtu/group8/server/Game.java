package dtu.group8.server;

import dtu.group8.server.model.Player;
import dtu.group8.server.model.Quiz;
import dtu.group8.server.model.QuizQuestion;
import dtu.group8.util.Printer;

import java.util.ArrayList;
import java.util.Objects;

public class Game {
    private ArrayList<Player> players;
    private Quiz quiz;
    public Game() {
        quiz = new Quiz();
        quiz.questions.add(new QuizQuestion("2+2","5"));
        quiz.questions.add(new QuizQuestion("apples","bananas"));
        players = new ArrayList<>();
    }
    public Game(Quiz quiz) {
        this.quiz = quiz;
    }



    public void addPlayer(String playerId) {
        boolean isFound = false;
        for (Player currPlayer : players) {
            if (Objects.equals(currPlayer.getId(), playerId)) {
                isFound = true;
            }
        }

        if (!isFound) {
            players.add(new Player(playerId));
        }

    }
    public void removePlayer(String playerId) {

    }

    public int quizSize(){
        return quiz.quizSize();
    }

    public String getQuestion(int index){
        return quiz.getQuestion(index);
    }

    public String getAnswer(int index){
        return quiz.getAnswer(index);
    }

    public boolean checkAnswer(int index, String answer){
        return quiz.checkAnswer(index,answer);
    }
    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void selectNewQuestion(){
        quiz.selectRandomQuestion();
    }
    public String getCurrentQuestion() {
        return quiz.getCurrentQuestion().getQuestion();
    }
    public String getCurrentAnswer(){
        return quiz.getCurrentQuestion().getAnswer();
    }

    public boolean checkAnswer(String answer){
        return quiz.getCurrentQuestion().checkAnswer(answer);
    }

    public void printOutPlayers() {
        for (Player player : players) {
            new Printer().println("PlayerId: " + player.getId() + ", PlayerPoint: " + player.getPoint());
        }
    }



}

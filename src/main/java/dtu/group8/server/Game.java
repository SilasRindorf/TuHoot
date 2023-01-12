package dtu.group8.server;

import dtu.group8.server.model.Player;
import dtu.group8.server.model.Quiz;
import dtu.group8.server.model.QuizQuestion;
import dtu.group8.util.Printer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.StringTokenizer;

public class Game {
    private ArrayList<Player> players;
    private Quiz quiz;
    public Game() {
        quiz = new Quiz();
        quiz.questions.add(new QuizQuestion("2+2", "5"));
        quiz.questions.add(new QuizQuestion("apples", "bananas"));
        players = new ArrayList<>();
    }
    public Game(Quiz quiz) {
        this.quiz = quiz;
    }


    public void addPlayer(String name, String playerId) {
        boolean isFound = false;
        for (Player currPlayer : players) {
            if (Objects.equals(currPlayer.getId(), playerId)) {
                isFound = true;
            }
        }

        if (!isFound) {
            players.add(new Player(playerId, name));
        }

    }
    public void removePlayer(String playerId) {

    }

    public int quizSize() {
        return quiz.quizSize();
    }

    public String getQuestion(int index) {
        return quiz.getQuestion(index);
    }

    public String getAnswer(int index) {
        return quiz.getAnswer(index);
    }

    public boolean checkAnswer(int index, String answer, String id) {
        boolean isCorrect = quiz.checkAnswer(index, answer);
        // Adds point if players answers correctly
        for (Player player1 : players) {
            if (player1.getId().equals(id) && isCorrect) {
                player1.setPoint(calculatePoints(index));
            }
        }
        return isCorrect;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public boolean allAnsweredCorrect(int index) {
        return quiz.getAmountOfCorrectAnswers(index) == players.size();
    }


    public void printOutPlayers() {
        for (Player player : players) {
            new Printer().println("PlayerId: " + player.getId() + ", PlayerPoint: " + player.getPoint());
        }
    }

    public String getScores(){
        StringBuilder builder = new StringBuilder();
        builder.append("Highest scores:");
        builder.append("\tName\tScore");
        Collections.sort(players);
        for (Player player :
                players) {
            builder.append("\n\t" + player.getName() + "\t");
        }
        return builder.toString();
    }



}
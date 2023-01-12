package dtu.group8.server;

import dtu.group8.server.model.Player;
import dtu.group8.server.model.Quiz;
import dtu.group8.server.model.QuizQuestion;
import dtu.group8.util.Printer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Game {
    private ArrayList<Player> players;
    private final Quiz quiz;

    public Game() {
        quiz = new Quiz();
        quiz.questions.add(new QuizQuestion("2+2", "5"));
        quiz.questions.add(new QuizQuestion("apples", "bananas"));
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
                break;
            }
        }

        if (!isFound) {
            players.add(new Player(playerId));
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
        // Adds point if players answers correctly
        Player player = new Player(id);
        for (Player player1 : players) {
            if (player1.getId().equals(player.getId()) && quiz.checkAnswer(index, answer)) {
                player1.setPoint(500);
            }
        }
        return quiz.checkAnswer(index, answer);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void selectNewQuestion() {
        quiz.selectRandomQuestion();
    }

    public String getCurrentQuestion() {
        return quiz.getCurrentQuestion().getQuestion();
    }

    public String getCurrentAnswer() {
        return quiz.getCurrentQuestion().getAnswer();
    }

    public boolean checkAnswer(String answer) {
        return quiz.getCurrentQuestion().checkAnswer(answer);
    }

    public void printOutPlayers() {
        for (Player player : players) {
            new Printer().println("PlayerId: " + player.getId() + ", PlayerPoint: " + player.getPoint());
        }
    }

    public String getScores() {
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

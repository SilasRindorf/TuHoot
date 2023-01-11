package dtu.group8.server;

import dtu.group8.server.model.Player;
import dtu.group8.server.model.Quiz;
import dtu.group8.server.model.QuizQuestion;
import dtu.group8.util.Printer;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class Game {
    private String id;
    private String name;
    private String host;
    private Player player;
    private ArrayList<Player> players;
    private RemoteSpace remoteSpace;
    private Space space;

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

    public Game(String id, String name, String host, Player player,
                ArrayList<Player> players, RemoteSpace remoteSpace, Space space) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.player = player;
        this.players = players;
        this.remoteSpace = remoteSpace;
        this.space = space;
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

}

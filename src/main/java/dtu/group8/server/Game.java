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
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class Game {
    private String id;
    private String name;
    private String hostId;
    private String hostName;
    private Player me;
    private ArrayList<Player> players;
    private final Quiz quiz;
    private boolean amIHost;

    private RemoteSpace remoteSpace;
    private Space space;
    private Semaphore printerLock = new Semaphore(1);
    Printer printer = new Printer("Game:", Printer.PrintColor.WHITE);


    public Game() {
        quiz = new Quiz();
        quiz.questions.add(new QuizQuestion("2+2", "5"));
        quiz.questions.add(new QuizQuestion("apples", "bananas"));
        players = new ArrayList<>();
    }

    public Game(Quiz quiz) {
        this.quiz = quiz;
    }

    public Game(String id, String name, String hostId, Player me,
                ArrayList<Player> players, RemoteSpace remoteSpace, Space space) {
        this.id = id;
        this.name = name;
        this.hostId = hostId;
        this.me = me;
        this.players = players;
        this.remoteSpace = remoteSpace;
        this.space = space;
        this.quiz = new Quiz();
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

    public void addPlayer(Player player) {
        for (Player currP : this.players) {
            if (currP.getId().equals(player.getId())) {
                return;
            }
        }
        players.add(player);
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

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public Quiz getQuiz() {
        return quiz;
    }


    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Player getMe() {
        return me;
    }

    public void setMe(Player me) {
        this.me = me;
    }

    public RemoteSpace getRemoteSpace() {
        return remoteSpace;
    }

    public void setRemoteSpace(RemoteSpace remoteSpace) {
        this.remoteSpace = remoteSpace;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }


    public Semaphore getPrinterLock() {
        return printerLock;
    }

    public void display_size_of_added_player() {
        Printer printer = new Printer("", Printer.PrintColor.WHITE);
        printer.println("Total players: " + this.players.size());
    }

    /*----------------------------Global user input method-----------------------------*/
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    private int readerLock = 0;
    private Semaphore semaphore = new Semaphore(1);
    private String userInput;

    public String takeUserInput() {

        try {
            if (readerLock == 1) {
                semaphore.acquire();
                semaphore.release();
                return userInput;
            }

            readerLock = 1;
            semaphore.acquire();
            userInput = input.readLine();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        readerLock = 0;
        if (semaphore.availablePermits() == 0) semaphore.release();
        return userInput;
    }

    public boolean amIHost() {
        return this.getMe().getId().equals(this.getHostId());
    }
}

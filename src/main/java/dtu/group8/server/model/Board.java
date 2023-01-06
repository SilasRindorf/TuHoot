package dtu.group8.server.model;

import java.util.ArrayList;
import java.util.Objects;

public class Board {
    private ArrayList<Player> players;
    private Quiz quizzes;



    public Board(ArrayList<Player> players, Quiz quizzes) {

        this.players = players;
        //players = new ArrayList<Player>();
        if (quizzes == null) {
            quizzes = new Quiz();
            this.quizzes.addSomeRandomQuizzes();
        }
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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void printOutPlayers() {
        for (Player player : players) {
            System.out.println("PlayerId: " + player.getId() + ", PlayerPoint: " + player.getPoint());
        }
    }


}

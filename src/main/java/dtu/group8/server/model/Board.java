package dtu.group8.server.model;

import java.util.ArrayList;
import java.util.Objects;

public class Board {
    ArrayList<Player> players;
    Quiz quiz;



    public Board() {
        players = new ArrayList<Player>();
        quiz = new Quiz();
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

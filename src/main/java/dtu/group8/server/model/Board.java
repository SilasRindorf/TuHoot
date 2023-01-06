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
            if (Objects.equals(currPlayer.id, playerId)) {
                isFound = true;
            }
        }

        if (!isFound) {
            players.add(new Player(playerId));
        }

    }


    public void removePlayer(String playerId) {

    }
}

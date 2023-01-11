package dtu.group8.lobby.data_class;

import java.util.ArrayList;

public class Game {
    private String name;
    private String id;
    private Player hostPlayer;
    //private ArrayList<String> playerIds;
    private ArrayList<Player> players;

    public Game(String name, String id, Player hostPlayer, ArrayList<Player> players) {
        this.name = name;
        this.id = id;
        this.hostPlayer = hostPlayer;
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Player getHostPlayer() {
        return hostPlayer;
    }

    public void setHostPlayer(Player hostPlayer) {
        this.hostPlayer = hostPlayer;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        for (Player currP : this.players) {
            if (currP.getId().equals(player.getId())) {
                return;
            }
        }
        players.add(player);
    }

}



package dtu.group8.lobby.data_class;

import java.util.ArrayList;

public class Board {
    private String name;
    private String id;
    private ArrayList<String> playerIds;

    public Board(String name, String id, ArrayList<String> playerIds) {
        this.name = name;
        this.id = id;
        this.playerIds = playerIds;
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

    public ArrayList<String> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(ArrayList<String> playerIds) {
        this.playerIds = playerIds;
    }
}

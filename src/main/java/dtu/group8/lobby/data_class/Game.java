package dtu.group8.lobby.data_class;

import java.util.ArrayList;

public class Game {
    private String name;
    private String id;
    private String hostId;
    private ArrayList<String> playerIds;

    public Game(String name, String id, String hostId, ArrayList<String> playerIds) {
        this.name = name;
        this.id = id;
        this.hostId = hostId;
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

    public String getHostId() {
        return hostId;
    }

/*    public void setHostId(String hostId) {
        if (!playerIds.contains(hostId)) {
            playerIds.add(hostId)
        }
        this.hostId = hostId;
    }*/
}

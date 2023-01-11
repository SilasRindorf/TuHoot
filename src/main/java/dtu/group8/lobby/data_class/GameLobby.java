package dtu.group8.lobby.data_class;

import java.util.ArrayList;

public class GameLobby {
    private String name;
    private String id;
    private PlayerLobby hostPlayerLobby;
    private ArrayList<String> playerIds;
    private ArrayList<String> playerNames;

    public GameLobby(String name, String id, PlayerLobby hostPlayerLobby) {
        this.name = name;
        this.id = id;
        this.hostPlayerLobby = hostPlayerLobby;
        this.playerIds = new ArrayList<>();
        this.playerNames = new ArrayList<>();
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

    public PlayerLobby getHostPlayer() {
        return hostPlayerLobby;
    }

    public void setHostPlayer(PlayerLobby hostPlayerLobby) {
        this.hostPlayerLobby = hostPlayerLobby;
    }



    public void addPlayer(String playerName, String playerId) {
        if (!playerIds.contains(playerId)) {
            playerNames.add(playerName);
            playerIds.add(playerId);
        }
    }

    public ArrayList<String> getPlayerIds() {
        return playerIds;
    }

    public ArrayList<String> getPlayerNames() {
        return playerNames;
    }
}



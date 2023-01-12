package dtu.group8.lobby;

import dtu.group8.lobby.data_class.GameLobby;
import dtu.group8.lobby.data_class.PlayerLobby;
import dtu.group8.server.Game;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static dtu.group8.lobby.Util.*;

public class LobbyServer {
    private ArrayList<GameLobby> gameList = new ArrayList<>();
    Semaphore semaphore = new Semaphore(1);
    private SpaceRepository repository;
    private SequentialSpace spaceLobby;
    private BufferedReader input;
    Integer spaceCounter = 0;

    public static void main(String[] args) {

        LobbyServer lobbyServer = new LobbyServer();
        lobbyServer.startServer();
    }

    public void startServer() {

        input = new BufferedReader(new InputStreamReader(System.in));
        repository = new SpaceRepository();
        spaceLobby = new SequentialSpace();
        repository.add("lobby", spaceLobby);


        try {

            // Set the URI of the chat space
            System.out.print("Enter URI of the chat server or press enter for default: ");
            String uri = input.readLine();

            if (uri.isEmpty()) {
                //uri = "tcp://localhost:9002/?keep";
                uri = getUri("");
            }

            // Open a gate
            System.out.println("Opening repository gate at " + uri + "...");
            repository.addGate(uri);
            // --------------------------- waiting for requests -----------------------
            listen_for_available_gameList_req_from_client.start();
            listen_for_add_player_req_from_client.start();
            listen_for_add_player_response_from_host.start();



            while (true) {
                Object[] createBoardObj = spaceLobby.get(new ActualField(CREATE_GAME_REQ), new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class));
                System.out.println("LobbyServer: Creating board...");
                String gameName = createBoardObj[1].toString();
                String hostId = createBoardObj[2].toString();
                String hostName = createBoardObj[3].toString();

                spaceCounter++;
                String gameId = "gameId" + spaceCounter;  // gameId/boardId/spaceId
                GameLobby newGameLobby = new GameLobby(gameName, gameId, new PlayerLobby(hostName, hostId));
                newGameLobby.addPlayer(hostName, hostId);
                semaphore.acquire();
                this.gameList.add(newGameLobby);
                semaphore.release();
                SequentialSpace newSpace = new SequentialSpace();
                repository.add(gameId, newSpace);
                // getPlayerNames and getPlayerIds contains only the host, for now.
                newSpace.put(ALL_PLAYERS, newGameLobby.getPlayerNames(), newGameLobby.getPlayerIds());
                String receiverId = hostId; // just not to mix up.
                spaceLobby.put(MY_SPACE_ID, receiverId, gameName, gameId, hostName, hostId);
                System.out.println("LobbyServer: Game created");
                System.out.println("\tGame name: " + gameName);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUri(String parameter) {
        return  "tcp://" + IP + ":" + PORT + "/" + parameter + TYPE;
    }



    Thread listen_for_available_gameList_req_from_client = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    Object[] obj = spaceLobby.get(new ActualField(SHOW_ME_AVAILABLE_GAMES_REQ), new FormalField(String.class));
                    System.out.println("LobbyServer: Request for game-list received from: " + obj[1]);

                    ArrayList<String> tempGames = new ArrayList<>();
                    semaphore.acquire();
                    for (GameLobby gameLobby : gameList) {
                        tempGames.add(gameLobby.getName() + PATTERN_FOR_PLAYER_ID_SPLITTER + gameLobby.getId());
                    }
                    semaphore.release();
                    spaceLobby.put(SHOW_ME_AVAILABLE_GAMES_RES, obj[1].toString(), tempGames);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });



    Thread listen_for_add_player_req_from_client = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    Object[] addMeObj = spaceLobby.get(new ActualField(ADD_ME_REQ_FROM_CLIENT), new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class));
                    String playerName = addMeObj[1].toString();
                    String playerId = addMeObj[2].toString();
                    String boardId = addMeObj[3].toString();
                    System.out.println("LobbyServer: Add to game req: Received from client: " + playerId);

                    semaphore.acquire();
                    for (GameLobby currGame : gameList) {
                        if (currGame.getId().equals(boardId)) {
                            // Sends add request to the host
                            String hostId = currGame.getHostPlayer().getId();
                            spaceLobby.put(JOINT_REQ_FROM_SERVER, hostId, playerName, playerId);
                            System.out.println("LobbyServer: Add to game req: Req sent to host: ");
                            break;
                        }
                    }
                    semaphore.release();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });



    Thread listen_for_add_player_response_from_host = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    Object[] obj = spaceLobby.get(new ActualField(JOINT_RES_FROM_HOST), new FormalField(String.class),
                            new FormalField(String.class), new FormalField(String.class), new FormalField(String.class),
                            new FormalField(String.class), new FormalField(String.class));

                    String gameName = obj[1].toString();
                    String gameId = obj[2].toString();
                    String hostName = obj[3].toString();
                    String hostId = obj[4].toString();
                    String addedClientName = obj[5].toString();
                    String addedClientId = obj[6].toString();

                    System.out.println(hostName + " accepted " + addedClientName);

                    semaphore.acquire();
                    // Adding the new player to the belonging game has no effect for now. The full game list already exists in the private space.
                    for (GameLobby currGame : gameList) {
                        if (gameId.equals(currGame.getId())) {
                            currGame.addPlayer(addedClientName, addedClientId);

                            // Sending response back to client
                            //String hostPlayerId = currGame.getHostPlayer().getId();
                            String receiverId = addedClientId; // just not to mix up.
                            spaceLobby.put(MY_SPACE_ID, receiverId, gameName, gameId, hostName, hostId);
                            System.out.println("LobbyServer: sent gameId/spaceId to client");
                            break;
                        }
                    }
                    semaphore.release();

                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });






}





















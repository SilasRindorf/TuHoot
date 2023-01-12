package dtu.group8.lobby;

import dtu.group8.lobby.data_class.GameLobby;
import dtu.group8.lobby.data_class.PlayerLobby;
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
                newSpace.put(ALL_PLAYERS, newGameLobby.getPlayerNames(), newGameLobby.getPlayerIds());
                spaceLobby.put(MY_SPACE_ID, hostId, gameId, gameName);
                System.out.println("LobbyServer: Game created");
                System.out.println("\tGame name: " + gameName);






/*
                Printer log = new Printer();
                System.out.println("Creating board...");
                //Get all clients from lobby
                LinkedList<Object[]> allClients = spaceLobby.getAll(new ActualField("lobby"), new FormalField(String.class), new FormalField(String.class));
                //id++
                SequentialSpace newSpace = new SequentialSpace();
                repository.add(newSpaceId, newSpace);


                //Not used yet
                newSpace.put(LOCK_FOR_GAME_START);


                String[] playerNames = new String[allClients.size()];
                String[] playerIds = new String[allClients.size()];
                int counter = 0;

                // Info print
                System.out.println("\tboardId: " + spaceCounter);
                System.out.println("\tClients");
                for (Object[] client : allClients) {
                    String pName = client[1].toString();
                    String pid = client[2].toString();

                    System.out.println("\t\tClient: " + pid);

                    //Send info to clients
                    spaceLobby.put(pid, newSpaceId);
                    playerNames[counter] = pName;
                    playerIds[counter] = pid;
                    counter++;
                }

                newSpace.put("allMembers", playerNames, playerIds);
*/

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
                            new FormalField(String.class), new FormalField(String.class));
                    String gameId = obj[1].toString();
                    String playerName = obj[2].toString();
                    String playerId = obj[3].toString();
                    System.out.println(playerId);



                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });


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



}





















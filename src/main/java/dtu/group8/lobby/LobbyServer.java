package dtu.group8.lobby;

import dtu.group8.lobby.data_class.Board;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.SimpleTimeZone;

public class LobbyServer {
    static final String CREATE_BOARD = "create board";
    private static final String PORT = "9002";
    //private static final String LOCALHOST = "10.209.95.114";
    private static final String IP = "localhost";

    private static final String TYPE = "?keep";

    private static final String LOCK_FOR_GAME_START = "lockForGameStart";
    private ArrayList<Board> boards = new ArrayList<>();
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
            //Lock for starting a game
            spaceLobby.put("createBoardLock");

            while (true) {
                Object[] createBoardObj = spaceLobby.get(new ActualField(CREATE_BOARD), new FormalField(Object.class), new FormalField(Object.class));
                System.out.println("Creating board...");
                String boardName = createBoardObj[1].toString();
                String hostId = createBoardObj[2].toString();
                spaceCounter++;
                String boardId = "boardId" + spaceCounter;  // boardId/spaceId
                ArrayList<String> clientIds = new ArrayList<>();
                clientIds.add(hostId);
                Board newBoard = new Board(boardName, boardId, hostId, clientIds);
                this.boards.add(newBoard);
                SequentialSpace newSpace = new SequentialSpace();
                repository.add(boardId, newSpace);
                newSpace.put("allPlayers", boardId, newBoard.getPlayerIds());

                spaceLobby.put("mySpaceId", hostId, boardId, boardName);
                System.out.println("Board created");
                System.out.println("\tBoard name: " + boardName);






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

    Thread listenForAddPlayer = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    Object[] addMeObj = spaceLobby.get(new ActualField("addMe"), new FormalField(Object.class), new FormalField(Object.class));
                    String playerId = addMeObj[1].toString();
                    String boardId = addMeObj[2].toString();


                }


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });
}





















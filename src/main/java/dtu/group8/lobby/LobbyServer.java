package dtu.group8.lobby;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class LobbyServer {
    static final String CREATE_BOARD = "create board";
    private static final String PORT = "9002";
    private static final String LOCALHOST = "localhost";
    private static final String TYPE = "?keep";

    Integer spaceCounter = 0;

    public static void main(String[] args) {

        LobbyServer lobbyServer = new LobbyServer();
        lobbyServer.startServer();
    }

    public void startServer() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            SpaceRepository repository = new SpaceRepository();
            SequentialSpace spaceLobby = new SequentialSpace();
            repository.add("lobby", spaceLobby);
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


            while (true) {
                spaceLobby.get(new ActualField(CREATE_BOARD));
                LinkedList<Object[]> obj = spaceLobby.getAll(new ActualField("lobby"), new FormalField(String.class), new FormalField(String.class));

                spaceCounter++;
                SequentialSpace newSpace = new SequentialSpace();
                String newSpaceId = "boardId" + spaceCounter;
                repository.add(newSpaceId, newSpace);
                //newSpace.put("Hello");

                for (Object[] client : obj) {
                    spaceLobby.put(client[2], newSpaceId);
                    System.out.println(client[2]);
                }


            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUri(String parameter) {
        return  "tcp://" + LOCALHOST + ":" + PORT + "/" + parameter + TYPE;
    }
}





















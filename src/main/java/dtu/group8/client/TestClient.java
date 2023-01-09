package dtu.group8.client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class TestClient {
    private static final String CREATE_BOARD = "create board";
    private static final String PORT = "9002";
    private static final String LOCALHOST = "localhost";
    private static final String TYPE = "?keep";

    public static void main(String[] args) {
        TestClient client = new TestClient();
        client.start();
        System.exit(0);
    }

    public void start() {
        try {

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            // Set the URI of the chat space
            System.out.print("Enter URI of the chat server or press enter for default: ");
            String uri = input.readLine();

            // Default value
            if (uri.isEmpty()) {
                //uri = "tcp://" + LOCALHOST + ":" + PORT + "/lobby" + TYPE;
                uri = getUri("lobby");
            }

            // Connect to the remote chat space
            System.out.println("Connecting to chat space " + uri + "...");
            RemoteSpace remoteSpace = new RemoteSpace(uri);

            // Read client name from the console
            System.out.print("Enter your name: ");
            String name = input.readLine();

            String clientID = UUID.randomUUID().toString();
            remoteSpace.put("lobby", name, clientID);


            String userInput = "";
            while (!userInput.equalsIgnoreCase("exit")) {
                userInput = input.readLine();
                if (userInput.equalsIgnoreCase(CREATE_BOARD)){
                    remoteSpace.put(CREATE_BOARD);
                }
            }
            System.out.println("Loop exit");

            Object[] obj = remoteSpace.get(new ActualField(clientID), new FormalField(String.class));
            String spaceId = obj[1].toString();
            String uri2 = "tcp://" + LOCALHOST + ":" + PORT + "/" + spaceId + TYPE;
            RemoteSpace space = new RemoteSpace(uri2);

            space.get(new ActualField("Hello"));

            System.out.println("Hello received");


        } catch (
                IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getUri(String parameter) {
         return  "tcp://" + LOCALHOST + ":" + PORT + "/" + parameter + TYPE;
    }


}

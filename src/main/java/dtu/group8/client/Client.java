package dtu.group8.client;

import dtu.group8.server.ClientServer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * Client
 * Responsibilities:
 * Must have:
 *      Receive questions
 *      Send answers
 *      Receive correct answer
 *      Show if answer was correct or wrong
 * Can have:
 *      See opponent points
 *      See timer
 *      See amount of remaining questions
 *      Reconnect to game
 */
public class Client {
    // Port of server
    private final String PORT = "9002";
    // localhost
    //private final String IP = "localhost";
    private static final String IP = "10.209.127.138";

    private static final String TYPE = "?keep";
    private String name = "";
    private BufferedReader input;
    private boolean amIHost = false;

    public Space matchMake(){
        boolean isBoardCreated = false;
        try {

            input = new BufferedReader(new InputStreamReader(System.in));
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
            name = input.readLine();

            String clientID = UUID.randomUUID().toString();
            remoteSpace.put("lobby", name, clientID);
            ClientLoop looper = new ClientLoop(remoteSpace);
            Thread thread = new Thread(looper);
            thread.start();

            Object[] obj = remoteSpace.get(new ActualField(clientID), new FormalField(String.class));
            if (thread.isAlive()) {
                looper.setAlive(false);
            }
            System.out.println("Game starting soon...");

            String spaceId = obj[1].toString();
            String uri2 = "tcp://" + IP + ":" + PORT + "/" + spaceId + TYPE;

            Space newSpace = new RemoteSpace(uri2);

            return newSpace;
        } catch (
                IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }
    public void start(Space server) {
        //____________________________________ SETUP ____________________________________
        if (server == null){
            return;
        }
        try {
            if (input == null){
                input = new BufferedReader(new InputStreamReader(System.in));
            }

            new Thread(new ThreadStartGame(server)).start();

            // Generate random client ID
            String clientID = String.valueOf(Math.random());
            // Connect to server
            server.put("add",name,clientID);
            // Get ack from server
            Object[] t = server.get(new ActualField(clientID),new FormalField(String.class));
            if (!t[1].equals("ok")){
                System.out.println("Server did not ack... returning");
                return;
            }
            //Wait for server to start
            System.out.println("Waiting for server to start");
            //Get game state
            t = server.query(new ActualField("gameState"), new FormalField(Integer.class));
            if (((Integer) t[1] == 1)){
                System.out.println("Starting game...");

            } else {
                System.out.println("Failed to start game");
            }
            //____________________________________ STARTING GAME ____________________________________
            System.out.println("playing game!");
            while (!((Integer) t[1] == 0)) {
                // Answer server ack
                t = server.query(new FormalField(Integer.class));
                if ((Integer) t[1] == 3 ){
                    server.put( clientID, "ok");
                }
                //____________________________________ RECEIVE QUESTION ____________________________________
                System.out.println("Question coming up");

                //Questionable stuff starts now
                //Get question from server and print to console
                t = server.query(new ActualField("Q"), new FormalField(String.class));
                System.out.println("Question: " + t[1]);
                //____________________________________ ANSWER ____________________________________
                Object[] gameState = server.queryp(new ActualField("gameState"),new FormalField(Integer.class));
                while ((int) gameState[1] == 4){
                    server.queryp(new ActualField("gameState"),new FormalField(String.class));
                }
                //Get answer and send to server
                server.put(clientID, input.readLine());
                //Get actual answer from server
                t = server.query(new ActualField("V"),new FormalField(String.class),new FormalField(Boolean.class));
                if ((boolean) t[2]){
                    System.out.println("You got the answer correct!");
                } else {
                    System.out.println("Wrong answer!");
                }

                //Should check if client already supplied correct answer
                System.out.println("Answer was " + t[2]);
                //Questionable coding ends.... maybe

                //Check game state
                t = server.query(new ActualField("gameState"), new FormalField(Integer.class));
            }
            //____________________________________ GAME END ____________________________________
            System.out.println("Stopping game...");
            //____________________________________ EXCEPTION HANDLING ____________________________________
        } catch (
                IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getUri(String parameter) {
        return  "tcp://" + IP + ":" + PORT + "/" + parameter + TYPE;
    }

}


package dtu.group8.client;

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
    private final String LOCALHOST = "127.0.0.1";

    private static final String TYPE = "?keep";
    private String name = "";
    private BufferedReader input;
    public Space matchMake(){
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


            String userInput = "";
            while (true) {
                userInput = input.readLine();
                if (userInput.equalsIgnoreCase("create board")){
                    remoteSpace.put("create board");
                    break;
                } else if (userInput.equalsIgnoreCase("join board")){
                    break;
                } else if (userInput.equalsIgnoreCase("exit")){
                    return null;
                }
            }
            System.out.println("Loop exit");

            Object[] obj = remoteSpace.get(new ActualField(clientID), new FormalField(String.class));
            String spaceId = obj[1].toString();
            String uri2 = "tcp://" + LOCALHOST + ":" + PORT + "/" + spaceId + TYPE;
            return new RemoteSpace(uri2);
        } catch (
                IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }
    public void start(Space server) {
        if (server == null){
            return;
        }
        try {
            if (input == null){
                input = new BufferedReader(new InputStreamReader(System.in));
            }


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
            t = server.query(new FormalField(Integer.class));
            if (((Integer) t[1] == 1)){
                System.out.println("Starting game...");

            } else {
                System.out.println("Failed to start game");
            }
            while (!((Integer) t[1] == 0)){
                System.out.println("playing game");
                // Answer server ack
                t = server.query(new FormalField(Integer.class));
                if ((Integer) t[1] == 3 ){
                    server.put(clientID,"ok");
                }

                //Questionable stuff starts now
                //Get question from server and print to console
                t = server.query(new ActualField("Q"), new FormalField(String.class));
                System.out.println("Question: " + t[2]);
                //Get answer and send to server
                server.put(clientID,input.readLine());
                //Get actual answer from server
                t = server.query(new ActualField("A"),new FormalField(String.class));
                //Should check if client already supplied correct answer
                System.out.println("Correct answer was " + t[3]);

                //Sleep because nap time
                Thread.sleep(2000);

                //Questionable coding ends.... maybe

                //Check game state
                t = server.query(new FormalField(Integer.class));
            }
            System.out.println("Stopping game...");

        } catch (
                IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getUri(String parameter) {
        return  "tcp://" + LOCALHOST + ":" + PORT + "/" + parameter + TYPE;
    }

}


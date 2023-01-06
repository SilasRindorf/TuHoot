package dtu.group8.client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

/**
 * Client
 * Responsibilities:
 * Must have:
 *      Receive questions
 *      Receive answer options
 *      Send answers
 * Can have:
 *      See opponent points
 *      See timer
 *      See amount of remaining questions
 *      Reconnect to game
 */
public class Client {
    // Port of server
    private final String port = "9002";
    // localhost
    private final String serverIP = "127.0.0.1:" + port;
    // DTUsecure server
    private final String temp = "10.209.95.114:" + port;
    public void start() {
        try {

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            // Set the URI of the chat space
            // Default value
            System.out.print("Enter URI of the chat server or press enter for default: ");
            String uri = input.readLine();
            // Default value
            if (uri.isEmpty()) {
                uri = "tcp://"+ temp + "/chat?keep";
            }

            // Connect to the remote chat space
            System.out.println("Connecting to chat space " + uri + "...");
            RemoteSpace chat = new RemoteSpace(uri);

            // Read client name from the console
            System.out.print("Enter your name: ");
            String name = input.readLine();

            // Read game id from the console
            System.out.print("Enter game ID: ");
            String gameID = input.readLine();
            // Generate random client ID
            String clientID = String.valueOf(Math.random());
            // Connect to server
            chat.put("add",gameID,name,clientID);
            // Get ack from server
            Object[] t = chat.get(new ActualField(clientID),new FormalField(String.class));
            if (!t[1].equals("ok")){
                System.out.println("Server did not ack... returning");
                return;
            }
            //Wait for server to start
            System.out.println("Waiting for server to start");
            //Get game state
            t = chat.query(new ActualField(gameID),new FormalField(Integer.class));
            if (((Integer) t[1] == 1)){
                System.out.println("Starting game...");

            } else {
                System.out.println("Failed to start game");
            }
            while (((Integer) t[1] == 1)){
                System.out.println("playing game");
                Thread.sleep(2000);

                //Check game state
                t = chat.query(new ActualField(gameID),new FormalField(Integer.class));
            }
            System.out.println("Stopping game...");

        } catch (
                IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


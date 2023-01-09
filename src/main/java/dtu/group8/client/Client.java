package dtu.group8.client;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;

/**
 * Client
 * Responsibilities:
 * Must have:
 * Receive questions
 * Send answers
 * Receive correct answer
 * Show if answer was correct or wrong
 * Can have:
 * See opponent points
 * See timer
 * See amount of remaining questions
 * Reconnect to game
 */
public class Client {
    // Port of server
    private final String port = "9002";
    // localhost
    private final String localhost = "127.0.0.1:" + port;
    // DTUsecure server
    private final String DTUsecure = "10.209.95.114:" + port;

    public void start() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            // Set the URI of the chat space
            System.out.print("Enter URI of the chat server or press enter for default: ");
            String uri = input.readLine();
            // Default value
            if (uri.isEmpty()) {
                uri = "tcp://" + localhost + "/chat?keep";
            }

            // Connect to the remote chat space
            System.out.println("Connecting to chat space " + uri + "...");
            RemoteSpace chat;
            try {

                chat = new RemoteSpace(uri);
            } catch (ConnectException e) {
                e.printStackTrace();
                return;
            }

            // Read client name from the console
            System.out.print("Enter your name: ");
            String name = input.readLine();

            // Read game id from the console
            System.out.print("Enter game ID: ");
            String gameID = input.readLine();
            // Generate random client ID
            String clientID = String.valueOf(Math.random());
            // Connect to server
            chat.put("add", gameID, name, clientID);
            // Get ack from server
            Object[] t = chat.get(new ActualField(clientID), new FormalField(String.class));
            if (!t[1].equals("ok")) {
                System.out.println("Server did not ack... returning");
                return;
            }
            //Wait for server to start
            System.out.println("Waiting for server to start");
            //Get game state
            t = chat.query(new ActualField(gameID), new FormalField(Integer.class));
            if (((Integer) t[1] == 1)) {
                System.out.println("Starting game...");

            } else {
                System.out.println("Failed to start game");
            }
            System.out.println("playing game");
            while (!((Integer) t[1] == 0)) {
                // Answer server ack
                t = chat.query(new ActualField(gameID), new FormalField(Integer.class));
                if ((Integer) t[1] == 3) {
                    chat.put(gameID, clientID, "ok");
                }
                System.out.println("Question coming up");

                //Questionable stuff starts now
                //Get question from server and print to console
                t = chat.query(new ActualField(gameID), new ActualField("Q"), new FormalField(String.class));
                System.out.println("Question: " + t[2]);
                //Get answer and send to server
                chat.put(gameID, clientID, input.readLine());
                //Get actual answer from server
                t = chat.query(new ActualField(gameID), new ActualField("A"), new FormalField(String.class));
                //Should check if client already supplied correct answer
                System.out.println("Correct answer was " + t[3]);
                //Sleep because nap time
                Thread.sleep(2000);
                //Questionable coding ends.... maybe

                //Check game state
                t = chat.query(new ActualField(gameID), new FormalField(Integer.class));
            }
            System.out.println("Stopping game...");

        } catch (
                IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


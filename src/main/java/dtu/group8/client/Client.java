package dtu.group8.client;

import dtu.group8.server.ClientServer;
import dtu.group8.server.model.Player;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
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
    private final String LOCALHOST = "localhost";
    //private static final String LOCALHOST = "10.209.95.114";

    private static final String TYPE = "?keep";
    private Player player;
    private String clientName = "";
    String clientID = "";
    private final String OPTIONS = "Options:\n\t1. create board\n\t2. join board\n\t3. exit\n\t or wait to get an invitation";
    private BufferedReader input;
    public static Object[] allPlayers;
    //private boolean amIHost = false;

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
            clientName = input.readLine();
            clientID = UUID.randomUUID().toString();
            remoteSpace.put("lobby", clientName, clientID);
            ThreadCreateBoard threadCreateBoard = new ThreadCreateBoard(remoteSpace);
            Thread thread = new Thread(threadCreateBoard);
            thread.start();

            Object[] obj = remoteSpace.get(new ActualField(clientID), new FormalField(String.class));
            thread.join();


            String spaceId = obj[1].toString();
            String uri2 = "tcp://" + LOCALHOST + ":" + PORT + "/" + spaceId + TYPE;
            System.out.println("You are connected to board: " + spaceId);


            Space newSpace = new RemoteSpace(uri2);
            ClientServer server = new ClientServer(newSpace);
            // TODO
            server.run();

            return newSpace;
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

            player = new Player(clientID);
            player.setName(clientName);
            ThreadStartGame threadStartGame = new ThreadStartGame(server, player);
            Thread sThread = new Thread(threadStartGame);
            sThread.start();
            // Waiting for an invitation
            Object[] ackMsg = server.get(new ActualField(clientID), new FormalField(Object.class), new FormalField(Object.class));
            String invitedPlayerName = ackMsg[2].toString();

            Object[] obj = server.query(new ActualField("host"), new FormalField(Object.class));
            String hostClientId = obj[1].toString();
            sThread.join();
            if (!Objects.equals(hostClientId, clientID)) {
                while (true) {
                    System.out.println("\nYou are invited to join " + invitedPlayerName + "'s game.\nWrite <ok> to join, or <no> to refuse. You have 10 seconds.");
                    String userInput = input.readLine();
                    if (userInput.equalsIgnoreCase("ok")) {
                        server.put("ack", "ok", clientID);

                    } else if (userInput.equalsIgnoreCase("no")) {
                        server.put("ack", "no", clientID);
                    }
                }
            } else  {
                System.out.println("Waiting for player(s) to join...");
            }

/*            Thread checkAckThread = new Thread(new Thread_Acknowledgement_ToJoinGame(server, false));
            checkAckThread.start();

            Thread sleepThread = new Thread(new Thread_Acknowledgement_ToJoinGame(server, true));*/


            server.get(new ActualField("game started"));
            System.out.println("Game is starting...");



            server.getp(new ActualField("hello"));
            //System.out.println("hello received");
            // Generate random client ID
            String clientID = String.valueOf(Math.random());
            // Connect to server
            server.put("add", clientName,clientID);
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
            System.out.println("playing game");
            while (!((Integer) t[1] == 0)) {
                // Answer server ack
                t = server.query(new FormalField(Integer.class));
                if ((Integer) t[1] == 3 ){
                    server.put( clientID, "ok");
                }
                System.out.println("Question coming up");

                //Questionable stuff starts now
                //Get question from server and print to console
                t = server.query(new ActualField("Q"), new FormalField(String.class));
                System.out.println("Question: " + t[2]);
                //Get answer and send to server
                server.put( clientID, input.readLine());
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


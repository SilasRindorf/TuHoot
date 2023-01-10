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
    //private static final String LOCALHOST = "10.209.95.114";
    private final String IP = "localhost";

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
            String uri2 = "tcp://" + IP + ":" + PORT + "/" + spaceId + TYPE;
            //String uri2 = "tcp://" + LOCALHOST + ":" + PORT + "/" + spaceId + TYPE;
            System.out.println("You are connected to board: " + spaceId);


            Space newSpace = new RemoteSpace(uri2);
/*            ClientServer server = new ClientServer(newSpace);
            // TODO
            server.run();*/
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

            player = new Player(clientID);
            player.setName(clientName);
            ThreadStartGame threadStartGame = new ThreadStartGame(space, player);
            Thread sThread = new Thread(threadStartGame);
            sThread.start();
            // Waiting for an invitation
            Object[] ackMsg = space.get(new ActualField(clientID), new FormalField(Object.class), new FormalField(Object.class));
            String invitedPlayerName = ackMsg[2].toString();

            Object[] obj = space.query(new ActualField("host"), new FormalField(Object.class));
            String hostClientId = obj[1].toString();
            sThread.join();
            // Checks if this client is the host
            if (!Objects.equals(hostClientId, clientID)) {
                while (true) {
                    System.out.println("You are invited to join " + invitedPlayerName + "'s game.\nWrite <ok> to join, or <no> to refuse. You have 10 seconds.");
                    String userInput = input.readLine();
                    if (userInput.equalsIgnoreCase("ok")) {
                        space.put("ack", "ok", clientID);
                        break;

                    } else if (userInput.equalsIgnoreCase("no")) {
                        space.put("ack", "no", clientID);
                        break;
                    }
                }
            } else  {
                System.out.println("Waiting for player(s) to join...");
            }

            if (Objects.equals(hostClientId, clientID)) {
                Thread checkAckThread = new Thread(new Thread_Acknowledgement_ToJoinGame(space, false));
                checkAckThread.start();
                //checkAckThread.join();
                Thread sleepThread = new Thread(new Thread_Acknowledgement_ToJoinGame(space, true));
                sleepThread.start();
            }

            space.query(new ActualField("game started"));
            System.out.println("Game is starting...");

            ///// Game starts here.

            if (Objects.equals(hostClientId, clientID)) {
                System.out.println("Your are the host.");
                ClientServer clientServer = new ClientServer(space);
                clientServer.run();
            }




            space.getp(new ActualField("hello"));
            //System.out.println("hello received");
            // Generate random client ID
            String clientID = String.valueOf(Math.random());
            // Connect to space
            space.put("add", clientName,clientID);
            // Get ack from space
            Object[] t = space.get(new ActualField(clientID),new FormalField(String.class));
            if (!t[1].equals("ok")){
                System.out.println("Server did not ack... returning");
                return;
            }
            //Wait for space to start
            System.out.println("Waiting for space to start");
            //Get game state
            t = space.query(new FormalField(Integer.class));
            if (((Integer) t[1] == 1)){
                System.out.println("Starting game...");

            } else {
                System.out.println("Failed to start game");
            }
            //____________________________________ STARTING GAME ____________________________________
            System.out.println("playing game!");
            while (!((Integer) t[1] == 0)) {
                // Answer space ack
                t = space.query(new FormalField(Integer.class));
                if ((Integer) t[1] == 3 ){
                    space.put( clientID, "ok");
                }
                //____________________________________ RECEIVE QUESTION ____________________________________
                System.out.println("Question coming up");

                //Questionable stuff starts now
                //Get question from space and print to console
                t = space.query(new ActualField("Q"), new FormalField(String.class));
                System.out.println("Question: " + t[1]);
                //____________________________________ ANSWER ____________________________________
                Object[] gameState = server.queryp(new ActualField("gameState"),new FormalField(Integer.class));
                while ((int) gameState[1] == 4){
                    server.queryp(new ActualField("gameState"),new FormalField(String.class));
                }
                //Get answer and send to space
                space.put(clientID, input.readLine());
                //Get actual answer from space
                t = space.query(new ActualField("V"),new FormalField(String.class),new FormalField(Boolean.class));
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


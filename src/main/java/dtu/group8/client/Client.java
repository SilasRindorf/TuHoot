package dtu.group8.client;

import dtu.group8.server.ClientServer;
import dtu.group8.server.Game;
import dtu.group8.server.model.Player;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import static dtu.group8.lobby.Util.*;

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
    GameSetup gameSetup;
    Printer printerNoTag = new Printer("", Printer.PrintColor.WHITE);

    public Game matchMake(Player client) {
        try {
            Printer printer = new Printer("Client:matchMake", Printer.PrintColor.WHITE);
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            // Set the URI of the chat space
            printer.print("Enter URI of the lobby server or press enter for default: ");
            String uri = input.readLine();
            // Default value
            if (uri.isEmpty()) uri = getUri("lobby");
            // Connect to the remote chat space
            printer.println("Connecting to chat space " + uri + "...");
            RemoteSpace remoteSpace = new RemoteSpace(uri);
            Player player;
            if (client == null) {
                // Read client name from the console
                String clientName = "";
                while (clientName.isBlank()) {
                    printer.print("", "Enter your name: ", Printer.PrintColor.ANSI_RESET);
                    clientName = input.readLine();
                }

                String clientId = UUID.randomUUID().toString();
                player = new Player(clientName, clientId, 0);
            } else {
                player = client;
            }
            gameSetup = new GameSetup(remoteSpace);
            return gameSetup.initializeGame(player);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("return null");
        return null;

    }

    public Game setup(Game game) {
        Space space = game.getSpace();
        if (space == null) {
            System.out.println("Space is null");
            return null;
        }

        try {
            Printer log = new Printer("PlayerLog", Printer.PrintColor.YELLOW);

            // Checks if this client is the host
            if (game.amIHost()) {
                AddPlayerHandler listenForAddReq = new AddPlayerHandler(game);
                game.setThreadAddPlayer(new Thread(listenForAddReq));
                game.getThreadAddPlayer().start();
                gameSetup.display_start_game_option(game);
            } else {
                printerNoTag.println("Waiting for game to start...");
            }

            space.query(new ActualField(GAME_START));
            System.out.println("Game is starting...");

            // Checks if this client is the host
            if (game.amIHost()) {
                Thread gameThread = new Thread(new ClientServer(game));
                gameThread.start();
            }

            /*-----------------------------Initialization of a game done-------------------------------------*/

            // Connect to space
            // Get ack from space
            log.println("Getting ack");
            log.println("Player id " + game.getMe().getId());
            Object[] t = space.get(new ActualField("ACK"), new ActualField(game.getMe().getId()), new FormalField(String.class));
            log.println("Got ack response");

            if (!t[2].equals("ok")) {
                System.out.println("Server did not ack... returning");
                return null;
            }
            //Wait for space to start
            log.println("Waiting for game state to be start");
            //Get game state
            t = space.query(new ActualField("gameState"), new FormalField(Integer.class));
            if (((Integer) t[1] == 1)) {
                log.println("Starting game...");
            } else {
                System.out.println("Failed to start game");
                return null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return game;
    }

    public void start(Game game) {
        Space space = game.getSpace();
        Printer log = new Printer("PlayerLog", Printer.PrintColor.YELLOW);
        log.setLog(false);
        Printer printer = new Printer();
        try {
            //____________________________________ STARTING GAME ____________________________________
            log.println("playing game!");
            log.println("getting question size");
            Object[] size = space.query(new ActualField("QuizSize"), new FormalField(Integer.class));
            log.println("starting game loop");
            Object[] question;
            Object[] highscores;
            for (int i = 0; i < (Integer) size[1]; i++) {
                printer.println("Question coming up!");
                question = space.query(new ActualField("Q" + i), new FormalField(String.class));
                printer.println("Question " + (i + 1) + ":\n\t" + question[1].toString());
                log.println("Getting answer and sending it to space");

                questionGuess(game, log, printer, i);
                highscores = space.query(new ActualField("Highscores"), new FormalField(String.class));
                printer.println(highscores[1].toString());
                log.println("Replying to ACK");
                space.put("ACK", game.getMe().getId(), "OK");
            }
            log.println("Stopping game...");
            endGame(game);
            //____________________________________ EXCEPTION HANDLING ____________________________________
        } catch (
                InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void endGame(Game game) {
        Printer printer = new Printer();
        printer.print("Do you want join another lobby(y/n)? ");

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {

            while (true) {
                String str = input.readLine().trim();

                if (str.equalsIgnoreCase("y")) {
                    start(setup(matchMake(game.getMe())));
                } else {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: Should give right answer if user does not guess right

    private void questionGuess(Game game, Printer log, Printer printer, int i) throws IOException, InterruptedException {
        Space space = game.getSpace();
        String clientID = game.getMe().getId();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Object[] answer;
        space.put("A", clientID, input.readLine(), i);
        log.println("Waiting for verification of answer");
        answer = space.get(new ActualField("V"), new FormalField(String.class), new FormalField(Boolean.class));
        log.println("Received verification from Space");
        if ((boolean) answer[2]) {
            printer.println("You got the answer correct!");
        } else {
            log.println("Getting correct answer");
            answer = space.query(new ActualField("CA" + i), new FormalField(String.class));
            printer.println("You got the answer wrong! The correct answer was " + answer[1]);
            questionGuess(game, log, printer, i);
        }
    }

    private String getUri(String parameter) {
        return "tcp://" + IP + ":" + PORT + "/" + parameter + TYPE;
    }
}
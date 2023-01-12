package dtu.group8.client.matchmaker;

import dtu.group8.client.ThreadCreateBoard;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.IOException;
import java.util.UUID;

public class MatchMaker {
    private static final String TYPE = "?keep", JOIN_ME_REQ = "join_req", JOIN_ME_RES = "join_res";
    private final Space space;
    private final String PORT = "9002", IP = "localhost";
    public String clientID, uri;

    public MatchMaker(Space space) {
        this.space = space;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void start(GameSpaceHandler gameSpaceHandler) {
        RemoteSpace lobby;
        try {
            Printer printer = new Printer("Client:matchMake", Printer.PrintColor.WHITE);
            // ____________________________________ SETUP CONNECTION TO LOBBY ____________________________________
            clientID = UUID.randomUUID().toString();
            // Set the URI of the chat space
            // Default value
            if (uri == null) {
                uri = getUri("lobby");

            }
            // Connect to the remote chat space
            printer.println("Connecting to chat space " + uri + "...");

            lobby = new RemoteSpace(uri);

            // Read client name from the console
            printer.print("", "Enter your name: ", Printer.PrintColor.ANSI_RESET);
            Object input = space.get(new FormalField(String.class));
            String playerName = input.toString();

            // ____________________________________ JOIN LOBBY ____________________________________
            lobby.put("lobby", playerName, clientID);

            ThreadCreateBoard threadCreateBoard = new ThreadCreateBoard(lobby);
            Thread thread = new Thread(threadCreateBoard);
            thread.start();

            Object[] obj = lobby.get(new ActualField(clientID), new FormalField(String.class));
            thread.join();

            String spaceId = obj[1].toString();
            String uri2 = "tcp://" + IP + ":" + PORT + "/" + spaceId + TYPE;
            printer.println("You are connected to board " + spaceId);

            gameSpaceHandler.run(new RemoteSpace(uri2));
        } catch (
                IOException |
                InterruptedException e) {
            e.printStackTrace();
        }
    }


    private String getUri(String parameter) {
        return "tcp://" + IP + ":" + PORT + "/" + parameter + TYPE;
    }
}


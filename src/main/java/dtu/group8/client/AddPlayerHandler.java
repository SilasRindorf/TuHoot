package dtu.group8.client;


import dtu.group8.server.Game;
import dtu.group8.server.model.Player;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;
import java.util.ArrayList;

import static dtu.group8.lobby.Util.*;

public class AddPlayerHandler implements Runnable{
    private Game game;
    private RemoteSpace lobbySpace;
    private Space space;
    public AddPlayerHandler(Game game) {
        this.game = game;
        this.lobbySpace = game.getRemoteSpace();
        this.space = game.getSpace();

    }

    @Override
    public void run() {
        Printer printer = new Printer("", Printer.PrintColor.WHITE);

        try {
            while (true) {
                Object[] objs = lobbySpace.get(new ActualField(JOINT_REQ_FROM_SERVER), new ActualField(game.getMe().getId()),
                        new FormalField(String.class), new FormalField(String.class));

                String pName = objs[2].toString();
                String pId = objs[3].toString();
                Player newPlayer = new Player(pName, pId, 0);

                game.getPrinterLock().acquire();
                System.out.println();
                printer.println( newPlayer.getName() + " wants to join");
                printer.println("Enter 'ok' to accept or 'no' to decline request.");
                System.out.print("Input command: ");
                String str = game.takeUserInput();

                if (str.equalsIgnoreCase("ok")) {
                    lobbySpace.put(JOINT_RES_FROM_HOST, game.getName(), game.getId(), game.getHostName(),
                            game.getHostId(), newPlayer.getName(), newPlayer.getId());

                    printer.println("You have accepted " + newPlayer.getName());
                    game.addPlayer(newPlayer);
                    Object[] obj = space.get(new ActualField(ALL_PLAYERS), new FormalField(ArrayList.class), new FormalField(ArrayList.class));
                    ArrayList<String> playerNames = (ArrayList<String>) obj[1];
                    ArrayList<String> playerIds = (ArrayList<String>) obj[2];
                    playerNames.add(newPlayer.getName());
                    playerIds.add(newPlayer.getId());
                    space.put(ALL_PLAYERS, playerNames, playerIds);
                    //printer.println("Sent response to " + pName); //
                    game.display_size_of_added_player();
                } else if (str.equalsIgnoreCase("no")) {
                    // do nothing
                    // TODO Send a message to the server, and then have the server send a response back to the client.
                }
                game.getPrinterLock().release();
            }


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

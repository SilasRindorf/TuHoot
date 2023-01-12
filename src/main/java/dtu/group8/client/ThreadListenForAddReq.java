package dtu.group8.client;

import dtu.group8.lobby.Util;
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
import java.util.ArrayList;

import static dtu.group8.lobby.Util.*;

public class ThreadListenForAddReq implements Runnable{
    private Game game;
    private RemoteSpace lobbySpace;
    private Space space;
    public ThreadListenForAddReq(Game game) {
        this.game = game;
        this.lobbySpace = game.getRemoteSpace();
        this.space = game.getSpace();

    }

    @Override
    public void run() {
        //BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Printer printer = new Printer("ThreadListenForAddReq:", Printer.PrintColor.WHITE);

        try {
            while (true) {
                Object[] objs = lobbySpace.get(new ActualField(JOINT_REQ_FROM_SERVER), new ActualField(game.getMe().getId()),
                        new FormalField(String.class), new FormalField(String.class));

                String pName = objs[2].toString();
                String pId = objs[3].toString();
                Player newPlayer = new Player(pName, pId, 0);


                System.out.println("\nThreadListenForAddReq: " + newPlayer.getName() + " wants to join");
                System.out.println("Enter ok to accept, or no to refuse the request");
                String str = game.takeUserInput();
                //String str = Util.takeUserInput();
                System.out.println(str);
                if (str.equalsIgnoreCase("ok")) {
                    lobbySpace.put(JOINT_RES_FROM_HOST, game.getHostId(), newPlayer.getName(), newPlayer.getId());
                    printer.println("You accepted: " + newPlayer.getName());

                    game.addPlayer(newPlayer);
                    Object[] obj = space.get(new ActualField(ALL_PLAYERS), new FormalField(ArrayList.class), new FormalField(ArrayList.class));
                    ArrayList<String> playerNames = (ArrayList<String>) obj[1];
                    ArrayList<String> playerIds = (ArrayList<String>) obj[2];
                    playerNames.add(newPlayer.getName());
                    playerIds.add(newPlayer.getId());
                    space.put(ALL_PLAYERS, playerNames, playerIds);

                    printer.println("Sent req back to server: " + pName);

                } else if (str.equalsIgnoreCase("no")) {
                    // do nothing
                }
            }


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

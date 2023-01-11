package dtu.group8.client;


import dtu.group8.server.model.Player;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.Thread.sleep;

public class ThreadStartGame implements Runnable {
    private final String OPTIONS = "Options:\n\t1. start game\n\tor press enter to wait for an invitation";
    private Space space;
    private static final String LOCK_FOR_GAME_START = "lockForGameStart";
    private Player player;
    private BufferedReader input;
    private static final String JOIN_ME_REQ = "join_req";
    public ThreadStartGame(Space space, Player player) {
        this.space = space;
        this.player = player;
    }
    private boolean alive = true;

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public void run() {
        try {
            while (alive) {
                Printer log = new Printer("ThreadStartGame", Printer.PrintColor.BLUE);
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                input = new BufferedReader(new InputStreamReader(System.in));
                String userInput = input.readLine();
                if (userInput.equalsIgnoreCase("1") || userInput.equalsIgnoreCase("start game")){
                    close();
                    Object[] obj = space.get(new ActualField(LOCK_FOR_GAME_START));
                    log.println("Locked lobby server");
                    if (obj[0].equals(LOCK_FOR_GAME_START)) {
                        log.println("Getting all clients");
                        Client.allPlayers = space.query(new ActualField("allMembers"), new FormalField(Object.class), new FormalField(Object.class));
                        log.println("Got all clients");
                        String[] pids = (String[]) Client.allPlayers[2];
                        log.println("Sending player names to server");
                        for (String pid : pids) {
                            log.println("\t","pid=" + pid + " name=" + player.getName());
                            space.put(pid, JOIN_ME_REQ, player.getName());
                        }
                        log.println("Adding host...");
                        // TODO The host must be removed from the tuple when the game is over
                        space.put("host", player.getId());
                        log.println("Unlocking server");
                        space.put(LOCK_FOR_GAME_START);
                        break;
                    } else {
                        System.out.println("The game has already been started");
                    }
                    //space.put(LOCK_FOR_GAME_START);

                } else if (userInput.equalsIgnoreCase("")) {
                    Client.allPlayers = space.query(new ActualField("allMembers"), new FormalField(Object.class), new FormalField(Object.class));
                    System.out.println("Waiting for a game to start...");
                    close();
                    break;
                }
                close ();
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close(){
        alive = false;

    }
}


class Thread_Acknowledgement_ToJoinGame implements Runnable {
    Space space;
    String[] pids;
    boolean sleepThread;
    HashMap<String, String> receivedPids;
    private static final String JOIN_ME_RES = "join_res";


    public Thread_Acknowledgement_ToJoinGame(Space space, boolean sleepThread) {
        this.space = space;
        this.sleepThread = sleepThread;
    }

    @Override
    public void run() {

        try {
            if (sleepThread) {
                sleep(10000);
                space.put("game started");
                // TODO Remove players that have not responded.
                return;
            }

            receivedPids = new HashMap<>();
            pids = (String[]) Client.allPlayers[2];
            for (int i = 0; i < pids.length-1; i++) {
                Object[] obj = space.get(new ActualField(JOIN_ME_RES), new FormalField(Object.class), new FormalField(Object.class));
                receivedPids.put(obj[1].toString(), obj[2].toString());
            }

            space.put("game started");

            // TODO Remove players that replies "no"

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}

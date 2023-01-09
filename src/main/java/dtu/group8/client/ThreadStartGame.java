package dtu.group8.client;


import dtu.group8.server.model.Player;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ThreadStartGame implements Runnable {
    private final String OPTIONS = "Options:\n\t1. start game\n\tor wait to get an invitation";
    private Space space;
    private boolean isAlive = true;
    private boolean amIHost = false;
    private static final String LOCK_FOR_GAME_START = "lockForGameStart";
    private Player player;
    //BufferedReader input;




    public ThreadStartGame(Space space, Player player) {
        this.space = space;
        this.player = player;
    }

    @Override
    public void run() {
        try {
            while (isAlive) {
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                //input = new BufferedReader(new InputStreamReader(System.in));
                Scanner scanner = new Scanner(System.in);



                String userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase("1") || userInput.equalsIgnoreCase("start game")){
                    Object[] obj = space.getp(new ActualField(LOCK_FOR_GAME_START));

                    if (obj[0].equals(LOCK_FOR_GAME_START)) {

                        Object[] membersObj = space.get(new ActualField("allMembers"), new FormalField(Object.class), new FormalField(Object.class));
                        String[] pids = (String[]) membersObj[2];

                        for (String pid : pids) {
                            space.put(pid, "join me", player.getName());
                        }



                        amIHost = true;
                        space.put(LOCK_FOR_GAME_START);
                        break;

                    } else {
                        System.out.println("The game has already been started");
                    }

                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isAmIHost() {
        return amIHost;
    }
}

class ThreadAcknowledgement implements Runnable {
    Space space;
    String[] pids;

    public ThreadAcknowledgement(Space space, String[] pids) {
        this.space = space;
        this.pids = pids;
    }

    @Override
    public void run() {
       HashMap<String, String> receivedPids = new HashMap<>();

        try {
            for (int i = 0; i < pids.length; i++) {
                Object[] obj = space.get(new ActualField("ack"), new FormalField(Object.class), new FormalField(Object.class));
                receivedPids.put(obj[1].toString(), obj[2].toString());
            }
            System.out.println(receivedPids);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}

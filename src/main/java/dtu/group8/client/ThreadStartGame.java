package dtu.group8.client;


import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class ThreadStartGame implements Runnable {
    private final String OPTIONS = "Options:\n\t1. start game\n\tor wait to get an invitation";
    private Space space;
    private boolean isAlive = true;
    private boolean amIHost = false;
    private static final String LOCK_FOR_GAME_START = "lockForGameStart";




    public ThreadStartGame(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        try {
            while (isAlive) {
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

                String userInput = input.readLine();
                if (userInput.equalsIgnoreCase("1") || userInput.equalsIgnoreCase("start game")){
                    Object[] obj = space.getp(new ActualField(LOCK_FOR_GAME_START));

                    if (obj[0].equals(LOCK_FOR_GAME_START)) {

                        Object[] membersObj = space.get(new ActualField("allMembers"), new FormalField(Object.class), new FormalField(Object.class));

                        String[] pids = (String[]) membersObj[2];


                        space.put("joinMe");
                        amIHost = true;
                        space.put(LOCK_FOR_GAME_START);
                        break;

                    } else {
                        System.out.println("The game has already been started");
                    }

                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}


class Player2 {
    private String name;
    private String id;
    private int point;


    Player2(String name, String id, int point) {
        this.name = name;
        this.id = id;
        this.point = point;
    }


    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getPoint() {
        return point;
    }
}
package dtu.group8.client;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ThreadCreateBoard implements Runnable {
    private final String OPTIONS = "Options:\n\t1. create board\n\tor press enter to wait for a board";
    private RemoteSpace remoteSpace;
    private boolean isAlive = true;
    private boolean amIBoardCreator = false;
    public ThreadCreateBoard(RemoteSpace remoteSpace) {
        this.remoteSpace = remoteSpace;
    }
    @Override
    public void run() {
        try {
            while (isAlive) {
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                String userInput = input.readLine();
                if (userInput.equalsIgnoreCase("create board") ||
                        userInput.equalsIgnoreCase("1")){
                    remoteSpace.get(new ActualField("createBoardLock"));

                    remoteSpace.put("create board");
                    amIBoardCreator = true;
                    remoteSpace.put("createBoardLock");
                    break;

                } else if (userInput.equalsIgnoreCase("")){
                    System.out.println("Waiting for a board...");
                    break;
                }
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

/*    public void setAlive(boolean alive) {
        isAlive = alive;
    }*/

    public boolean amIBoardCreator() {
        return amIBoardCreator;
    }
}

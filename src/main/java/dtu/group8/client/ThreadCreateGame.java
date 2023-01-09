package dtu.group8.client;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ThreadCreateGame implements Runnable {
    private final String OPTIONS = "Options:\n\t1. create board\n\t2. join board\n\t3. exit\n\tor wait to get a board";
    private RemoteSpace remoteSpace;
    private boolean isAlive = true;
    private boolean amIBoardCreator = false;
    public ThreadCreateGame(RemoteSpace remoteSpace) {
        this.remoteSpace = remoteSpace;
    }
    @Override
    public void run() {
        try {
            while (isAlive) {
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                //BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                //String userInput = input.readLine();
                Scanner scanner = new Scanner(System.in);
                String userInput = scanner.nextLine();


                if (userInput.equalsIgnoreCase("create board") ||
                        userInput.equalsIgnoreCase("1")){
                    remoteSpace.get(new ActualField("createBoardLock"));

                    remoteSpace.put("create board");
                    amIBoardCreator = true;
                    remoteSpace.put("createBoardLock");
                    break;
                } else if (userInput.equalsIgnoreCase("join board") ||
                        userInput.equalsIgnoreCase("2")){
                    break;
                } else if (userInput.equalsIgnoreCase("exit") ||
                        userInput.equalsIgnoreCase("3")){
                    System.out.println("Exiting...");
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean amIBoardCreator() {
        return amIBoardCreator;
    }
}

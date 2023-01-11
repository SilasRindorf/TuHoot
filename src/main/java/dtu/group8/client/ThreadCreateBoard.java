package dtu.group8.client;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadCreateBoard implements Runnable {
    private final String OPTIONS = "Options:\n\t1. create board\n\tor press enter to wait for a board";
    private RemoteSpace remoteSpace;
    private String myId;

    public ThreadCreateBoard(RemoteSpace remoteSpace, String myId) {
        this.remoteSpace = remoteSpace;
        this.myId = myId;
    }
    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                String userInput = input.readLine();
                if (userInput.equalsIgnoreCase("create board") ||
                        userInput.equalsIgnoreCase("1")){

                    //remoteSpace.get(new ActualField("createBoardLock"));
                    System.out.print("Enter board name: ");
                    String boardName = input.readLine();
                    remoteSpace.put("create board",boardName, myId);

                    //Object SremoteSpace.get(new ActualField(myId));


                    //remoteSpace.put("createBoardLock");
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


}

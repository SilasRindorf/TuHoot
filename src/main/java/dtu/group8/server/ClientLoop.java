package dtu.group8.server;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientLoop implements Runnable {
    private final String OPTIONS = "Options:\n\t1. create board\n\t2. join board\n\t3. exit\n\t or wait to get an invitation";
    private RemoteSpace remoteSpace;

    public ClientLoop(RemoteSpace remoteSpace) {
        this.remoteSpace = remoteSpace;
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
                    remoteSpace.get(new ActualField("createBoardLock"));

                    remoteSpace.put("create board");
                    //isBoardCreated = true;

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
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

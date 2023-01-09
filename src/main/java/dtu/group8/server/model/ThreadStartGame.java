package dtu.group8.server.model;


import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadStartGame implements Runnable {
    private final String OPTIONS = "Options:\n\t1. start game\n\tor wait to get an invitation";
    private Space space;


    public ThreadStartGame(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

                String userInput = input.readLine();
                if (userInput.equalsIgnoreCase("1") || userInput.equalsIgnoreCase("start game")){

                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

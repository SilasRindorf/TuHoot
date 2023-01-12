package dtu.group8.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInput implements Runnable{
    private String userInput;
    private boolean stop = false;

    @Override
    public void run() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (!stop) {
            try {
                userInput = input.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getUserInput() {
        return userInput;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}

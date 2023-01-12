package dtu.group8.client;

import dtu.group8.client.matchmaker.MatchMaker;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class ModularClient {
    private String clientID;
    private Space server;
    private final Space clientInput;
    private int state = -2;
    private final boolean alive = true;
    private final Printer log;
    private boolean isListeningToState = true;

    public ModularClient() {
        log = new Printer();
        log.setDefaultTAG("ModularClient");
        log.setDefaultPrintColor(Printer.PrintColor.YELLOW);
        clientInput = new SequentialSpace();

        setup();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        String strInput = "";
        while (alive) {
            try {
                log.println("Looping...");
                strInput = input.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            switch (state) {
                case -2:
                    try {
                        clientInput.put(strInput);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case -1:
                    break;
                case 0:
                    try {
                        clientInput.put(strInput);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        }
    }

    public void listenToState() {
        Object[] serverState;
        while (isListeningToState) {
            try {
                serverState = server.queryp(new ActualField("GameState"), new FormalField(Integer.class));
                if (serverState != null && (Integer) serverState[1] != state) {
                    state = (Integer) serverState[1];
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void setup() {
        clientID = UUID.randomUUID().toString();
        MatchMaker mm = new MatchMaker(clientInput);
        mm.clientID = this.clientID;
        //_________ MATCH MAKE THREAD _________
        Thread matchMake = new Thread(() -> {
            mm.start(gameSpace -> {
                Printer log = new Printer();
                state = -1;
                server = gameSpace;
            });
        });
        matchMake.start();


        //_________ STATE LISTENER THREAD _________
        Thread stateListener = new Thread(this::listenToState);
        //stateListener.start();


    }

    public void close() {
        isListeningToState = false;
    }
}

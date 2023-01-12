package dtu.group8.client;

import dtu.group8.server.Game;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static dtu.group8.lobby.Util.JOINT_REQ_FROM_SERVER;
import static dtu.group8.lobby.Util.JOINT_RES_FROM_HOST;

public class ThreadListenForAddReq implements Runnable{
    private Game game;
    private RemoteSpace lobbySpace;
    public ThreadListenForAddReq(Game game) {
        this.game = game;
        this.lobbySpace = game.getRemoteSpace();

    }

    @Override
    public void run() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        try {
            Object[] objs = lobbySpace.query(new ActualField(JOINT_REQ_FROM_SERVER), new ActualField(game.getMe().getId()),
                    new FormalField(String.class), new FormalField(String.class));

            String pName = objs[2].toString();
            String pId = objs[2].toString();

            System.out.println(pName + " wants to join");
            System.out.println("Enter ok to accept, or no to refuse the request");
            String str = input.readLine();
            if (str.equalsIgnoreCase("ok")) {
                lobbySpace.put(JOINT_RES_FROM_HOST, game.getId(), pName, pId);
            } else if (str.equalsIgnoreCase("no")) {
                // do nothing
            }




        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

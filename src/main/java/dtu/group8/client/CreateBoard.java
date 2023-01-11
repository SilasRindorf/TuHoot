package dtu.group8.client;

import dtu.group8.server.Game;
import dtu.group8.server.model.Player;
import org.jspace.RemoteSpace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CreateBoard {
    private final String OPTIONS = "Options:\n\t1. create game\n\t2. join game";
    private RemoteSpace remoteSpace;
    private Player player;
    private Game game;

    public CreateBoard(Game game, Player player, RemoteSpace remoteSpace) {
        this.game = game;
        this.player = player;
        this.remoteSpace = remoteSpace;

    }

    public void showOptions_andTakeInput() {
        try {
            while (true) {
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                String userInput = input.readLine();
                if (userInput.equalsIgnoreCase("create game") ||
                        userInput.equalsIgnoreCase("1")){

                    //remoteSpace.get(new ActualField("createBoardLock"));
                    System.out.print("Enter board name: ");
                    String gameName = input.readLine();
                    game.setName(gameName);
                    game.setHost(player.getId());
                    remoteSpace.put("create game",gameName, player.getId());
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

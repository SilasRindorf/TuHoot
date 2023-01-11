package dtu.group8.client;

import dtu.group8.server.Game;
import dtu.group8.server.model.Player;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InitializeGame {
    private static final String LOCK_FOR_GAME_START = "lockForGameStart";
    private static final String JOIN_ME_REQ = "join_req";

    private RemoteSpace remoteSpace;
    private Player player;
    private Game game;


    public InitializeGame(Game game, Player player, RemoteSpace remoteSpace) {
        this.game = game;
        this.player = player;
        this.remoteSpace = remoteSpace;

    }

    public void display_create_and_joint_game_options() {
        final String OPTIONS = "Options:\n\t1. create game\n\t2. join game";
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


    public void display_start_game_option (Space space) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            final String OPTIONS = "Options:\n\t1. start game\n\tor press enter to wait for an invitation";

            while (true) {
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                input = new BufferedReader(new InputStreamReader(System.in));
                String userInput = input.readLine();
                if (userInput.equalsIgnoreCase("1") || userInput.equalsIgnoreCase("start game")){
                    Object[] obj = space.getp(new ActualField(LOCK_FOR_GAME_START));

                    if (obj[0].equals(LOCK_FOR_GAME_START)) {

                        Client.allPlayers = space.query(new ActualField("allMembers"), new FormalField(Object.class), new FormalField(Object.class));
                        String[] pids = (String[]) Client.allPlayers[2];

                        for (String pid : pids) {
                            space.put(pid, JOIN_ME_REQ, player.getName());
                        }
                        space.put(LOCK_FOR_GAME_START);

                        // TODO The host must be removed from the tuple when the game is over
                        space.put("host", player.getId());
                        break;

                    } else {
                        System.out.println("The game has already been started");
                    }

                } else if (userInput.equalsIgnoreCase("")) {
                    Client.allPlayers = space.query(new ActualField("allMembers"), new FormalField(Object.class), new FormalField(Object.class));
                    System.out.println("Waiting for a game to start...");
                    break;
                }
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

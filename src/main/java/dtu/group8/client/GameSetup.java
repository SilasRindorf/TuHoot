package dtu.group8.client;

import dtu.group8.server.Game;
import dtu.group8.server.model.Player;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static dtu.group8.client.Client.*;

public class GameSetup {
    private static final String LOCK_FOR_GAME_START = "lockForGameStart";
    private static final String JOIN_ME_REQ = "join_req";

    private RemoteSpace remoteSpace;

    public GameSetup(RemoteSpace remoteSpace) {
        this.remoteSpace = remoteSpace;

    }

    public Game initializeGame(Player player) {
        Game game = new Game();
        game.setMe(player);
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
                    remoteSpace.put("create game",gameName, player.getId(), player.getName());
                    getSpace(game);
                    break;

                } else if (userInput.equalsIgnoreCase("2") || userInput.equalsIgnoreCase("join game")){

                    System.out.println("Waiting for a board...");
                    break;
                }
            }

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        return game;
    }


    public void display_start_game_option (Game game) {
        Space space = game.getSpace();
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            final String OPTIONS = "Options:\n\t1. start game\n\tor press enter to wait for an invitation";

            while (true) {
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                String userInput = input.readLine();
                if (userInput.equalsIgnoreCase("1") || userInput.equalsIgnoreCase("start game")){
                    //Object[] obj = space.getp(new ActualField(LOCK_FOR_GAME_START));

                    if (obj[0].equals(LOCK_FOR_GAME_START)) {

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

    void getAllPlayersFromSpace(Game game, Space space) throws InterruptedException {
        Object[] obj = space.query(new ActualField("allMembers"), new FormalField(Object.class), new FormalField(Object.class));

    }

    void getSpace(Game game) throws InterruptedException, IOException {
        Printer printer = new Printer("GameSetup: getSpace", Printer.PrintColor.WHITE);

        Object[] obj = remoteSpace.get(new ActualField("mySpaceId"), new ActualField(game.getMe().getId()), new FormalField(Object.class), new FormalField(Object.class));
        game.setName(obj[3].toString());

        if (Objects.equals(game.getHost(), game.getMe().getId()))
            printer.println("Game " + game.getName() +" created");

        game.setId(obj[2].toString()); // spaceId/gameId
        String uri2 = "tcp://" + IP + ":" + PORT + "/" + game.getId() + TYPE;
        printer.println("You are connected to game " + game.getName());
        game.setSpace(new RemoteSpace(uri2));
    }


    void joinGame() {

    }
}

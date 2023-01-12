package dtu.group8.client;

import dtu.group8.server.Game;
import dtu.group8.server.model.Player;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static dtu.group8.lobby.Util.*;
import static dtu.group8.lobby.Util.IP;
import static dtu.group8.lobby.Util.PORT;
import static dtu.group8.lobby.Util.TYPE;

public class GameSetup {
    private RemoteSpace lobbySpace;
    private Printer printer;

    public GameSetup(RemoteSpace lobbySpace) {
        this.lobbySpace = lobbySpace;
        this.printer = new Printer("GameSetup:", Printer.PrintColor.WHITE);

    }

    public Game initializeGame(Player player) {
        Game game = new Game();
        game.setMe(player);
        game.addPlayer(player);
        game.setRemoteSpace(lobbySpace);
        final String OPTIONS = "Options:\n\t1. create game\n\t2. join game";
        try {
            while (true) {
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                String userInput = game.takeUserInput();

                if (userInput.equalsIgnoreCase("create game") ||
                        userInput.equalsIgnoreCase("1")){
                    System.out.print("Enter game name: ");
                    String gameName = game.takeUserInput();
                    game.setName(gameName);
                    game.setHostId(player.getId());
                    /* Sends a request to the server to create a new game. */
                    lobbySpace.put(CREATE_GAME_REQ,gameName, player.getId(), player.getName());

                    /* Gets the appropriate space from the server*/
                    getSpace(game);
                    // TODO Check if the given game-name already exists in the server.
                    break;

                } else if (userInput.equalsIgnoreCase("2") || userInput.equalsIgnoreCase("join game")){
                    if (joinGame(game)) {
                        break;
                    } else return initializeGame(player);
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
            final String OPTIONS = "Options:\n\t1. start game\n\tor just wait for other to join";
            while (true) {
                game.getPrinterLock().acquire();
                System.out.println(OPTIONS);
                System.out.print("Input command: ");
                game.getPrinterLock().release();
                String userInput = game.takeUserInput();
                if (userInput.equalsIgnoreCase("1") || userInput.equalsIgnoreCase("start game")){
                    getAllPlayersFromSpace(game);
                    space.put(GAME_START);
                    break;

                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO Add all the added players from the host server to space
    void getAllPlayersFromSpace(Game game) throws InterruptedException {
        Object[] obj = game.getSpace().query(new ActualField(ALL_PLAYERS), new FormalField(ArrayList.class), new FormalField(ArrayList.class));
        ArrayList<String> playerNames = (ArrayList<String>) obj[1];
        ArrayList<String> playerIds = (ArrayList<String>) obj[2];
        assert playerIds.size() != playerNames.size() : "players.size != playerIds.size";

        for (int i = 0; i < playerNames.size(); i++) {
            Player currPlayer = new Player(playerNames.get(i), playerIds.get(i), 0);
            game.addPlayer(currPlayer);
        }

    }

    void getSpace(Game game) throws InterruptedException, IOException {
        Printer printer = new Printer("GameSetup: getSpace", Printer.PrintColor.WHITE);
        /* Awaits a response from the server regarding the game creation. */
        Object[] obj = lobbySpace.get(new ActualField(MY_SPACE_ID), new ActualField(game.getMe().getId()),
                new FormalField(Object.class), new FormalField(Object.class),
                new FormalField(Object.class), new FormalField(Object.class));

        String gameName = obj[2].toString();
        String gameId = obj[3].toString();
        String hostName = obj[4].toString();
        String hostId = obj[5].toString();

        game.setName(gameName);
        game.setId(gameId);
        game.setHostName(hostName);
        game.setHostId(hostId);

        // Checks if this player/client is the host
        if (game.amIHost())
            printer.println("Game " + game.getName() +" created");
        String uri2 = "tcp://" + IP + ":" + PORT + "/" + game.getId() + TYPE;
        printer.println("You are connected to game " + game.getName());
        game.setSpace(new RemoteSpace(uri2));

        // Displays the size of added players.
        if (game.amIHost()) {
            game.display_size_of_added_player();
        }
    }


    boolean joinGame(Game game) throws InterruptedException, IOException {
        /* Retrieves all the available game from the server/lobby */
        String myId =  game.getMe().getId();
        lobbySpace.put( SHOW_ME_AVAILABLE_GAMES_REQ, myId);
        Object[] obj = lobbySpace.get(new ActualField(SHOW_ME_AVAILABLE_GAMES_RES), new ActualField(myId), new FormalField(ArrayList.class));
        ArrayList<String> arr = (ArrayList<String>) obj[2];

        /* Displays the available games that are obtained by making the request above. */
        System.out.println("Available game(s): " + arr.size());
        HashMap<String, String> gameNames = new HashMap<>();
        for (String s : arr) {
            String[] currGame = s.split(PATTERN_FOR_PLAYER_ID_SPLITTER, 2);
            String gameName = currGame[0];
            String gameId = currGame[1];
            gameNames.put(gameName,gameId);
            System.out.println("\t" + gameName);
        }

        /* Lets the user choose a game by typing its name. */
        String userChosenGameId;
        while (true) {
            if (arr.isEmpty()) printer.print("Press enter to go back: ");
            else printer.print("Enter a game name to join or press enter to go back: ");

            String userInput = game.takeUserInput();
            if (userInput.equals("")) return false;   // This means the user wants to go back.

            userChosenGameId = gameNames.get(userInput);
            if (userChosenGameId != null) break;   // The chosen game name does not exist in the game.
            else System.out.println("There is no game with a name " + userInput);

        }

        /* Sends a request to the lobby server to ask the host to allow this client to join the chosen game. */
        lobbySpace.put(ADD_ME_REQ_FROM_CLIENT, game.getMe().getName(), game.getMe().getId(), userChosenGameId);
        printer.println("joinGame: Sent add req to server");

        // TODO Set space in game, when message is received
        getSpace(game);
        return true;
    }

}

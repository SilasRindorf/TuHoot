package dtu.group8.lobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Util {
    public final static String JOINT_REQ_FROM_SERVER = "join_req_from_server";
    public final static String JOINT_RES_FROM_HOST = "join_res_from_host";
    public final static String ADD_ME_REQ_FROM_CLIENT = "add_me_to_game";
    public final static String SHOW_ME_AVAILABLE_GAMES_REQ = "show_me_games_req";
    public final static String SHOW_ME_AVAILABLE_GAMES_RES = "show_me_games_res";
    public final static String PATTERN_FOR_PLAYER_ID_SPLITTER = "::";
    public final static String GAME_START = "game-start";



    public final static String ALL_PLAYERS = "allPlayers";
    public final static String CREATE_GAME_REQ = "create game";
    public final static String MY_SPACE_ID = "mySpaceId";

    public static final String PORT = "9002";
    public static final String IP = "localhost";
    public static final String TYPE = "?keep";


    public static String takeUserInput() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        try {
            userInput = input.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Game: " + userInput);
        return userInput;
    }
}

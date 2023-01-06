package dtu.group8.server;

import dtu.group8.server.model.Board;
import org.jspace.Space;

public class Game {
    Board board;
    String id;
    Space space;

    public Game(Board board, String id, Space space) {
/*        if (board == null) {
            board = new Board();
        }*/
        this.board = board;
        this.id = id;
        this.space = space;
    }



/*    void addPlayerToGame(Object[] obj) throws InterruptedException {
        System.out.println(obj[3]);
        String playerId = obj[3].toString();
*//*        if (game.id == null) {
            game.id = obj[1].toString();
        }*//*
        space.put(playerId, "ok");
        this.board.addPlayer(playerId);
        System.out.println("Player " + playerId + " is added");
    }*/

}

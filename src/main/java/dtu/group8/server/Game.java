package dtu.group8.server;

import dtu.group8.server.model.Board;

public class Game {
    Board board;
    String id;

    public Game(Board board, String id) {
        if (board == null) {
            board = new Board();
        }
        this.board = board;
        this.id = id;
    }

}

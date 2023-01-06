package dtu.group8.server;

import dtu.group8.server.model.Board;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.List;

class TuHootGame implements Runnable {
    Space space;
    Game game;

    public TuHootGame(Space space) {
        this.space = space;
        game = new Game(new Board(), null);
    }


    @Override
    public void run() {
        initializePlayers();

        try {

            //GAME START
            updateGameState(space, game.id, GameState.START);

            //GAME LOOP
            while (true) {
                // GAME ACK
                //updateGameState(space, )

                break;
            }
            Thread.sleep(10000);
            //GAME OVER
            updateGameState(space, game.id, GameState.STOP);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    void updateGameState(Space space, String gameId, GameState state) throws Exception{
        space.getp(new ActualField(gameId),new FormalField(Integer.class));
        space.put(gameId, state.value);

        System.out.println(GameState.START.value);
    }

    void initializePlayers() {
        try {

            System.out.println("Space from the thread " +  space);
            //add, gameID, name, clientID
            List<Object[]> objs = space.queryAll(new ActualField("add"),new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));

            for (Object[] obj : objs) {
                addPlayerToGame(obj);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    void addPlayerToGame(Object[] obj) throws InterruptedException {
        System.out.println(obj[3]);
        String playerId = obj[3].toString();
        if (game.id == null) {
            game.id = obj[1].toString();
        }
        space.put(playerId, "ok");
        game.board.addPlayer(playerId);
        System.out.println("Player " + playerId + " is added");
    }
}

enum GameState {
    STOP(0),
    START(1),
    PAUSE(2),
    ACK(3);
    public final int value;

    GameState(int value) {
        this.value = value;
    }
}
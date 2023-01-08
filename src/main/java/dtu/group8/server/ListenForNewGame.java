package dtu.group8.server;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.List;

import static java.lang.Thread.sleep;

class ListenForNewGame implements Runnable {
    Space space;
    Game game;

    public ListenForNewGame(Space space) {
        this.space = space;
        //game = new Game(new Board(), null);
    }


    @Override
    public void run() {

        try {

            //sleep(30000);
/*            initializePlayers();

            // Game start
            System.out.println("starting game " + game.id);
            updateGameState(space, game.id, GameState.START);

            game.board.printOutPlayers();
            // Game loop
*//*            while (true) {
                // Game AKN




                break;
            }*//*



            sleep(10000);
            //GAME OVER
            updateGameState(space, game.id, GameState.STOP);*/

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    void initializePlayers() {
        try {
            System.out.println("Space from the thread " +  space);
            //add, gameID, name, clientID
            List<Object[]> objs = space.queryAll(new ActualField("add"),new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));

            for (Object[] obj : objs) {
                //addPlayerToGame(obj);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}


package dtu.group8.server;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

class TuHootGame implements Runnable {
    Space space;
    public TuHootGame(Space space) {
        this.space = space;
    }


    @Override
    public void run() {
        try {

            while (true) {
                System.out.println("Space from the thread " +  space);
                //add, gameID, name, clientID
                Object[] obj = space.get(new ActualField("add"),new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
                // TODO Add the player..

                System.out.println(obj[3]);
                String clientID = obj[3].toString();
                space.put(clientID, "ok");
                System.out.println("Player " + clientID + " is added");
                updateGameState(space, obj[1].toString(), GameState.START);
                Thread.sleep(10000);
                updateGameState(space, obj[1].toString(), GameState.STOP);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    void updateGameState(Space space, String gameId, GameState state) throws Exception{
        space.getp(new ActualField(gameId),new FormalField(Integer.class));
        space.put(gameId, state.value);

        System.out.println(GameState.START.value);
    }

}

enum GameState {
    STOP(0),
    START(1),
    PAUSE(2);
    public final int value;

    GameState(int value) {
        this.value = value;
    }
}
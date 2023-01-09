package dtu.group8.server;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

public class ClientServer implements Runnable{
    private GameController gameController;
    private Space space;
    public ClientServer(Space space){
        this.space = space;
        this.gameController = new GameController(space);
    }

    @Override
    public void run() {
        try {
            gameController.startGame();

            //space.put("joinMe");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

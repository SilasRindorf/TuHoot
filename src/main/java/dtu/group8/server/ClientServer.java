package dtu.group8.server;

import org.jspace.Space;

public class ClientServer implements Runnable {
    private final GameController gameController;

    public ClientServer(Space space) {
        this.gameController = new GameController(space);
    }

    @Override
    public void run() {
        try {
            gameController.startGame();

            //space.put("joinMe");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

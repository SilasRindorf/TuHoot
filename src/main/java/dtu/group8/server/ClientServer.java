package dtu.group8.server;

public class ClientServer implements Runnable {
    private final GameController gameController;

    public ClientServer(Game game) {
        this.gameController = new GameController(game);
    }

    @Override
    public void run() {
        try {
            gameController.startGame();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

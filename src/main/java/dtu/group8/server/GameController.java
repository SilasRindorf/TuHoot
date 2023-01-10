package dtu.group8.server;

import dtu.group8.server.model.GameState;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

public class GameController {
    Game game;
    Space space;

    public GameController(Space space) {
        this.game = new Game();
        this.space = space;
    }


    private boolean isAlive = true;

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
    public void startGame() throws InterruptedException {
        // Add players to game
        for (Object[] t : space.getAll(new ActualField("add"),new FormalField(String.class))){
            game.addPlayer(t[1].toString());
        }
        game.selectNewQuestion();
        space.put("Q",game.getCurrentQuestion());
        long timeMillis = System.currentTimeMillis();
        long end = timeMillis+30000;
        while(System.currentTimeMillis() < end) {
            // do something
            Object[] t = space.get(new ActualField("A"),new FormalField(String.class),new FormalField(String.class));
            game.correctAnswer(t[2].toString());
            space.put("V",t[1], game.correctAnswer(t[2].toString()));

        }
        isAlive = false;
        while (isAlive){

        }
    }

    private void updateGameState(Space space, GameState state) throws Exception{
        space.getp(new ActualField("gameState"),new FormalField(Integer.class));
        space.put("gameState", state.value);

    }
}

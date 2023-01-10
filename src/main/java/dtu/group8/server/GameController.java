package dtu.group8.server;

import dtu.group8.server.model.GameState;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

public class GameController {
    private final String TAG = "GameController";
    public Game game;
    private Space space;

    public GameController(Space space) {
        this.game = new Game();
        this.space = space;
    }


    private boolean isAlive = true;

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public void startGame() throws InterruptedException {
        Printer.getInstance().print(TAG,"starting game...",Printer.PrintColor.CYAN);
        Printer.getInstance().print(TAG,"Adding players...",Printer.PrintColor.CYAN);

        // Add players to game
        for (Object[] t : space.getAll(new ActualField("add"),new FormalField(String.class))){
            game.addPlayer(t[1].toString());
        }
        game.selectNewQuestion();
        while (game.getCurrentQuestion() != null && isAlive){
            space.put("Q",game.getCurrentQuestion());
            long timeMillis = System.currentTimeMillis();
            long end = timeMillis+30000;
            while(System.currentTimeMillis() < end) {
                // do something
                Object[] t = space.get(new ActualField("A"),new FormalField(String.class),new FormalField(String.class));
                game.checkAnswer(t[2].toString());
                space.put("V",t[1], game.checkAnswer(t[2].toString()));
            }
            updateGameState(space,GameState.NEXT);
            space.put("A",game.getCurrentAnswer());
            game.selectNewQuestion();
        }
    }


    private void updateGameState(Space space, GameState state) throws InterruptedException{
        space.getp(new ActualField("gameState"),new FormalField(Integer.class));
        space.put("gameState", state.value);

    }
}

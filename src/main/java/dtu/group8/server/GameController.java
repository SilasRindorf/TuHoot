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
        Printer printer = new Printer();
        printer.setDefaultTAG( TAG + ":startGame");
        printer.setDefaultPrintColor(Printer.PrintColor.CYAN);
        printer.println("starting game...");
        printer.println("Adding players...");
        // Add players to game
        for (Object[] t : space.getAll(new ActualField("add"),new FormalField(String.class),new FormalField(String.class))){
            game.addPlayer(t[2].toString());
            printer.println("PLAYER ADD ",t[2].toString(), Printer.PrintColor.RED);
            space.put(t[2],"ok");
        }
        printer.println("Done adding players");
        printer.println("Selecting question");
        updateGameState(space,GameState.START);
        game.selectNewQuestion();
        printer.println("Selected question");
        while (game.getCurrentQuestion() != null && isAlive){
            space.put("Q",game.getCurrentQuestion());

            printer.println("Waiting for answers");
            long timeMillis = System.currentTimeMillis();
            long end = timeMillis+10000;
            while(System.currentTimeMillis() < end) {
                // do something
                Thread.sleep(2000);
                Object[] t = space.getp(new ActualField("A"),new FormalField(String.class),new FormalField(String.class));
                if (t != null) {
                    printer.println("Checking submitted answer=\"" + t[2].toString() + "\" for question=\"" + game.getCurrentQuestion() + "\"");
                    game.checkAnswer(t[2].toString());
                    printer.println("Replying to answer");
                    space.put("V", t[1], game.checkAnswer(t[2].toString()) );
                }
            }
            printer.println("No longer waiting for answers");
            space.get(new ActualField("Q"), new FormalField(String.class));
            printer.println("Updating game state");

            printer.println("Sending correct answer");
            space.getp(new ActualField("A"), new FormalField(String.class));
            space.put("A",game.getCurrentAnswer());
            printer.println("Selecting new question");
            game.selectNewQuestion();
        }
    }


    private void updateGameState(Space space, GameState state) throws InterruptedException{
        space.getp(new ActualField("gameState"),new FormalField(Integer.class));
        space.put("gameState", state.value);

    }
}

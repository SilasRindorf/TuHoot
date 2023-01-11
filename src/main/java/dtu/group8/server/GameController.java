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


    private boolean alive = true;

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void startGame(){
        Printer printer = new Printer();
        printer.setDefaultTAG( TAG + ":startGame");
        printer.setDefaultPrintColor(Printer.PrintColor.CYAN);
        printer.println("starting game...");
        printer.println("Adding players...");
        Object[] answer;
        // Add players to game
        try {
            for (Object[] t : space.getAll(new ActualField("add"), new FormalField(String.class), new FormalField(String.class))) {
                game.addPlayer(t[2].toString());
                printer.println("PLAYER ADD ", t[2].toString(), Printer.PrintColor.RED);
                space.put(t[2], "ok");
            }
            printer.println("Done adding players");
            updateGameState(GameState.START);
        } catch (InterruptedException e ){
            printer.println("Error adding players", Printer.PrintColor.RED);
            e.printStackTrace();
        }
        try {
            space.put("QuizSize", game.quizSize());
            for (int i = 0; i < game.quizSize(); i++){
                space.put("Q"+ i, game.getQuestion(i));
                space.put("CA" + i,game.getAnswer(i));
            }

            while (alive) {
                answer = space.get(new ActualField("A"), new FormalField(String.class), new FormalField(String.class), new FormalField(Integer.class));
                space.put("V",answer[1].toString(),game.checkAnswer((Integer) answer[3],answer[2].toString()));
            }
        } catch (InterruptedException e){
            printer.println("Error in game loop", Printer.PrintColor.RED);
            e.printStackTrace();
        }
    }




    private void updateGameState(GameState state) throws InterruptedException{
        space.getp(new ActualField("gameState"),new FormalField(Integer.class));
        space.put("gameState", state.value);

    }
}

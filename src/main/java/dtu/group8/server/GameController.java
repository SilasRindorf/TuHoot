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
        Printer log = new Printer();
        log.setDefaultTAG( TAG + ":startGame");
        log.setDefaultPrintColor(Printer.PrintColor.CYAN);
        log.println("starting game...");
        log.println("Adding players...");
        Object[] answer;
        // Add players to game
        try {
            for (Object[] t : space.getAll(new ActualField("add"), new FormalField(String.class), new FormalField(String.class))) {
                game.addPlayer(t[2].toString());
                log.println("PLAYER ADD ", t[2].toString(), Printer.PrintColor.CYAN);
                space.put(t[2], "ok");
            }
            log.println("Done adding players");
            updateGameState(GameState.START);
        } catch (InterruptedException e ){
            log.println("Error adding players", Printer.PrintColor.RED);
            e.printStackTrace();
        }

        /**
         * TODO : Skal køres i runder a et givent antal tid
         * TODO : Der skal gives point
         * TODO : Highscores skal ud i tuple space?
         * TODO : Alle får 500 point for et rigtigt svar lige nu
         */

        try {
            space.put("QuizSize", game.quizSize());
            for (int i = 0; i < game.quizSize(); i++){
                space.put("Q"+ i, game.getQuestion(i));
                space.put("CA" + i,game.getAnswer(i));
                log.println("Round begins " + i);
                while (alive && !game.allAnsweredCorrect(i)) {
                    log.println("Waiting for answers");
                    log.println("\t","game.allAnsweredCorrect " + game.allAnsweredCorrect(i));
                    //Tuple contains:
                    //'A', clientId, answer, question index
                    answer = space.get(new ActualField("A"), new FormalField(String.class), new FormalField(String.class), new FormalField(Integer.class));
                    space.put("V", answer[1].toString(), game.checkAnswer((Integer) answer[3], answer[2].toString(), answer[1].toString()));
                    log.println("\t","Player  " + game.getPlayers().get(0).toString() + game.getPlayers().get(0).getPoint());
                }
                log.println("Round ends " + i);
            }
        } catch (InterruptedException e){
            log.println("Error in game loop", Printer.PrintColor.RED);
            e.printStackTrace();
        }
    }


    private void updateGameState(GameState state) throws InterruptedException{
        space.getp(new ActualField("gameState"),new FormalField(Integer.class));
        space.put("gameState", state.value);

    }
}

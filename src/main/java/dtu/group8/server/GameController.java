package dtu.group8.server;

import dtu.group8.server.model.GameState;
import dtu.group8.server.model.Player;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.ArrayList;

public class GameController {
    public Game game;
    private final Space space;
    private final Printer log;
    private boolean alive = true;
    private Printer printer;


    public GameController(Game game) {
        this.log = new Printer();
        log.setLog(false);
        log.setDefaultTAG("GameController");
        log.setDefaultPrintColor(Printer.PrintColor.CYAN);
        this.game = game;
        this.space = game.getSpace();
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    private void initialize() {
        log.setDefaultTAG("GameController : initialize");
        log.println("starting game...");
        log.println("Adding players...");
        // Add players to game
        try {
            for (Object[] t : space.getAll(new ActualField("add"), new FormalField(String.class), new FormalField(String.class))) {
                game.addPlayer(t[1].toString(), t[2].toString());
                log.println("PLAYER ADD ", t[2].toString(), Printer.PrintColor.CYAN);

            }

            /* The loop below performs the same function as the one above. The difference is
               that the given game parameter already contains all the added players */
            for (Player currPlayer : game.getPlayers()) {
                log.println("PLAYER ADD ", currPlayer.getId(), Printer.PrintColor.CYAN);
                space.put("ACK", currPlayer.getId(), "ok");
            }

            log.println("Done adding players");
            updateGameState(GameState.START);
        } catch (InterruptedException e) {
            log.println("Error adding players", Printer.PrintColor.RED);
            e.printStackTrace();
        }

    }

    private void verifyingAnswers(int index) {
        Object[] answer;
        long time = System.currentTimeMillis();
        long end = System.currentTimeMillis() + 15000;

        try {
            while (alive && !game.allAnsweredCorrect(index) && time < end) {
                time = System.currentTimeMillis();
                log.println("Time left " + (end - time));
                log.println("Waiting for answers");
                //Tuple contains:
                //'A', clientId, answer, question index
                answer = space.get(new ActualField("A"), new FormalField(String.class), new FormalField(String.class), new FormalField(Integer.class));
                space.put("V", answer[1].toString(), game.checkAnswer((Integer) answer[3], answer[2].toString(), answer[1].toString()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void startGame() {
        initialize();

        log.setDefaultTAG("startGame");

        /**
         * TODO: Fix at clients bliver softlocked hvis de ikke svarer på sidste spørgsmål
         * TODO: Hvis der løbes tør for tid og serveren går til næste spørgsmål, kan klienten svare på det gamle / Klienten skal have at vide den skal gå videre
         */

        try {
            space.put("QuizSize", game.quizSize());
            // Play round
            for (int i = 0; i < game.quizSize(); i++) {

                space.put("Q" + i, game.getQuestion(i));
                space.put("CA" + i, game.getAnswer(i));
                log.println("Round " + (i + 1) + " begins");
                verifyingAnswers(i);
                log.println("Game info: " + game.allAnsweredCorrect(i), Printer.PrintColor.GREEN);

                log.println("Round " + (1 + i) + " ends");
                handleHighScores();
            }
            //___________________ GAME END ___________________

        } catch (InterruptedException e) {
            log.println("Error in game loop", Printer.PrintColor.RED);
            e.printStackTrace();
        }
    }

    private void handleHighScores() {
        for (int k = 0; k < game.getPlayers().size(); k++) {
            log.println("Player  " + game.getPlayers().get(k).getName() + " " + game.getPlayers().get(k).getPoints());
        }

        try {
            space.put("Highscores", game.getScores());
            ackReq();
            space.get(new ActualField("Highscores"), new FormalField(String.class));

        } catch (InterruptedException e) {
            log.println("Error in handleHighScores", Printer.PrintColor.RED);
            e.printStackTrace();
        }
    }

    // TODO: Kick players
    // TODO: Break while after all clients ACK
    private void ackReq() {
        try {
            log.println("Getting client ACKs");
            updateGameState(GameState.ACK);
            log.println("\t", "Listening for ACKs");
            ArrayList<Player> playerList = new ArrayList<>(game.getPlayers());
            Thread ackHandler = new Thread(new ackHandler(game, playerList, player -> {
                playerList.remove(player);
                log.println("\t", "Player with ID" + player.getId() + " has been kicked");

            }));
            ackHandler.start();
            log.println("Ack Thread started");
            log.println("Main thread sleeping...");
            Thread.sleep(15000);
            log.println("Main thread waking...");
            if (ackHandler.isAlive()) {
                log.println("Interrupting Ack Thread");
                ackHandler.interrupt();
            }
            for (Player player : playerList) {
                game.removePlayer(player.getId());
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    void updateGameState(GameState state) throws InterruptedException {
        space.getp(new ActualField("gameState"), new FormalField(Integer.class));
        space.put("gameState", state.value);

    }
}

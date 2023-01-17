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

    public void startGame() {
        Printer printer = new Printer();
        printer.setLog(false);
        printer.setDefaultTAG("startGame");
        printer.setDefaultPrintColor(Printer.PrintColor.CYAN);
        printer.println("starting game...");
        printer.println("Adding players...");
        Object[] answer;
        // Add players to game
        try {
            //Touple contains
            //
            for (Object[] t : space.getAll(new ActualField("add"), new FormalField(String.class), new FormalField(String.class))) {
                game.addPlayer(t[1].toString(), t[2].toString());
                log.println("PLAYER ADD ", t[2].toString(), Printer.PrintColor.CYAN);
/*            for (Object[] t : space.getAll(new ActualField("add"), new FormalField(String.class), new FormalField(String.class))) {
                game.addPlayer(t[2].toString());
                printer.println("PLAYER ADD ", t[2].toString(), Printer.PrintColor.CYAN);
                space.put("ACK", t[2], "ok");
            }*/
            }

            /* The loop below performs the same function as the one above. The difference is
               that the given game parameter already contains all the added players */
            for (Player currPlayer : game.getPlayers()) {
                printer.println("PLAYER ADD ", currPlayer.getId(), Printer.PrintColor.CYAN);
                space.put("ACK", currPlayer.getId(), "ok");
            }

            log.println("Done adding players");
            updateGameState(GameState.START);
        } catch (InterruptedException e) {
            log.println("Error adding players", Printer.PrintColor.RED);
            e.printStackTrace();
        }

        /**
         * TODO: Fix at clients bliver softlocked hvis de ikke svarer på sidste spørgsmål
         * TODO: Hvis der løbes tør for tid og serveren går til næste spørgsmål, kan klienten svare på det gamle / Klienten skal have at vide den skal gå videre
         */

        try {
            space.put("QuizSize", game.quizSize());
            for (int i = 0; i < game.quizSize(); i++) {
                long time = System.currentTimeMillis();
                long end = System.currentTimeMillis() + 15000;
                space.put("Q" + i, game.getQuestion(i));
                space.put("CA" + i, game.getAnswer(i));
                log.println("Round " + (i + 1) + " begins");
                log.println("Game info: " + game.allAnsweredCorrect(i), Printer.PrintColor.GREEN);
                while (alive && !game.allAnsweredCorrect(i) && time < end) {
                    time = System.currentTimeMillis();
                    log.println("Time left " + (end - time));
                    log.println("Waiting for answers");
                    //Tuple contains:
                    //'A', clientId, answer, question index
                    answer = space.get(new ActualField("A"), new FormalField(String.class), new FormalField(String.class), new FormalField(Integer.class));
                    space.put("V", answer[1].toString(), game.checkAnswer((Integer) answer[3], answer[2].toString(), answer[1].toString()));
                    log.println("\t", "game.allAnsweredCorrect " + game.allAnsweredCorrect(i));
                }
                for (int k = 0; k < game.getPlayers().size(); k++) {

                    log.println("Player  " + game.getPlayers().get(k).getName() + " " + game.getPlayers().get(k).getPoints());
                }
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
        try {
            space.put("Highscores", game.getScores());
            handleACK();
            space.get(new ActualField("Highscores"), new FormalField(String.class));

        } catch (InterruptedException e) {
            log.println("Error in handleHighScores", Printer.PrintColor.RED);
            e.printStackTrace();
        }
    }

    // TODO: Kick players
    // TODO: Break while after all clients ACK
    private void handleACK() {
        try {
            log.println("Getting client ACKs");
            updateGameState(GameState.ACK);

            long time = System.currentTimeMillis();
            long end = time + 15000;
            Object[] ACK;
            ArrayList<Player> IDs = new ArrayList<>(game.getPlayers());
            log.println("\t", "Listening for ACKs");
            while (end > System.currentTimeMillis()) {
                // ACK, ClientID, OK/NO
                ACK = space.getp(new ActualField("ACK"), new FormalField(String.class), new FormalField(String.class));
                if (ACK != null && ACK[2].toString().equalsIgnoreCase("ok")) {
                    for (Player player : IDs) {
                        log.println("\t", "Received ACK from: " + player.getId());
                        if (player.getId().equals(ACK[1])) {
                            log.println("\t\t", "ACK was OK");
                            IDs.remove(player);
                            if (IDs.size() == 0)
                                updateGameState(GameState.CONTINUE);
                            return;
                        }
                    }
                }
                updateGameState(GameState.CONTINUE);
            }
            for (Player player : IDs) {
                log.println("\t", "Player with ID" + player.getId() + " has been kicked");
                game.removePlayer(player.getId());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void updateGameState(GameState state) throws InterruptedException {
        space.getp(new ActualField("gameState"), new FormalField(Integer.class));
        space.put("gameState", state.value);

    }
}

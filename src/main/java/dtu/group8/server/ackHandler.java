package dtu.group8.server;

import dtu.group8.server.model.GameState;
import dtu.group8.server.model.Player;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;

import java.util.ArrayList;

public class ackHandler implements Runnable {
    private Game game;
    private OnComplete onComplete;

    public ackHandler(Game game, OnComplete onComplete) {
        this.game = game;
        this.onComplete = onComplete;
    }


    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        Printer log = new Printer("ACK_HANDLER", Printer.PrintColor.PURPLE);
        Object[] ACK;
        ArrayList<Player> IDs = new ArrayList<>(game.getPlayers());
        try {
            ACK = game.getSpace().get(new ActualField("ACK"), new FormalField(String.class), new FormalField(String.class));
            for (int i = 0; i < IDs.size(); i++) {
                if (ACK[2].toString().equalsIgnoreCase("ok")) {
                    for (Player player : IDs) {
                        log.println("\t", "Received ACK from: " + player.getId());
                        if (player.getId().equals(ACK[1])) {
                            log.println("\t\t", "ACK was OK");
                            IDs.remove(player);
                            if (IDs.size() == 0)
                                onComplete.run();
                            return;
                        }
                    }
                }
            }
            for (Player player : IDs) {
                log.println("\t", "Player with ID" + player.getId() + " has been kicked");
                game.removePlayer(player.getId());
            }
            onComplete.run();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
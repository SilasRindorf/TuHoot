package dtu.group8.server;

import dtu.group8.server.model.Player;
import dtu.group8.util.Printer;
import org.jspace.ActualField;
import org.jspace.FormalField;

import java.util.ArrayList;

public class ackHandler implements Runnable {
    private Game game;
    private OnStateChange onStateChange;
    private ArrayList<Player> IDs;

    public ackHandler(Game game, ArrayList<Player> players, OnStateChange onStateChange) {
        this.game = game;
        this.onStateChange = onStateChange;
        IDs = players;
    }


    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        Printer log = new Printer("ACK_HANDLER", Printer.PrintColor.PURPLE);
        Object[] ACK;
        try {
            for (Player player : game.getPlayers()
            ) {
                log.println(player.getName() + " " + player.getId());
            }
            for (int i = 0; i < game.getPlayers().size(); i++) {
                log.println("Amount of ACK received: " + i);
                ACK = game.getSpace().get(new ActualField("ACK"), new FormalField(String.class), new FormalField(String.class));
                if (ACK[2].toString().equalsIgnoreCase("ok")) {
                    for (Player player : IDs) {
                        log.println("\t", "Received ACK from: " + player.getId() + " " + player.getName());
                        if (player.getId().equals(ACK[1])) {
                            log.println("\t\t", "ACK was OK from " + player.getId());
                            IDs.remove(player);
                            if (IDs.size() == 0)
                                return;
                        }
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
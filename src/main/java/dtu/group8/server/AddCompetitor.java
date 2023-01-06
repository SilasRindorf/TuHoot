package dtu.group8.server;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

class AddCompetitor implements Runnable {
    Space space;
    public AddCompetitor(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        try {
            space.get(new ActualField("add"), new FormalField(String.class));
            // TODO Add the player..

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
package dtu.group8.server;

import org.jspace.Space;

public class ClientServer implements Runnable{
    private Space space;
    public ClientServer(Space space){
        this.space = space;
    }

    @Override
    public void run() {

    }
}

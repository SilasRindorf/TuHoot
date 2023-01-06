package dtu.group8.server;

import dtu.group8.server.model.GameState;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

public class GameController {
    Game game;
    Space space;

    public GameController(Game game, Space space) {
        this.game = game;
        this.space = space;
    }



    void updateGameState(Space space, String gameId, GameState state) throws Exception{
        space.getp(new ActualField(gameId),new FormalField(Integer.class));
        space.put(gameId, state.value);

    }
}

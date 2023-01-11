package dtu.group8.server.model;

public enum GameState {
    STOP(0),
    START(1),
    PAUSE(2),
    ACK(3),
    NEXT(4),
    CONTINUE(5);
    public final int value;

    GameState(int value) {
        this.value = value;
    }
}

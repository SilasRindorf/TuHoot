package dtu.group8.server.model;

public class Player {
    public String id;
    private int point = 0;
    Player(String playerId){
        this.id = playerId;
    }
    void addPoint(int point) {
        this.point += point;
    }

    public int getPoint() {
        return point;
    }
}

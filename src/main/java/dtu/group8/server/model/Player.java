package dtu.group8.server.model;

public class Player {
    private String id;
    private int point = 0;
    public Player(String playerId){
        this.id = playerId;
    }
    void addPoint(int point) {
        this.point += point;
    }

    public int getPoint() {
        return point;
    }

    public String getId() {
        return id;
    }
}

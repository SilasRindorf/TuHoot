package dtu.group8.server.model;

public class Player {
    private String name;
    private String id;
    private int point;

    public Player(String id) {
        this.id = id;
        this.point = 0;
    }

    public Player(String name, String playerId, int point){
        this.name = name;
        this.id = playerId;
        this.point = point;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
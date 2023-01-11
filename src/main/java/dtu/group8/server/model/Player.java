package dtu.group8.server.model;

public class Player {
    private String name;
    private String id;
    private int point = 0;


    public Player(/*Identifying string of a client*/String playerId){
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
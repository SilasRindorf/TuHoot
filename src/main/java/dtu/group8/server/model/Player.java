package dtu.group8.server.model;

public class Player implements Comparable<Player> {
    private String name;
    private int point;
    private final String id;
    private int points = 0;

    public Player(String id) {
        this.id = id;
        this.point = 0;
    }

    public Player(String name, String playerId, int point){
        this.name = name;
        this.id = playerId;
        this.point = point;
    }

    public Player(String playerId, String name) {
        this.id = playerId;
        this.name = name;
    }

    void addPoint(int point) {
        this.points += point;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points += points;
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

    @Override
    public int compareTo(Player o) {
        return this.getPoints() - o.getPoints();
    }
}
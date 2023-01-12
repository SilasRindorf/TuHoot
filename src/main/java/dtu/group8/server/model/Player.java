package dtu.group8.server.model;

public class Player implements Comparable<Player> {
    private String name;
    private String id;
    private int point;
    private final String id;
    private int point = 0;

    public Player(String id) {
        this.id = id;
        this.point = 0;
    }

    public Player(String name, String playerId, int point){
        this.name = name;
    public Player(String playerId) {
        this.id = playerId;
        this.point = point;
    }

    void addPoint(int point) {
        this.point += point;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point += point;
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
        return this.getPoint() - o.getPoint();
    }
}
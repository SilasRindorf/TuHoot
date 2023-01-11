package dtu.group8.client;

public class Main {


    public static void main(String[] args) {
        Client client = new Client();
        client.start(client.setup());
        System.exit(0);
    }
}
package dtu.group8;

import dtu.group8.client.Client;

public class Main2 {
    public static void main(String[] args) {
        Client client = new Client();
        client.start(client.matchMake());
        System.exit(0);
    }
}

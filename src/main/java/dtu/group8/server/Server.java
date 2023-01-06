package dtu.group8.server;

import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class Server {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
    public Server(){
    }

    public void start() {
        try {

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            // Create a repository
            SpaceRepository repository = new SpaceRepository();
            // Create a local space for the chat messages
            SequentialSpace space = new SequentialSpace();

            // Add the space to the repository
            repository.add("chat", space);


            // Set the URI of the chat space
            System.out.print("Enter URI of the chat server or press enter for default: ");
            String uri = input.readLine();
            // Default value
            if (uri.isEmpty()) {
                //uri = "tcp://127.0.0.1:9001/?keep";
                uri = "tcp://10.209.95.114:9002/?keep";

            }

            new Thread(new TuHootGame(space)).start();
            System.out.println("Space from the main " +  space);

            // Open a gate
            URI myUri = new URI(uri);
            String gateUri = "tcp://" + myUri.getHost() + ":" + myUri.getPort() + "?keep" ;
            System.out.println("Opening repository gate at " + gateUri + "...");
            repository.addGate(gateUri);


        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}

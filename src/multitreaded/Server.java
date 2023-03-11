package multitreaded;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private final int port;
    private final List<PrintStream> clients;
    private ServerSocket server;

    public static void main(String[] args) throws IOException {
        new Server(3500).run();
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void run() throws IOException {
        server = new ServerSocket(port);
        System.out.printf("Port %d is now open.%n", this.port);

        do {
            Socket client = server.accept();
            System.out.println("New client connected!!!");
            clients.add(new PrintStream(client.getOutputStream()));

            new Thread(new ClientHandler(this,
                    client.getInputStream())).start();

        } while (this.clients.size() <= 20); // only accepts 20 clients per server

    }

    void broadcastMessages(String msg) {
        System.out.println("Broadcasting message => " + msg);
        for (PrintStream client : this.clients) {
            client.println(msg);
        }
    }

    private static class ClientHandler implements Runnable {
        Server server;
        InputStream client;

        public ClientHandler(Server server, InputStream client) {
            this.server = server;
            this.client = client;
        }

        @Override
        public void run() {
            try {
                String message;
                Scanner sc = new Scanner(this.client);
                while (sc.hasNextLine()) {
                    message = sc.nextLine();
                    server.broadcastMessages(message);
                }
                this.client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

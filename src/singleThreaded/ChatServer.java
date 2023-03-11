package singleThreaded;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    static List<RunnableClient> clients = new ArrayList<>();

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(3500);
            serverSocket.setReuseAddress(true);

            while(true) {
                Socket s = serverSocket.accept();
                System.out.println("Waiting for client connections");

                RunnableClient client = new RunnableClient(s);
                Thread clientThread = new Thread(client);
                clientThread.start();
                clients.add(client);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


class RunnableClient implements Runnable {
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;

    public RunnableClient(Socket s) {
        try {
            socket = s;
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // receiving message from the client
            String message = dis.readUTF();

            System.out.println(message);

            // sending a message back to the client
            for (RunnableClient client : ChatServer.clients) {
                if(!client.socket.isClosed()) {
                    client.getDos().writeUTF(message);
                    client.getDos().flush();
                }
            }

            // close the connection
            socket.close();

            ChatServer.clients.remove(this);
            // Close the connection if the message is exit
//            if (message.toLowerCase(Locale.ROOT).equals("exit")) break;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized Socket getSocket() {
        return socket;
    }

    public DataOutputStream getDos() {
        return dos;
    }
}
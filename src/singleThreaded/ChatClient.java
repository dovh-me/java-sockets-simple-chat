package singleThreaded;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.start();
    }

    public void start() {

        try(
                Scanner scanner = new Scanner(System.in);

                ) {
            while(true) {
                Socket socket = new Socket(InetAddress.getLocalHost(),3500);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                System.out.print("Enter message: ");
                String userInput = scanner.nextLine();

                dos.writeUTF(userInput);
                dos.flush();

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String ackMessage = dis.readUTF();

                System.out.println("server: " + ackMessage);

                if(userInput.toLowerCase(Locale.ROOT).equals("exit")) break;
                socket.close();
            }

        } catch (IOException e) {
            System.out.printf("Error: %s%n", e.getMessage());
            e.printStackTrace();
        }
    }
}

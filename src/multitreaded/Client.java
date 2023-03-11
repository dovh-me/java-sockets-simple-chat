package multitreaded;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Scanner;

public class Client {
    private String host;
    private int port;
    private String nickname;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws UnknownHostException, IOException {
        Socket clientSocket = new Socket(InetAddress.getByName(this.host), port);
        System.out.println("Client connection success");

        new Thread(new
                IncomingMessageHandler(clientSocket.getInputStream())).start();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter nickname: ");
        nickname = scanner.nextLine();

        PrintStream ps = new PrintStream(clientSocket.getOutputStream());
        while(true) {
            System.out.println("Enter Message");
            String userInput = scanner.nextLine();
            if(userInput.toLowerCase(Locale.ROOT).equals("exit")) break;

            ps.println(nickname +": "+userInput);
            ps.flush();
        }

        ps.close();
        scanner.close();
        clientSocket.close();
    }

    class IncomingMessageHandler implements Runnable{
        private final InputStream server;

        public IncomingMessageHandler(InputStream server) {
            this.server = server;
        }

        @Override
        public void run() {
            Scanner s = new Scanner(server);
            while (s.hasNextLine()) {
                System.out.println(s.nextLine());
            }
            s.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 3500);
        client.run();
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final List<ClientHandler> clients;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket clientSocket, List<ClientHandler> clients) {
        this.clientSocket = clientSocket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Kullanıcı adınızı girin:");
            username = in.readLine();
            broadcast(username + " sohbete katıldı.");

            String message;
            while ((message = in.readLine()) != null) {
                broadcast(username + ": " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                clients.remove(this);
                broadcast(username + " sohbetten ayrıldı.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            if (client != this) {
                client.sendMessage(message);
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple server class that listens on a specified port for multiple client connections.
 * Each client is handled in its own thread. The server responds to a special 'HEARTBEAT' message with 'ALIVE'.
 */
public class ServerProcess {
    /**
     * Starts the server, accepts multiple clients, and handles each in a separate thread.
     * @param port The port number for the server to listen on.
     */
    public void process(int port) {
        Thread serverThread = new Thread(() -> runServer(port));
        serverThread.start();
    }

    // The main server loop: accepts clients and starts a handler thread for each
    private void runServer(int port) {
        try (var serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port: " + port);
            while (true) {
                var client = serverSocket.accept();
                new Thread(() -> handleClient(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handles communication with a single client
    private void handleClient(Socket client) {
        try (var clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
             var clientOutput = new PrintWriter(client.getOutputStream(), true)) {
            String line;
            while ((line = clientInput.readLine()) != null) {
                if ("HEARTBEAT".equals(line)) {
                    clientOutput.println("ALIVE");
                } else {
                    System.out.println("Client says: " + line);
                    clientOutput.println("type shiii");
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected or error occurred.");
        }
    }
}

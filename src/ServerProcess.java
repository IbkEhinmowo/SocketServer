import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Abstract base class for all server processes.
 * Contains common server behavior that both Primary and Backup servers share.
 */
public abstract class ServerProcess {
    protected volatile boolean isPrimary = false;
    protected volatile boolean running = true;
    // Unique identifier for this server (provided via constructor)
    protected final String serverId;
    private ServerSocket serverSocket;

    // New constructor that accepts a server ID
    protected ServerProcess(String serverId) {
        this.serverId = (serverId == null ? "SERVER-UNKNOWN" : serverId);
    }

    /**
     * Starts the server, accepts multiple clients, and handles each in a separate thread.
     * @param port The port number for the server to listen on.
     */
    public void process(int port) {
        Thread serverThread = new Thread(() -> runServer(port));
        serverThread.start();
        // Start heartbeat sender as a daemon thread so this server notifies the Monitor
        Thread hb = new Thread(this::sendHeartbeats, "heartbeat-sender");
        hb.setDaemon(true);
        hb.start();
    }

    // The main server loop: accepts clients and starts a handler thread for each
    private void runServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + port);
            while (running) {
                var client = serverSocket.accept();
                new Thread(() -> handleClient(client)).start();
            }
        } catch (IOException e) {
            if (running) e.printStackTrace();
        }
    }

    // Handles communication with a single client
    private void handleClient(Socket client) {
        try (var clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
             var clientOutput = new PrintWriter(client.getOutputStream(), true)) {
            String line;
            while ((line = clientInput.readLine()) != null) {
                if ("PROMOTE".equals(line)) {
                    isPrimary = true;
                    onPromotedToPrimary(); // Hook for subclasses
                    clientOutput.println("PROMOTED");
                } else if (isPrimary) {
                    System.out.println("Client says: " + line);
                    clientOutput.println("type shii");
                } else {
                    clientOutput.println("NOT PRIMARY");
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected or error occurred.");
        }
    }
    // Runs forever, sending heartbeats
    private void sendHeartbeats() {
        while (running) {
            try {
                sendHeartbeat();
                Thread.sleep(2000); // Wait 2 seconds
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    // Send one heartbeat message
    private void sendHeartbeat() {
        try (Socket socket = new Socket("localhost", 9000); // Connect to monitor
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Send the standard heartbeat format the Monitor expects
            out.println(serverId);

        } catch (IOException e) {
            System.out.println("Failed to send heartbeat");
        }
    }


    /**
     * Hook method for subclasses to override.
     * Called when this server is promoted to primary.
     */
    protected abstract void onPromotedToPrimary();

    /**
     * Stops the server process gracefully.
     */
    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try { serverSocket.close(); } catch (IOException ignored) {}
        }
    }
}
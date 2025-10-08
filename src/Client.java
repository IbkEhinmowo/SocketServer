import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 8090;
    private static final int RECONNECT_INTERVAL = 5000; // 5 seconds

    /**
     * The main method is the entry point for the client application.
     * It establishes a connection to the server and sends a message.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner consoleInput = new Scanner(System.in);

        while (true) {
            try {
                connectAndRun(consoleInput);
            } catch (IOException e) {
                System.out.println("Connection failed. Retrying in " + RECONNECT_INTERVAL / 1000 + " seconds...");
                try {
                    Thread.sleep(RECONNECT_INTERVAL);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        consoleInput.close();
    }

    private static void connectAndRun(Scanner consoleInput) throws IOException {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to server. Type messages to send. Type 'exit' to quit.");

            // Thread to read from server - will naturally exit when connection dies
            Thread readerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println("server said: " + serverMessage);
                    }
                } catch (IOException e) {
                    // Connection died
                }
            });
            readerThread.start();

            // Main thread reads user input and sends to server
            while (true) {
                if (!consoleInput.hasNextLine()) {
                    break; // EOF on stdin
                }

                String message = consoleInput.nextLine();

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnected.");
                    System.exit(0);
                }

                out.println(message);

                // If write fails, socket is closed
                if (out.checkError()) {
                    throw new IOException("Connection lost");
                }
            }
        }
    }
}

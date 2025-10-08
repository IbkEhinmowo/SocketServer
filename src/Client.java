import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    /**
     * The main method is the entry point for the client application.
     * It establishes a connection to the server and sends a message.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Use a try-with-resources statement to ensure the Socket and PrintWriter are closed automatically.
        try (var socket = new Socket("localhost", 8090);
             // Create a PrintWriter to send text to the server.
             // The 'true' argument enables auto-flushing, which sends the data immediately apparently.
             var out = new PrintWriter(socket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var consoleInput = new java.util.Scanner(System.in)) {

            // Start a thread to display server output
            Thread serverOutputThread = new Thread(() -> {
                try {
                    String serverLine;
                    while ((serverLine = in.readLine()) != null) {
                        System.out.println("server said: " + serverLine);
                    }
                } catch (IOException e) {
                    System.out.println("Lost connection to server.");
                }
            });
            serverOutputThread.setDaemon(true);
            serverOutputThread.start();

            System.out.println("Connected to server. Type messages to send. Type 'exit' to quit.");
            String message;
            while (true) {
                try {
                    Thread.sleep(1000); // Small delay to prevent busy waiting
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                }
                System.out.print("Enter message: "); // Use print instead of println for prompt
                message = consoleInput.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                out.println(message);
            }
            System.out.println("Client disconnected.");


        } catch (IOException e) {
            // If an I/O error occurs (e.g., the server is not running), print the stack trace.
            e.printStackTrace();
        }
    }
}

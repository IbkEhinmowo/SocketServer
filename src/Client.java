import java.io.IOException;
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
        try (var socket = new Socket("localhost", 8080);
             // Create a PrintWriter to send text to the server.
             // The 'true' argument enables auto-flushing, which sends the data immediately apparently.
             var Message = new PrintWriter(socket.getOutputStream(), true)) {

            // Announce that the connection was successful.
            System.out.println("Connected to server. Sending message...");
            // Send a line of text to the server.
            Message.println("Hello from the client!");

        } catch (IOException e) {
            // If an I/O error occurs (e.g., the server is not running), print the stack trace.
            e.printStackTrace();
        }
    }
}

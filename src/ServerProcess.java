import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

/**
 * A simple server class that listens on a specified port for a single client connection.
 * It reads lines of text from the client and prints them to the console.
 */
public class ServerProcess {
    /**
     * Starts the server, waits for a client to connect, and then processes its input stream.
     * @param port The port number for the server to listen on.
     */
    public void process(int port) {
        // Use a try-with-resources statement to ensure the ServerSocket is closed automatically.
        try (var serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port: " + port);
            // Wait for a client to connect and accept the connection.
            var client = serverSocket.accept();
            var clientIP = client.getInetAddress().getHostAddress();
            var clientPort = client.getPort();
            // Create a BufferedReader to read text from the client's input stream.
            var clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line;
            // Loop as long as there are lines to read from the client, print it out
            while ((line = clientInput.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            // If an I/O error occurs, print trace.
            e.printStackTrace();
        }
    }
}

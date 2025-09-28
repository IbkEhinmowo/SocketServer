import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A client that connects to the server and periodically sends HEARTBEAT messages.
 * If the connection is lost, it keeps trying to reconnect and resumes heartbeats when possible.
 * Can be stopped with Ctrl+C.
 */
public class HeartbeatClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;
        int intervalMillis = 1000; // 1 second between heartbeats
        int reconnectMillis = 2000; // 2 seconds between reconnect attempts

        while (true) {
            try (var socket = new Socket(host, port);
                 var out = new PrintWriter(socket.getOutputStream(), true);
                 var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("Connected to server. Sending heartbeats every " + (intervalMillis/1000.0) + " seconds.");
                while (true) {
                    // Send heartbeat
                    out.println("HEARTBEAT");
                    System.out.println("Sent: HEARTBEAT");
                    // Wait for server response
                    String response = in.readLine();
                    if (response == null) {
                        System.out.println("Server closed the connection or no response. Will attempt to reconnect.");
                        break;
                    }
                    System.out.println("Received: " + response);
                    // Wait before sending the next heartbeat
                    Thread.sleep(intervalMillis);
                }
            } catch (IOException e) {
                System.out.println("Could not connect to server or lost connection. Retrying in " + (reconnectMillis/1000.0) + " seconds...");
            } catch (InterruptedException e) {
                System.out.println("Heartbeat client interrupted.");
                break;
            }
            // Wait before attempting to reconnect
            try {
                Thread.sleep(reconnectMillis);
            } catch (InterruptedException e) {
                System.out.println("Heartbeat client interrupted during reconnect wait.");
                break;
            }
        }
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class PrimaryServer extends ServerProcess {
    protected void handleLine(String line, String clientIP, int clientPort) {
        System.out.printf("Received from %s:%d: %s\n", clientIP, clientPort, line);
    }
}




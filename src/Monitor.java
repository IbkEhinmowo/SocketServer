import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Monitor {
    public static void main(String[] args) {
        final int PORT = 9000;
        final int TIMEOUT_MS = 5000; // consider a server dead after 5s of no heartbeat

        // Map of serverId -> last seen timestamp (ms)
        final Map<String, Long> lastSeen = new ConcurrentHashMap<>();
        // Set of currently-known-alive servers
        final Set<String> alive = ConcurrentHashMap.newKeySet();

        try (ServerSocket ss = new ServerSocket(PORT)) {
            ss.setSoTimeout(TIMEOUT_MS);
            System.out.println("Monitor listening for heartbeats on port " + PORT);

            while (true) {
                try {
                    Socket s = ss.accept(); // will throw SocketTimeoutException when no connection within timeout
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                        String line = in.readLine();
                        if (line != null) {
                            // Treat any non-empty trimmed line as the server id (no special parsing)
                            String id = line.trim();

                            if (!id.isEmpty()) {
                                long now = System.currentTimeMillis();
                                lastSeen.put(id, now);
                                if (!alive.contains(id)) {
                                    alive.add(id);
                                    System.out.println("Server " + id + " alive (heartbeat received)");
                                }
                            }
                        }
                    } catch (IOException ignored) {
                        // ignore per original behaviour
                    } finally {
                        try { s.close(); } catch (IOException ignored) {}
                    }
                } catch (SocketTimeoutException e) {
                    // On timeout, check for servers that haven't sent heartbeats recently
                    long now = System.currentTimeMillis();
                    for (String id : lastSeen.keySet()) {
                        Long t = lastSeen.get(id);
                        if (t == null) continue;
                        if (alive.contains(id) && now - t > TIMEOUT_MS) {
                            alive.remove(id);
                            System.out.println("Server " + id + " dead (no heartbeat for " + (now - t) + "ms)");
                        }
                    }
                    // loop and wait for next heartbeat
                }
            }
        } catch (IOException e) {
            System.err.println("Monitor failed: " + e.getMessage());
        }
    }
}

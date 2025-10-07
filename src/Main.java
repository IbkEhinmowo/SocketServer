/**
 * The Main class is the entry point for the server application.
 */
public class Main {
    /**
     * The main method creates a new ServerProcess instance and starts the server on port 8080.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Create a new instance of the server.
        var server = new primary();
        var backupServer = new backup();

        // Announce that the server is about to start.
        System.out.println("Server process starting...");

        // Start the server process, listening on port 8090. This line will block
        // and keep the server running.
        server.process(8090);
        backupServer.process(8089);
    }
}

/**
 * The Main class is the entry point for the server application.
 */
public class Main {
    // Define static ports for clarity and easier management.
    public static final int PRIMARY_PORT = 8090;
    public static final int BACKUP_PORT = 8089;

    /**
     * The main method creates and starts the primary and backup servers, each in its own thread.
     * It also starts the monitor to watch over the primary server.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        var primaryServer = new primary();
        var backupServer = new backup();

        // Announce that the servers are about to start.
        System.out.println("Server processes starting...");

        // Start the primary server. The process() method handles its own threading.
        primaryServer.process(PRIMARY_PORT);
        System.out.println("Primary server started on port " + PRIMARY_PORT);

        // Start the backup server. The process() method handles its own threading.
        backupServer.process(BACKUP_PORT);
        System.out.println("Backup server started on port " + BACKUP_PORT);
    }
}

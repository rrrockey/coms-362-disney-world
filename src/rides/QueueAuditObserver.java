package rides;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Observer that logs all queue operations to a file.
 * Demonstrates the Observer pattern by maintaining an audit trail of queue activity
 * without coupling to the Ride or ManageRideQueues classes.
 *
 * @author ryanrockey
 */
public class QueueAuditObserver implements QueueObserver {

    private static final String AUDIT_LOG = "data/queue_audit.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Initializes the audit log file if it doesn't exist.
     */
    public static void initialize() throws IOException {
        Files.createDirectories(Paths.get("data"));
        if (!Files.exists(Paths.get(AUDIT_LOG))) {
            Files.writeString(Paths.get(AUDIT_LOG), 
                    "[AUDIT LOG INITIALIZED] " + LocalDateTime.now().format(formatter) + "\n");
        }
    }

    @Override
    public void onGuestAdded(Ride ride, int guestId, int position) {
        logEvent(String.format("[GUEST_ADDED] Ride: %s (ID: %d) | Guest: %d | Position: %d",
                ride.rideName, ride.rideId, guestId, position));
    }

    @Override
    public void onGuestRemoved(Ride ride, int guestId) {
        logEvent(String.format("[GUEST_REMOVED] Ride: %s (ID: %d) | Guest: %d",
                ride.rideName, ride.rideId, guestId));
    }

    @Override
    public void onGuestProcessed(Ride ride, int guestId) {
        logEvent(String.format("[GUEST_PROCESSED] Ride: %s (ID: %d) | Guest: %d (served from queue)",
                ride.rideName, ride.rideId, guestId));
    }

    @Override
    public void onOperationalStatusChanged(Ride ride, boolean isNowOperational) {
        String status = isNowOperational ? "OPERATIONAL" : "CLOSED";
        logEvent(String.format("[STATUS_CHANGED] Ride: %s (ID: %d) | New Status: %s",
                ride.rideName, ride.rideId, status));
    }

    /**
     * Writes an event to the audit log with a timestamp.
     */
    private void logEvent(String message) {
        try {
            String timestamp = LocalDateTime.now().format(formatter);
            String logEntry = String.format("%s | %s\n", timestamp, message);
            Files.writeString(Paths.get(AUDIT_LOG), logEntry, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("[QueueAuditObserver] Failed to write audit log: " + e.getMessage());
        }
    }

    /**
     * Retrieves and prints the entire audit log.
     */
    public static void printAuditLog() throws IOException {
        System.out.println("\n  ╔═ AUDIT LOG ═╗");
        if (Files.exists(Paths.get(AUDIT_LOG))) {
            Files.readAllLines(Paths.get(AUDIT_LOG)).forEach(line -> System.out.println("  " + line));
        } else {
            System.out.println("  No audit log found.");
        }
        System.out.println("  ╚═════════════╝\n");
    }

    /**
     * Clears the audit log.
     */
    public static void clearAuditLog() throws IOException {
        Files.deleteIfExists(Paths.get(AUDIT_LOG));
        Files.writeString(Paths.get(AUDIT_LOG), 
                "[AUDIT LOG CLEARED] " + LocalDateTime.now().format(formatter) + "\n");
    }
}
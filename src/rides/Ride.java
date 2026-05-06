package rides;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a theme park ride with queue management and observer notification.
 * Tracks queue position, wait times, operational status, and notifies observers
 * of any queue or operational changes.
 *
 * @author ryanrockey
 */
public class Ride {

    public int rideId;
    public String rideName;
    public int maxCapacity;
    public boolean isOperational;
    public int[] guestQueue;
    public int queueSize;

    // ── Observer Pattern Components ───────────────────────────────────
    private List<QueueObserver> observers = new ArrayList<>();

    /**
     * Constructor for creating a new ride.
     */
    public Ride(int rideId, String rideName, int maxCapacity, boolean isOperational) {
        this.rideId = rideId;
        this.rideName = rideName;
        this.maxCapacity = maxCapacity;
        this.isOperational = isOperational;
        this.guestQueue = new int[100];
        this.queueSize = 0;
    }

    // ================================================================== //
    //  O B S E R V E R   M A N A G E M E N T
    // ================================================================== //

    /**
     * Registers an observer to be notified of queue changes.
     */
    public void addObserver(QueueObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer from notifications.
     */
    public void removeObserver(QueueObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers that a guest was added to the queue.
     */
    private void notifyGuestAdded(int guestId, int position) {
        for (QueueObserver observer : observers) {
            observer.onGuestAdded(this, guestId, position);
        }
    }

    /**
     * Notifies all observers that a guest was removed from the queue.
     */
    private void notifyGuestRemoved(int guestId) {
        for (QueueObserver observer : observers) {
            observer.onGuestRemoved(this, guestId);
        }
    }

    /**
     * Notifies all observers that a guest was processed (front of queue).
     */
    private void notifyGuestProcessed(int guestId) {
        for (QueueObserver observer : observers) {
            observer.onGuestProcessed(this, guestId);
        }
    }

    /**
     * Notifies all observers that the operational status changed.
     */
    private void notifyOperationalStatusChanged(boolean newStatus) {
        for (QueueObserver observer : observers) {
            observer.onOperationalStatusChanged(this, newStatus);
        }
    }

    // ================================================================== //
    //  Q U E U E   O P E R A T I O N S
    // ================================================================== //

    /**
     * Adds a guest to the ride queue and notifies observers.
     * Returns the position in queue (1-indexed), or -1 if queue is full.
     */
    public int addGuestToQueue(int guestId) {
        if (queueSize >= guestQueue.length) {
            // Resize array if needed
            int[] newQueue = new int[guestQueue.length * 2];
            System.arraycopy(guestQueue, 0, newQueue, 0, queueSize);
            guestQueue = newQueue;
        }
        guestQueue[queueSize] = guestId;
        queueSize++;
        int position = queueSize;

        // ── Notify observers ──────────────────────────────────────────
        notifyGuestAdded(guestId, position);

        return position;
    }

    /**
     * Checks if a guest is already in the queue.
     */
    public boolean isGuestInQueue(int guestId) {
        for (int i = 0; i < queueSize; i++) {
            if (guestQueue[i] == guestId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the next guest from the queue (FIFO) and notifies observers.
     * Returns the guest ID, or -1 if queue is empty.
     */
    public int removeNextGuest() {
        if (queueSize == 0) return -1;

        int nextGuest = guestQueue[0];
        System.arraycopy(guestQueue, 1, guestQueue, 0, queueSize - 1);
        queueSize--;

        // ── Notify observers ──────────────────────────────────────────
        notifyGuestProcessed(nextGuest);

        return nextGuest;
    }

    /**
     * Removes a specific guest from the queue by guest ID and notifies observers.
     * Shifts remaining guests forward to maintain order.
     */
    public void removeGuestFromQueue(int guestId) {
        int indexToRemove = -1;

        // Find the guest in the queue
        for (int i = 0; i < queueSize; i++) {
            if (guestQueue[i] == guestId) {
                indexToRemove = i;
                break;
            }
        }

        // If guest not found, do nothing
        if (indexToRemove == -1) {
            return;
        }

        // Shift remaining guests forward
        System.arraycopy(guestQueue, indexToRemove + 1, guestQueue, indexToRemove, queueSize - indexToRemove - 1);
        queueSize--;

        // ── Notify observers ──────────────────────────────────────────
        notifyGuestRemoved(guestId);
    }

    /**
     * Returns a list of all guest IDs currently in the queue in order.
     */
    public List<Integer> getQueuedGuestIds() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < queueSize; i++) {
            ids.add(guestQueue[i]);
        }
        return ids;
    }

    /**
     * Gets estimated wait time in minutes (simplified: ~2 min per guest).
     */
    public int getEstimatedWaitMinutes() {
        return queueSize * 2;
    }

    // ================================================================== //
    //  O P E R A T I O N A L   S T A T U S
    // ================================================================== //

    /**
     * Sets the operational status and notifies observers if changed.
     */
    public void setOperational(boolean operational) {
        if (this.isOperational != operational) {
            this.isOperational = operational;
            notifyOperationalStatusChanged(operational);
        }
    }

    // ================================================================== //
    //  P E R S I S T E N C E
    // ================================================================== //

    /**
     * Serializes ride to CSV format for persistence.
     * Format: rideId,rideName,maxCapacity,isOperational,queueSize,guestIds...
     */
    public String toCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append(rideId).append(",")
          .append(rideName).append(",")
          .append(maxCapacity).append(",")
          .append(isOperational ? "1" : "0").append(",")
          .append(queueSize);

        for (int i = 0; i < queueSize; i++) {
            sb.append(",").append(guestQueue[i]);
        }
        return sb.toString();
    }

    /**
     * Deserializes ride from CSV line.
     */
    public static Ride fromCsv(String line) throws NumberFormatException {
        String[] parts = line.split(",");
        if (parts.length < 5) throw new NumberFormatException("Invalid ride CSV format");

        int rideId = Integer.parseInt(parts[0]);
        String rideName = parts[1];
        int maxCapacity = Integer.parseInt(parts[2]);
        boolean isOperational = Integer.parseInt(parts[3]) == 1;

        Ride ride = new Ride(rideId, rideName, maxCapacity, isOperational);
        ride.queueSize = Integer.parseInt(parts[4]);

        // Load queue guest IDs
        for (int i = 5; i < parts.length && i - 5 < ride.queueSize; i++) {
            ride.guestQueue[i - 5] = Integer.parseInt(parts[i]);
        }

        return ride;
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %d) - Queue: %d | Wait: ~%d min",
                rideName, rideId, queueSize, getEstimatedWaitMinutes());
    }
}

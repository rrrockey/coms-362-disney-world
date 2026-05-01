package rides;

/**
 * Represents a theme park ride with queue management.
 * Tracks queue position, wait times, and operational status.
 *
 * @author ryanrockey
 */
public class Ride {

    public int rideId;
    public String rideName;
    public int maxCapacity;          // maximum concurrent riders
    public boolean isOperational;    // true if ride is running
    public int[] guestQueue;         // array of guest IDs in queue order
    public int queueSize;            // current number of guests in queue

    /**
     * Constructor for creating a new ride.
     */
    public Ride(int rideId, String rideName, int maxCapacity, boolean isOperational) {
        this.rideId = rideId;
        this.rideName = rideName;
        this.maxCapacity = maxCapacity;
        this.isOperational = isOperational;
        this.guestQueue = new int[100];  // initial capacity
        this.queueSize = 0;
    }

    /**
     * Adds a guest to the ride queue.
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
        return queueSize;  // return position (1-indexed)
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
     * Removes the next guest from the queue (FIFO).
     * Returns the guest ID, or -1 if queue is empty.
     */
    public int removeNextGuest() {
        if (queueSize == 0) return -1;
        
        int nextGuest = guestQueue[0];
        System.arraycopy(guestQueue, 1, guestQueue, 0, queueSize - 1);
        queueSize--;
        return nextGuest;
    }

    /**
     * Gets estimated wait time in minutes (simplified: ~2 min per guest).
     */
    public int getEstimatedWaitMinutes() {
        return queueSize * 2;
    }

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
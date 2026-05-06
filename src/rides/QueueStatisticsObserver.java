package rides;

import java.util.HashMap;
import java.util.Map;

/**
 * Observer that tracks queue statistics and metrics.
 * Demonstrates the Observer pattern by maintaining statistics about queue activity
 * without coupling to the Ride or ManageRideQueues classes.
 *
 * @author ryanrockey
 */
public class QueueStatisticsObserver implements QueueObserver {

    private Map<Integer, Integer> guestAddCount = new HashMap<>();      // ride ID -> times added
    private Map<Integer, Integer> guestRemoveCount = new HashMap<>();   // ride ID -> times removed
    private Map<Integer, Integer> maxQueueSeen = new HashMap<>();       // ride ID -> max queue size
    private Map<Integer, Long> totalWaitTime = new HashMap<>();         // ride ID -> cumulative wait time

    @Override
    public void onGuestAdded(Ride ride, int guestId, int position) {
        guestAddCount.merge(ride.rideId, 1, Integer::sum);
        maxQueueSeen.merge(ride.rideId, position, Integer::max);
    }

    @Override
    public void onGuestRemoved(Ride ride, int guestId) {
        guestRemoveCount.merge(ride.rideId, 1, Integer::sum);
    }

    @Override
    public void onGuestProcessed(Ride ride, int guestId) {
        guestRemoveCount.merge(ride.rideId, 1, Integer::sum);
        // In a real system, you might track time from add to process
    }

    @Override
    public void onOperationalStatusChanged(Ride ride, boolean isNowOperational) {
        // Could log when rides go down/up
        System.out.println("[Statistics] Ride " + ride.rideName + " is now "
                + (isNowOperational ? "OPERATIONAL" : "CLOSED"));
    }

    /**
     * Returns the total number of guests added to a ride.
     */
    public int getTotalGuestsAdded(int rideId) {
        return guestAddCount.getOrDefault(rideId, 0);
    }

    /**
     * Returns the total number of guests removed from a ride.
     */
    public int getTotalGuestsRemoved(int rideId) {
        return guestRemoveCount.getOrDefault(rideId, 0);
    }

    /**
     * Returns the maximum queue size ever seen for a ride.
     */
    public int getMaxQueueSeen(int rideId) {
        return maxQueueSeen.getOrDefault(rideId, 0);
    }

    /**
     * Prints statistics for a specific ride.
     */
    public void printRideStatistics(Ride ride) {
        System.out.println("\n  ┌─ Queue Statistics: " + ride.rideName + " ─────┐");
        System.out.println("  │ Total added:   " + getTotalGuestsAdded(ride.rideId));
        System.out.println("  │ Total removed: " + getTotalGuestsRemoved(ride.rideId));
        System.out.println("  │ Peak queue:    " + getMaxQueueSeen(ride.rideId));
        System.out.println("  └────────────────────────────────┘");
    }

    /**
     * Prints statistics for all tracked rides.
     */
    public void printAllStatistics() {
        System.out.println("\n  ╔═ OVERALL QUEUE STATISTICS ═╗");
        if (guestAddCount.isEmpty()) {
            System.out.println("  ║ No activity recorded yet.    ║");
        } else {
            guestAddCount.forEach((rideId, count) -> {
                int removed = guestRemoveCount.getOrDefault(rideId, 0);
                int maxSeen = maxQueueSeen.getOrDefault(rideId, 0);
                System.out.printf("  ║ Ride %d: +%d | -%d | Peak: %d%n",
                        rideId, count, removed, maxSeen);
            });
        }
        System.out.println("  ╚════════════════════════════╝");
    }

    /**
     * Clears all statistics.
     */
    public void reset() {
        guestAddCount.clear();
        guestRemoveCount.clear();
        maxQueueSeen.clear();
        totalWaitTime.clear();
    }
}
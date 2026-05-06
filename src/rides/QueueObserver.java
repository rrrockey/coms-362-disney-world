package rides;

/**
 * Observer interface for queue changes.
 * Implements the Observer pattern to allow multiple listeners to react to queue events
 * without tight coupling to the Ride class.
 *
 * @author ryanrockey
 */
public interface QueueObserver {

    /**
     * Called when a guest is added to the queue.
     * 
     * @param ride         the ride being observed
     * @param guestId      the ID of the guest added
     * @param position     the position in queue (1-indexed)
     */
    void onGuestAdded(Ride ride, int guestId, int position);

    /**
     * Called when a guest is removed from the queue.
     * 
     * @param ride    the ride being observed
     * @param guestId the ID of the guest removed
     */
    void onGuestRemoved(Ride ride, int guestId);

    /**
     * Called when a guest is processed (removed from front of queue).
     * 
     * @param ride    the ride being observed
     * @param guestId the ID of the guest processed
     */
    void onGuestProcessed(Ride ride, int guestId);

    /**
     * Called when the ride operational status changes.
     * 
     * @param ride           the ride being observed
     * @param isNowOperational the new operational status
     */
    void onOperationalStatusChanged(Ride ride, boolean isNowOperational);
}

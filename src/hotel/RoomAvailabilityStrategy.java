package hotel;

import java.util.List;

/**
 * Strategy Pattern — defines the contract for determining whether
 * a given room is available for booking.
 *
 * Concrete strategies are swapped in at runtime by ManageHotels,
 * letting the availability algorithm vary independently of the
 * booking and repository logic.
 */
public interface RoomAvailabilityStrategy {

    /**
     * Returns true if {@code room} is bookable under this strategy's rules.
     *
     * @param room     the room to evaluate
     * @param bookings the full current list of bookings (all statuses)
     */
    boolean isAvailable(Room room, List<Booking> bookings);

    /** Label */
    String description();
}
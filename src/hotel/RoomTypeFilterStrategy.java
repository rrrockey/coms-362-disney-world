package hotel;

import java.util.List;

/**
 *
 * Combines a type requirement (Standard / Double / Suite) with the
 * strict no-active-booking rule.  A room passes only when:
 *   1. Its type matches the requested type (case-insensitive), AND
 *   2. It has no BOOKED or CHECKED_IN booking.
 *
 * This lets the front-desk agent quickly show only the room category
 * a guest has asked for, without manual filtering after the fact.
 */
public class RoomTypeFilterStrategy implements RoomAvailabilityStrategy {

    private final String requestedType;
    private final StrictAvailabilityStrategy strictCheck =
            new StrictAvailabilityStrategy();

    /**
     * @param requestedType  "Standard", "Double", or "Suite"
     *                       (compared case-insensitively)
     */
    public RoomTypeFilterStrategy(String requestedType) {
        this.requestedType = requestedType.trim();
    }

    @Override
    public boolean isAvailable(Room room, List<Booking> bookings) {
        return room.type.equalsIgnoreCase(requestedType)
            && strictCheck.isAvailable(room, bookings);
    }

    @Override
    public String description() {
        return "Available " + requestedType + " rooms only";
    }
}
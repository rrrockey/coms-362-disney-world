package hotel;

import java.time.LocalDate;
import java.util.List;

/**
 * A room is available when no existing BOOKED or CHECKED_IN booking
 * overlaps the requested [desiredCheckIn, desiredCheckOut) window.
 *
 * Overlap condition (two intervals [a,b) and [c,d) overlap when a < d && c < b):
 *   existing.checkIn  < desired.checkOut
 *   desired.checkIn   < existing.checkOut
 *
 * Rooms in NEEDS_CLEANING or AVAILABLE status do not block a future
 * reservation, so only BOOKED/CHECKED_IN bookings are considered.
 */
public class DateRangeAvailabilityStrategy implements RoomAvailabilityStrategy {

    private final LocalDate desiredCheckIn;
    private final LocalDate desiredCheckOut;

    public DateRangeAvailabilityStrategy(LocalDate desiredCheckIn,
                                         LocalDate desiredCheckOut) {
        if (!desiredCheckIn.isBefore(desiredCheckOut)) {
            throw new IllegalArgumentException(
                "Check-in date must be before check-out date.");
        }
        this.desiredCheckIn  = desiredCheckIn;
        this.desiredCheckOut = desiredCheckOut;
    }

    @Override
    public boolean isAvailable(Room room, List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.roomId == room.roomId)
                .filter(b -> b.status == RoomStatus.BOOKED
                          || b.status == RoomStatus.CHECKED_IN)
                .noneMatch(b -> overlaps(b));
    }

    /** True when the existing booking's dates conflict with the desired window. */
    private boolean overlaps(Booking existing) {
        try {
            LocalDate existIn  = LocalDate.parse(existing.checkInDate);
            LocalDate existOut = LocalDate.parse(existing.checkOutDate);
            // Overlap: existIn < desiredOut  AND  desiredIn < existOut
            return existIn.isBefore(desiredCheckOut)
                && desiredCheckIn.isBefore(existOut);
        } catch (Exception e) {
            // If dates are malformed, conservatively treat as occupied
            return true;
        }
    }

    @Override
    public String description() {
        return "Available for " + desiredCheckIn + " → " + desiredCheckOut;
    }
}
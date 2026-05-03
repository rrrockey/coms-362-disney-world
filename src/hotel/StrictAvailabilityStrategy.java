package hotel;

import java.util.List;

/**
 *
 * A room is considered available only when it has NO booking
 * currently in a BOOKED or CHECKED_IN state.  
 */
public class StrictAvailabilityStrategy implements RoomAvailabilityStrategy {

    @Override
    public boolean isAvailable(Room room, List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.roomId == room.roomId)
                .noneMatch(b -> b.status == RoomStatus.BOOKED
                             || b.status == RoomStatus.CHECKED_IN);
    }

    @Override
    public String description() {
        return "Any available room (no active booking)";
    }
}
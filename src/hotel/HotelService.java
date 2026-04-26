package hotel;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * Business logic layer for hotel operations.
 *
 * Separates decision-making from raw persistence (HotelRepository).
 */
public class HotelService {

    // ------------------------------------------------------------------ //
    //  Check-in with automatic room relocation
    // ------------------------------------------------------------------ //

    /**
     * Attempts to check a guest into their booked room.
     */
    public static CheckInResult checkIn(int roomId, int guestId) throws IOException {

        Booking booking = HotelRepository.findBookingByRoomAndStatus(roomId, RoomStatus.BOOKED);
        if (booking == null || booking.guestId != guestId) {
            return CheckInResult.failure(roomId, "No active booking found for this guest in room " + roomId + ".");
        }

        Room originalRoom = HotelRepository.findRoomById(roomId);
        if (originalRoom == null) {
            return CheckInResult.failure(roomId, "Room " + roomId + " does not exist.");
        }

        boolean roomReady = isRoomReady(roomId);

        if (roomReady) {
            // Happy path — check in to the original room
            booking.status = RoomStatus.CHECKED_IN;
            HotelRepository.saveBooking(booking);
            return CheckInResult.success(originalRoom, originalRoom, booking, false);
        }

        Room alternative = findAlternativeRoom(originalRoom, booking);

        if (alternative == null) {
            return CheckInResult.failure(roomId,
                "Room " + originalRoom.roomNumber + " is not ready and no alternative rooms are available.");
        }

        booking = relocateBooking(booking, alternative);
        return CheckInResult.success(originalRoom, alternative, booking, true);
    }

    // ------------------------------------------------------------------ //
    //  Helpers
    // ------------------------------------------------------------------ //

    /**
     * A room is "ready" if it has no NEEDS_CLEANING booking.
     * (AVAILABLE rooms with no booking record are inherently ready.)
     */
    private static boolean isRoomReady(int roomId) throws IOException {
        Booking dirty = HotelRepository.findBookingByRoomAndStatus(roomId, RoomStatus.NEEDS_CLEANING);
        return dirty == null;
    }

    /**
     * Finds the best available replacement room.
     */
    private static Room findAlternativeRoom(Room original, Booking existingBooking) throws IOException {
        List<Room> available = HotelRepository.findAvailableRooms();

        // Exclude the original room itself
        available = available.stream()
                .filter(r -> r.roomId != original.roomId)
                .toList();

        // Prefer same type
        Room sameType = available.stream()
                .filter(r -> r.type.equalsIgnoreCase(original.type))
                .findFirst()
                .orElse(null);

        if (sameType != null) return sameType;

        // Fallback: any available room, sorted by nightly rate ascending
        return available.stream()
                .min(Comparator.comparingDouble(r -> r.nightlyRate))
                .orElse(null);
    }

    
    private static Booking relocateBooking(Booking booking, Room newRoom) throws IOException {
        Booking relocated = new Booking(
                booking.bookingId,
                newRoom.roomId,
                booking.guestId,
                booking.guestName,
                booking.checkInDate,
                booking.checkOutDate,
                RoomStatus.CHECKED_IN);
        HotelRepository.saveBooking(relocated);
        return relocated;
    }

    // ------------------------------------------------------------------ //
    //  Result type
    // ------------------------------------------------------------------ //

    /**
     * Describes the outcome of a check-in attempt.
     */
    public static class CheckInResult {

        public final boolean   success;
        public final boolean   wasRelocated;
        public final Room      originalRoom;
        public final Room      assignedRoom;   // same as originalRoom if not relocated
        public final Booking   booking;        // null on failure
        public final String    message;

        private CheckInResult(boolean success, boolean wasRelocated,
                              Room originalRoom, Room assignedRoom,
                              Booking booking, String message) {
            this.success      = success;
            this.wasRelocated = wasRelocated;
            this.originalRoom = originalRoom;
            this.assignedRoom = assignedRoom;
            this.booking      = booking;
            this.message      = message;
        }

        static CheckInResult success(Room original, Room assigned, Booking booking, boolean relocated) {
            String msg = relocated
                ? String.format("Room %d was not ready. Guest relocated from room %d (%s) to room %d (%s).",
                    original.roomNumber, original.roomNumber, original.type,
                    assigned.roomNumber, assigned.type)
                : String.format("Guest successfully checked in to room %d (%s).",
                    assigned.roomNumber, assigned.type);
            return new CheckInResult(true, relocated, original, assigned, booking, msg);
        }

        static CheckInResult failure(int roomId, String reason) {
            return new CheckInResult(false, false, null, null, null, reason);
        }

        @Override
        public String toString() {
            return String.format("CheckInResult{success=%b, relocated=%b, message='%s'}",
                    success, wasRelocated, message);
        }
    }
}
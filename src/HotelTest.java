import hotel.*;
import java.io.IOException;
import java.util.List;

public class HotelTest {
    public static void main(String[] args) {
        try {
            HotelRepository.initialise();

            System.out.println("--- Testing Booking Creation ---");
            // Rooms are seeded in repository.initialise()
            // Room 1 (Standard) has ID 1
            Booking booking = new Booking(500, 1, 1, "Charlie", "2026-05-06", "2026-05-10", RoomStatus.BOOKED);
            HotelRepository.saveBooking(booking);
            System.out.println("Booking created for " + booking.guestName + " in room " + booking.roomId);

            System.out.println("\n--- Testing HotelService.checkIn (Success Path) ---");
            HotelService.CheckInResult result = HotelService.checkIn(1, 1);
            System.out.println(result);
            if (result.success && !result.wasRelocated) {
                System.out.println("SUCCESS: Checked in to original room.");
            } else {
                System.out.println("FAILURE: Check-in failed or unexpected relocation.");
            }

            System.out.println("\n--- Testing HotelService.checkIn (Relocation Path) ---");
            // Mark room 1 as dirty for a new guest
            Booking dirtyBooking = new Booking(501, 1, 2, "Bob", "2026-05-10", "2026-05-15", RoomStatus.NEEDS_CLEANING);
            HotelRepository.saveBooking(dirtyBooking);
            
            // Create a booking for Daisy in the now-dirty room 1
            Booking daisyBooking = new Booking(502, 1, 3, "Daisy", "2026-05-06", "2026-05-10", RoomStatus.BOOKED);
            HotelRepository.saveBooking(daisyBooking);

            // Room 2 should be available (seeded with Standard type)
            HotelService.CheckInResult result2 = HotelService.checkIn(1, 3);
            System.out.println(result2);
            if (result2.success && result2.wasRelocated && result2.assignedRoom.roomId != 1) {
                System.out.println("SUCCESS: Relocated guest to available room " + result2.assignedRoom.roomId);
            } else {
                System.out.println("FAILURE: Relocation failed.");
            }

            System.out.println("\nTest complete.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

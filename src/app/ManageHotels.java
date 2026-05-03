package app;

import hotel.*;
import employee.*;
import java.util.List;
import java.util.Scanner;

/**
 * CLI interface for hotel management.
 *
 * Main menu
 *  [1] Book a room        (customer books via staff)
 *  [2] Check in guest     (guest arrives, has booking)
 *  [3] Check out guest    (guest leaves, room → NEEDS_CLEANING)
 *  [4] Mark room cleaned  (housekeeper done, room → AVAILABLE)
 *  [5] View all rooms
 *  [0] Back
 */
public class ManageHotels {

    private static final Scanner sc = new Scanner(System.in);

    // Hardcoded on-duty employee for this session — extend later if needed
    private static final HotelEmployee FRONT_DESK  =
            new HotelEmployee(1, "Staff", "Front Desk");
    private static final HotelEmployee HOUSEKEEPER =
            new HotelEmployee(2, "Staff", "Housekeeper");

    public static void manageHotels() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> bookRoom();
                case "2" -> checkIn();
                case "3" -> checkOut();
                case "4" -> markCleaned();
                case "5" -> viewAllRooms();
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option, try again.");
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  [1] Book a room
    // ------------------------------------------------------------------ //

    private static void bookRoom() {
        System.out.println();
        printDivider("BOOK A ROOM");
        try {
            List<Room> available = HotelRepository.findAvailableRooms();
            if (available.isEmpty()) {
                System.out.println("  No rooms are currently available.");
                pause(); return;
            }

            printRoomTable(available, "AVAILABLE ROOMS");

            System.out.print("  Enter room number to book: ");
            int roomNumber = parseIntOrNeg(sc.nextLine().trim());
            Room room = available.stream()
                    .filter(r -> r.roomNumber == roomNumber)
                    .findFirst().orElse(null);
            if (room == null) {
                System.out.println("  Room not found or unavailable.");
                pause(); return;
            }

            System.out.print("  Guest name: ");
            String guestName = sc.nextLine().trim();

            System.out.print("  Guest ID (enter 0 if walk-in): ");
            int guestId = parseIntOrNeg(sc.nextLine().trim());
            if (guestId < 0) guestId = 0;

            System.out.print("  Check-in date  (YYYY-MM-DD): ");
            String checkIn = sc.nextLine().trim();

            System.out.print("  Check-out date (YYYY-MM-DD): ");
            String checkOut = sc.nextLine().trim();

            Booking booking = new Booking(
                    HotelRepository.nextBookingId(),
                    room.roomId,
                    guestId,
                    guestName,
                    checkIn,
                    checkOut,
                    RoomStatus.BOOKED);
            HotelRepository.saveBooking(booking);

            printDivider("");
            System.out.println("  ✓ Room booked successfully!\n");
            System.out.printf("  %-14s %d%n",   "Room:",       room.roomNumber);
            System.out.printf("  %-14s %s%n",   "Type:",       room.type);
            System.out.printf("  %-14s $%.2f/night%n", "Rate:", room.nightlyRate);
            System.out.printf("  %-14s %s%n",   "Guest:",      guestName);
            System.out.printf("  %-14s %s → %s%n", "Dates:",   checkIn, checkOut);
            System.out.printf("  %-14s %s%n",   "Booked by:",  FRONT_DESK);
            printDivider("");

        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }

    // ------------------------------------------------------------------ //
    //  [2] Check in
    // ------------------------------------------------------------------ //

    private static void checkIn() {
        System.out.println();
        printDivider("GUEST CHECK-IN");
        try {
            List<Booking> booked = HotelRepository.findBookingsByStatus(RoomStatus.BOOKED);
            if (booked.isEmpty()) {
                System.out.println("  No guests with pending bookings.");
                pause(); return;
            }

            printBookingTable(booked, "PENDING BOOKINGS");

            System.out.print("  Enter booking ID to check in: ");
            int bookingId = parseIntOrNeg(sc.nextLine().trim());
            Booking booking = booked.stream()
                    .filter(b -> b.bookingId == bookingId)
                    .findFirst().orElse(null);
            if (booking == null) {
                System.out.println("  Booking not found.");
                pause(); return;
            }

            Room room = HotelRepository.findRoomById(booking.roomId);
            booking.status = RoomStatus.CHECKED_IN;
            HotelRepository.saveBooking(booking);

            printDivider("");
            System.out.println("  ✓ Guest checked in!\n");
            System.out.printf("  %-14s %s%n", "Guest:",    booking.guestName);
            System.out.printf("  %-14s %d%n", "Room:",     room != null ? room.roomNumber : booking.roomId);
            System.out.printf("  %-14s %s%n", "Check-out:", booking.checkOutDate);
            System.out.printf("  %-14s %s%n", "Processed:", FRONT_DESK);
            printDivider("");

        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }

    // ------------------------------------------------------------------ //
    //  [3] Check out
    // ------------------------------------------------------------------ //

    private static void checkOut() {
        System.out.println();
        printDivider("GUEST CHECK-OUT");
        try {
            List<Booking> checkedIn = HotelRepository.findBookingsByStatus(RoomStatus.CHECKED_IN);
            if (checkedIn.isEmpty()) {
                System.out.println("  No guests currently checked in.");
                pause(); return;
            }

            printBookingTable(checkedIn, "CHECKED-IN GUESTS");

            System.out.print("  Enter booking ID to check out: ");
            int bookingId = parseIntOrNeg(sc.nextLine().trim());
            Booking booking = checkedIn.stream()
                    .filter(b -> b.bookingId == bookingId)
                    .findFirst().orElse(null);
            if (booking == null) {
                System.out.println("  Booking not found.");
                pause(); return;
            }

            Room room = HotelRepository.findRoomById(booking.roomId);
            booking.status = RoomStatus.NEEDS_CLEANING;
            HotelRepository.saveBooking(booking);

            printDivider("");
            System.out.println("  ✓ Guest checked out — room flagged for cleaning.\n");
            System.out.printf("  %-14s %s%n", "Guest:",     booking.guestName);
            System.out.printf("  %-14s %d%n", "Room:",      room != null ? room.roomNumber : booking.roomId);
            System.out.printf("  %-14s %s%n", "Status:",    RoomStatus.NEEDS_CLEANING);
            System.out.printf("  %-14s %s%n", "Processed:", FRONT_DESK);
            printDivider("");

        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }

    // ------------------------------------------------------------------ //
    //  [4] Mark room cleaned
    // ------------------------------------------------------------------ //

    private static void markCleaned() {
        System.out.println();
        printDivider("MARK ROOM AS CLEANED");
        try {
            List<Booking> dirty = HotelRepository.findBookingsByStatus(RoomStatus.NEEDS_CLEANING);
            if (dirty.isEmpty()) {
                System.out.println("  No rooms currently need cleaning.");
                pause(); return;
            }

            printBookingTable(dirty, "ROOMS NEEDING CLEANING");

            System.out.print("  Enter booking ID to mark cleaned: ");
            int bookingId = parseIntOrNeg(sc.nextLine().trim());
            Booking booking = dirty.stream()
                    .filter(b -> b.bookingId == bookingId)
                    .findFirst().orElse(null);
            if (booking == null) {
                System.out.println("  Booking not found.");
                pause(); return;
            }

            Room room = HotelRepository.findRoomById(booking.roomId);
            booking.status = RoomStatus.AVAILABLE;
            HotelRepository.saveBooking(booking);

            printDivider("");
            System.out.println("  ✓ Room marked as available.\n");
            System.out.printf("  %-14s %d%n", "Room:",        room != null ? room.roomNumber : booking.roomId);
            System.out.printf("  %-14s %s%n", "Status:",      RoomStatus.AVAILABLE);
            System.out.printf("  %-14s %s%n", "Confirmed by:", HOUSEKEEPER);
            printDivider("");

        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }

    // ------------------------------------------------------------------ //
    //  [5] View all rooms
    // ------------------------------------------------------------------ //

    private static void viewAllRooms() {
        System.out.println();
        printDivider("ALL ROOMS");
        try {
            List<Room>    rooms    = HotelRepository.loadAllRooms();
            List<Booking> bookings = HotelRepository.loadAllBookings();

            System.out.printf("  %-6s  %-10s  %-10s  %10s  %-16s  %s%n",
                    "Room", "Type", "Rate/Night", "Status", "Guest", "Dates");
            System.out.println("  " + "─".repeat(72));

            for (Room r : rooms) {
                // Find the most recent active booking for this room
                Booking active = bookings.stream()
                        .filter(b -> b.roomId == r.roomId &&
                                     b.status != RoomStatus.AVAILABLE)
                        .reduce((a, b) -> b)   // last entry wins
                        .orElse(null);

                String status = active != null ? active.status.name() : "AVAILABLE";
                String guest  = active != null ? active.guestName     : "—";
                String dates  = active != null
                        ? active.checkInDate + " → " + active.checkOutDate
                        : "—";

                System.out.printf("  %-6d  %-10s  $%-9.2f  %-16s  %-16s  %s%n",
                        r.roomNumber, r.type, r.nightlyRate, status, guest, dates);
            }
            printDivider("");

        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }

    // ------------------------------------------------------------------ //
    //  Formatting helpers
    // ------------------------------------------------------------------ //

    private static void printRoomTable(List<Room> rooms, String title) {
        printDivider(title);
        System.out.printf("  %-6s  %-10s  %s%n", "Room", "Type", "Rate/Night");
        System.out.println("  " + "─".repeat(32));
        for (Room r : rooms)
            System.out.printf("  %-6d  %-10s  $%.2f%n", r.roomNumber, r.type, r.nightlyRate);
        System.out.println();
    }

    private static void printBookingTable(List<Booking> bookings, String title) {
        printDivider(title);
        System.out.printf("  %-4s  %-16s  %-6s  %-14s  %s%n",
                "ID", "Guest", "Room", "Status", "Dates");
        System.out.println("  " + "─".repeat(62));
        for (Booking b : bookings) {
            try {
                Room r = HotelRepository.findRoomById(b.roomId);
                System.out.printf("  %-4d  %-16s  %-6d  %-14s  %s → %s%n",
                        b.bookingId,
                        truncate(b.guestName, 16),
                        r != null ? r.roomNumber : b.roomId,
                        b.status,
                        b.checkInDate,
                        b.checkOutDate);
            } catch (Exception e) {
                System.out.printf("  %-4d  %-16s  %-6d  %-14s%n",
                        b.bookingId, b.guestName, b.roomId, b.status);
            }
        }
        System.out.println();
    }

    private static void printMainMenu() {
        System.out.println();
        printDivider("HOTEL MANAGEMENT");
        System.out.println("  [1] Book a room");
        System.out.println("  [2] Check in guest");
        System.out.println("  [3] Check out guest");
        System.out.println("  [4] Mark room cleaned");
        System.out.println("  [5] View all rooms");
        System.out.println("  [0] Back");
        printDivider("");
        System.out.print("  Choice: ");
    }

    private static void printDivider(String label) {
        if (label.isEmpty()) {
            System.out.println("  " + "─".repeat(64));
        } else {
            String padded = "─── " + label + " ";
            int remaining = Math.max(0, 64 - padded.length());
            System.out.println("  " + padded + "─".repeat(remaining));
        }
    }

    private static void pause() {
        System.out.print("\n  Press ENTER to continue...");
        sc.nextLine();
    }

    private static int parseIntOrNeg(String s) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return -1; }
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
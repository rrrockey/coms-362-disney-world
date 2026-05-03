package hotel;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Persists rooms and bookings to:
 *   data/hotel.csv    — static room layout (seeded on initialise)
 *   data/bookings.csv — live booking records
 */
public class HotelRepository {

    private static final String DATA_DIR      = "data";
    private static final String HOTEL_FILE    = DATA_DIR + "/hotel.csv";
    private static final String BOOKINGS_FILE = DATA_DIR + "/bookings.csv";

    // ------------------------------------------------------------------ //
    //  Initialisation — seeds room layout if hotel.csv doesn't exist
    // ------------------------------------------------------------------ //

    public static void initialise() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));

        Path hotelPath = Paths.get(HOTEL_FILE);
        if (!Files.exists(hotelPath)) {
            StringBuilder sb = new StringBuilder("roomId,roomNumber,type,nightlyRate\n");
            for (int i = 0; i < 5; i++)
                sb.append(String.join(",", str(i + 1), str(101 + i), "Standard", "250.0")).append("\n");
            for (int i = 0; i < 3; i++)
                sb.append(String.join(",", str(6 + i), str(106 + i), "Double", "300.0")).append("\n");
            for (int i = 0; i < 2; i++)
                sb.append(String.join(",", str(9 + i), str(109 + i), "Suite", "500.0")).append("\n");
            Files.writeString(hotelPath, sb.toString());
        }

        Path bookingsPath = Paths.get(BOOKINGS_FILE);
        if (!Files.exists(bookingsPath)) {
            Files.writeString(bookingsPath, "bookingId,roomId,guestId,guestName,checkInDate,checkOutDate,status\n");
        }
    }

    // ================================================================== //
    //  R O O M S
    // ================================================================== //

    public static List<Room> loadAllRooms() throws IOException {
        List<Room> rooms = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(HOTEL_FILE))) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("roomId")) continue;
            try { rooms.add(Room.fromCsv(line)); }
            catch (Exception e) { System.err.println("[HotelRepository] Skipping room: " + line); }
        }
        return rooms;
    }

    public static Room findRoomById(int roomId) throws IOException {
        return loadAllRooms().stream()
                .filter(r -> r.roomId == roomId)
                .findFirst().orElse(null);
    }

    // ================================================================== //
    //  B O O K I N G S
    // ================================================================== //

    public static List<Booking> loadAllBookings() throws IOException {
        List<Booking> bookings = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(BOOKINGS_FILE))) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("bookingId")) continue;
            try { bookings.add(Booking.fromCsv(line)); }
            catch (Exception e) { System.err.println("[HotelRepository] Skipping booking: " + line); }
        }
        return bookings;
    }

    public static void saveBooking(Booking booking) throws IOException {
        Map<Integer, Booking> all = loadAllBookingsMap();
        all.put(booking.bookingId, booking);
        writeBookingsMap(all);
    }

    public static Booking findBookingByRoomAndStatus(int roomId, RoomStatus status) throws IOException {
        return loadAllBookings().stream()
                .filter(b -> b.roomId == roomId && b.status == status)
                .findFirst().orElse(null);
    }

    public static List<Booking> findBookingsByStatus(RoomStatus status) throws IOException {
        return loadAllBookings().stream()
                .filter(b -> b.status == status)
                .toList();
    }

    public static int nextBookingId() throws IOException {
        return loadAllBookings().stream()
                .mapToInt(b -> b.bookingId)
                .max().orElse(0) + 1;
    }

    // ================================================================== //
    //  Room availability — Strategy Pattern
    // ================================================================== //

    /**
     * Returns rooms that pass the supplied {@link RoomAvailabilityStrategy}.
     *
     * Both the room list and the full booking list are loaded once and
     * passed to the strategy for every room, keeping I/O minimal.
     *
     * @param strategy  the algorithm that decides whether a room qualifies
     * @return          rooms for which strategy.isAvailable() returns true
     */
    public static List<Room> findAvailableRooms(RoomAvailabilityStrategy strategy)
            throws IOException {
        List<Room>    rooms    = loadAllRooms();
        List<Booking> bookings = loadAllBookings();
        return rooms.stream()
                .filter(r -> strategy.isAvailable(r, bookings))
                .toList();
    }

    /**
     * Backward-compatible overload — uses {@link StrictAvailabilityStrategy}
     * so any existing callers continue to work without changes.
     */
    public static List<Room> findAvailableRooms() throws IOException {
        return findAvailableRooms(new StrictAvailabilityStrategy());
    }

    // ------------------------------------------------------------------ //
    //  Internal
    // ------------------------------------------------------------------ //

    private static Map<Integer, Booking> loadAllBookingsMap() throws IOException {
        Map<Integer, Booking> map = new LinkedHashMap<>();
        for (Booking b : loadAllBookings()) map.put(b.bookingId, b);
        return map;
    }

    private static void writeBookingsMap(Map<Integer, Booking> map) throws IOException {
        StringBuilder sb = new StringBuilder("bookingId,roomId,guestId,guestName,checkInDate,checkOutDate,status\n");
        for (Booking b : map.values()) sb.append(b.toCsv()).append("\n");
        Files.writeString(Paths.get(BOOKINGS_FILE), sb.toString());
    }

    private static String str(int i) { return String.valueOf(i); }
}

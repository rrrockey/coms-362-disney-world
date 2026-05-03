package hotel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents one booking record.
 * Persisted in data/bookings.csv.
 *
 * Lifecycle: AVAILABLE → BOOKED → CHECKED_IN → CHECKED_OUT → NEEDS_CLEANING → AVAILABLE
 */
public class Booking {

	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    public final int    bookingId;
    public final int    roomId;
    public int          guestId;       // 0 = no guest (room is available)
    public String       guestName;
    public String       checkInDate;   // stored as plain string: YYYY-MM-DD
    public String       checkOutDate;
    public RoomStatus   status;

    public Booking(int bookingId, int roomId, int guestId, String guestName,
                   String checkInDate, String checkOutDate, RoomStatus status) {
    	
    	validateDates(checkInDate, checkOutDate);
        this.bookingId    = bookingId;
        this.roomId       = roomId;
        this.guestId      = guestId;
        this.guestName    = guestName;
        this.checkInDate  = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status       = status;
    }

    private static void validateDates(String checkInDate, String checkOutDate) {
    	LocalDate checkIn = parseDate(checkInDate, "checkInDate");
    	LocalDate checkOut = parseDate(checkOutDate, "checkOutDate");
    	
    	LocalDate today = LocalDate.now();
    	
    	if (checkIn.isBefore(today)) {
    		throw new IllegalArgumentException("checkInDate must be not be before today " + today);
    	}
    	
    	if (!checkOut.isAfter(checkIn)) {
    		throw new IllegalArgumentException("checkOutDate must be after checkInDate");
    	}
    }
    
    private static LocalDate parseDate(String value, String fieldName) {
    	if (value == null || value.isBlank()) {
    		throw new IllegalArgumentException(fieldName + " must not be null or blank.");
    	}
    	try {
    		return LocalDate.parse(value.trim(), DATE_FMT);
    	} catch (DateTimeParseException e) {
    		throw new IllegalArgumentException(fieldName + " must be in YYYY-MM-DD format.");
    	}
    }
    
    public String toCsv() {
        return String.join(",",
                String.valueOf(bookingId),
                String.valueOf(roomId),
                String.valueOf(guestId),
                guestName,
                checkInDate,
                checkOutDate,
                status.name());
    }

    public static Booking fromCsv(String line) {
        String[] p = line.split(",", 7);
        if (p.length < 7) throw new IllegalArgumentException("Malformed booking line: " + line);
        return new Booking(
                Integer.parseInt(p[0].trim()),
                Integer.parseInt(p[1].trim()),
                Integer.parseInt(p[2].trim()),
                p[3].trim(),
                p[4].trim(),
                p[5].trim(),
                RoomStatus.valueOf(p[6].trim()));
    }

    @Override
    public String toString() {
        return String.format("Booking{id=%d, roomId=%d, guest='%s', %s→%s, status=%s}",
                bookingId, roomId, guestName, checkInDate, checkOutDate, status);
    }
}

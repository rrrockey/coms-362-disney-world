package guest;

/**
 * Represents a single park guest.
 *
 * @author ryanrockey
 */
public class Guest {

    public String name;
    public int    guestId;    // unique ID, used as CSV key
    public int    age;
    public String ticketType; // e.g. "1-Day", "Annual Pass", etc.

    public Guest(int guestId, String name, int age, String ticketType) {
        this.guestId    = guestId;
        this.name       = name;
        this.age        = age;
        this.ticketType = ticketType;
    }

    /** Returns a CSV row: guestId,name,age,ticketType */
    public String toCsv() {
        return String.join(",", String.valueOf(guestId), escapeCsv(name), String.valueOf(age), escapeCsv(ticketType));
    }

    /** Rebuilds a Guest from a CSV row produced by toCsv(). */
    public static Guest fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", 4);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Malformed guest CSV line: " + csvLine);
        }
        return new Guest(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]), parts[3]);
    }

    /** Wraps a field in quotes if it contains a comma. */
    private static String escapeCsv(String value) {
        return value.contains(",") ? "\"" + value + "\"" : value;
    }

    @Override
    public String toString() {
        return String.format("Guest{id=%d, name='%s', age=%d, ticket='%s'}",
                guestId, name, age, ticketType);
    }
}
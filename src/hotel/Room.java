package hotel;

/**
 * Represents a single hotel room definition (static layout).
 * Persisted in data/hotel.csv.
 */
public class Room {

    public final int    roomId;
    public final int    roomNumber;
    public final String type;        // Standard, Double, Suite
    public final double nightlyRate;

    public Room(int roomId, int roomNumber, String type, double nightlyRate) {
        this.roomId      = roomId;
        this.roomNumber  = roomNumber;
        this.type        = type;
        this.nightlyRate = nightlyRate;
    }

    public String toCsv() {
        return String.join(",",
                String.valueOf(roomId),
                String.valueOf(roomNumber),
                type,
                String.valueOf(nightlyRate));
    }

    public static Room fromCsv(String line) {
        String[] p = line.split(",");
        if (p.length < 4) throw new IllegalArgumentException("Malformed room line: " + line);
        return new Room(
                Integer.parseInt(p[0].trim()),
                Integer.parseInt(p[1].trim()),
                p[2].trim(),
                Double.parseDouble(p[3].trim()));
    }

    @Override
    public String toString() {
        return String.format("Room{id=%d, number=%d, type='%s', rate=%.2f}",
                roomId, roomNumber, type, nightlyRate);
    }
}
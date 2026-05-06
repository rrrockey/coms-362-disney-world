package rides;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Persists rides and their queues to CSV format under data/.
 *
 *   data/rides.csv — one row per ride with inline queue data
 *
 * @author ryanrockey
 */
public class RideRepository {

    private static final String DATA_DIR  = "data";
    private static final String RIDES_FILE = DATA_DIR + "/rides.csv";

    // ------------------------------------------------------------------ //
    //  Initialisation
    // ------------------------------------------------------------------ //

    /**
     * Creates the data/ directory and seed rides file if it doesn't exist yet.
     */
    public static void initialise() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));

        Path rf = Paths.get(RIDES_FILE);
        if (!Files.exists(rf)) {
            // Create seed data with 3 popular rides
            StringBuilder sb = new StringBuilder("rideId,rideName,maxCapacity,isOperational,queueSize,guestQueue...\n");
            sb.append("1,Space Mountain,30,1,0\n");
            sb.append("2,Splash Mountain,25,1,0\n");
            sb.append("3,Big Thunder Mountain,35,1,0\n");
            Files.writeString(rf, sb.toString());
        }
    }

    // ================================================================== //
    //  R I D E S
    // ================================================================== //

    /**
     * Saves a ride (creates or updates).
     */
    public static void saveRide(Ride ride) throws IOException {
        Map<Integer, Ride> all = loadAllRidesMap();
        all.put(ride.rideId, ride);
        writeRidesMap(all);
    }

    /**
     * Finds a ride by ID, or returns null.
     */
    public static Ride findRideById(int rideId) throws IOException {
        return loadAllRidesMap().get(rideId);
    }

    /**
     * Returns every ride in the CSV.
     */
    public static List<Ride> loadAllRides() throws IOException {
        return new ArrayList<>(loadAllRidesMap().values());
    }

    /**
     * Returns only operational rides.
     */
    public static List<Ride> loadOperationalRides() throws IOException {
        List<Ride> all = loadAllRides();
        return all.stream()
                  .filter(r -> r.isOperational)
                  .toList();
    }

    // Internal: returns ID -> Ride map preserving file order
    private static Map<Integer, Ride> loadAllRidesMap() throws IOException {
        Map<Integer, Ride> map = new LinkedHashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(RIDES_FILE));
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("rideId")) continue; // skip header
            try {
                Ride r = Ride.fromCsv(line);
                map.put(r.rideId, r);
            } catch (Exception e) {
                System.err.println("[RideRepository] Skipping malformed ride line: " + line);
            }
        }
        return map;
    }

    private static void writeRidesMap(Map<Integer, Ride> map) throws IOException {
        StringBuilder sb = new StringBuilder("rideId,rideName,maxCapacity,isOperational,queueSize,guestQueue...\n");
        for (Ride r : map.values()) {
            sb.append(r.toCsv()).append("\n");
        }
        Files.writeString(Paths.get(RIDES_FILE), sb.toString());
    }

    // ------------------------------------------------------------------ //
    //  ID generation
    // ------------------------------------------------------------------ //

    /**
     * Generates the next sequential ride ID.
     */
    public static int nextRideId() throws IOException {
        int max = 0;
        for (Ride r : loadAllRides()) {
            if (r.rideId > max) max = r.rideId;
        }
        return max + 1;
    }
}

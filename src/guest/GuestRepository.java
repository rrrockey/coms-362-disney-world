package guest;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Persists guests and guest-groups to flat CSV files under data/.
 *
 *   data/guests.csv  — one row per guest
 *   data/groups.csv  — GROUP header rows interleaved with MEMBER rows
 *
 * @author ryanrockey
 */
public class GuestRepository {

    private static final String DATA_DIR    = "data";
    private static final String GUESTS_FILE = DATA_DIR + "/guests.csv";
    private static final String GROUPS_FILE = DATA_DIR + "/groups.csv";

    // ------------------------------------------------------------------ //
    //  Initialisation
    // ------------------------------------------------------------------ //

    /** Creates the data/ directory and seed files if they don't exist yet. */
    public static void initialize() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));

        Path gf = Paths.get(GUESTS_FILE);
        if (!Files.exists(gf)) {
            // header line so the file is human-readable
            Files.writeString(gf, "guestId,name,age,ticketType\n");
        }

        Path grp = Paths.get(GROUPS_FILE);
        if (!Files.exists(grp)) {
            Files.writeString(grp, "# FORMAT: GROUP,<groupName>,<managerGuestId> / MEMBER,<guestCsvRow>\n");
        }
    }

    // ================================================================== //
    //  G U E S T S
    // ================================================================== //

    /**
     * Saves a single guest.
     * If a guest with the same ID already exists the row is updated in-place.
     */
    public static void saveGuest(Guest guest) throws IOException {
        Map<Integer, Guest> all = loadAllGuestsMap();
        all.put(guest.guestId, guest);
        writeGuestsMap(all);
    }

    /** Removes a guest by ID. No-op if not found. */
    public static void deleteGuest(int guestId) throws IOException {
        Map<Integer, Guest> all = loadAllGuestsMap();
        all.remove(guestId);
        writeGuestsMap(all);
    }

    /** Finds a guest by ID, or returns null. */
    public static Guest findGuestById(int guestId) throws IOException {
        return loadAllGuestsMap().get(guestId);
    }

    /** Returns every guest in the CSV. */
    public static List<Guest> loadAllGuests() throws IOException {
        return new ArrayList<>(loadAllGuestsMap().values());
    }

    // Internal: returns ID -> Guest map preserving file order
    private static Map<Integer, Guest> loadAllGuestsMap() throws IOException {
        Map<Integer, Guest> map = new LinkedHashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(GUESTS_FILE));
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("guestId")) continue; // skip header
            try {
                Guest g = Guest.fromCsv(line);
                map.put(g.guestId, g);
            } catch (Exception e) {
                System.err.println("[GuestRepository] Skipping malformed guest line: " + line);
            }
        }
        return map;
    }

    private static void writeGuestsMap(Map<Integer, Guest> map) throws IOException {
        StringBuilder sb = new StringBuilder("guestId,name,age,ticketType\n");
        for (Guest g : map.values()) {
            sb.append(g.toCsv()).append("\n");
        }
        Files.writeString(Paths.get(GUESTS_FILE), sb.toString());
    }

    // ================================================================== //
    //  G R O U P S
    // ================================================================== //

    /** Saves (or overwrites) a group. */
    public static void saveGroup(GuestGroup group) throws IOException {
        Map<String, GuestGroup> all = loadAllGroupsMap();
        all.put(group.groupName, group);
        writeGroupsMap(all);
    }

    /** Deletes a group by name. No-op if not found. */
    public static void deleteGroup(String groupName) throws IOException {
        Map<String, GuestGroup> all = loadAllGroupsMap();
        all.remove(groupName);
        writeGroupsMap(all);
    }

    /** Finds a group by name, or returns null. */
    public static GuestGroup findGroupByName(String groupName) throws IOException {
        return loadAllGroupsMap().get(groupName);
    }

    /** Returns every group in the CSV. */
    public static List<GuestGroup> loadAllGroups() throws IOException {
        return new ArrayList<>(loadAllGroupsMap().values());
    }

    // Internal: parses the groups file into a name -> GuestGroup map
    private static Map<String, GuestGroup> loadAllGroupsMap() throws IOException {
        Map<String, GuestGroup> map    = new LinkedHashMap<>();
        GuestGroup              current = null;

        List<String> lines = Files.readAllLines(Paths.get(GROUPS_FILE));
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (line.startsWith("GROUP,")) {
                // GROUP,<groupName>,<managerGuestId>
                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;

                String groupName = parts[1];
                int    managerId = Integer.parseInt(parts[2]);

                // manager guest must already exist in guests.csv
                Guest manager = findGuestById(managerId);
                if (manager == null) {
                    System.err.println("[GuestRepository] Manager not found for group '" + groupName + "'; skipping.");
                    current = null;
                    continue;
                }
                current = new GuestGroup(manager, groupName);
                map.put(groupName, current);

            } else if (line.startsWith("MEMBER,") && current != null) {
                // MEMBER,<guestCsvRow>
                String csvRow = line.substring("MEMBER,".length());
                try {
                    Guest g = Guest.fromCsv(csvRow);
                    // manager was already added by the GuestGroup constructor
                    if (!current.containsGuest(g.guestId)) {
                        current.addGuest(g);
                    }
                } catch (Exception e) {
                }
            }
        }
        return map;
    }

    private static void writeGroupsMap(Map<String, GuestGroup> map) throws IOException {
        StringBuilder sb = new StringBuilder(
                "# FORMAT: GROUP,<groupName>,<managerGuestId> / MEMBER,<guestCsvRow>\n");
        for (GuestGroup g : map.values()) {
            sb.append(g.toCsvBlock());
        }
        Files.writeString(Paths.get(GROUPS_FILE), sb.toString());
    }

    // ------------------------------------------------------------------ //
    //  ID generation
    // ------------------------------------------------------------------ //

    /**
     * Generates the next sequential guest ID.
     * Thread-safety is not a concern for this project scope.
     */
    public static int nextGuestId() throws IOException {
        int max = 0;
        for (Guest g : loadAllGuests()) {
            if (g.guestId > max) max = g.guestId;
        }
        return max + 1;
    }
}
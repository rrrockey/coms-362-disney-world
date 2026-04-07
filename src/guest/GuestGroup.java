package guest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A party of guests traveling together.
 * The manager is always the first member added and cannot be removed.
 *
 * @author ryanrockey
 */
public class GuestGroup {

    private final ArrayList<Guest> guests;
    private final Guest            manager;
    public        String           groupName;

    public GuestGroup(Guest manager, String groupName) {
        this.manager   = manager;
        this.groupName = groupName;
        this.guests    = new ArrayList<>();
        this.guests.add(manager); // manager is always the first member
    }

    // ------------------------------------------------------------------ //
    //  Member management
    // ------------------------------------------------------------------ //

    /** Adds a guest to the group. Throws if they are already a member. */
    public void addGuest(Guest newGuest) throws Exception {
        if (containsGuest(newGuest.guestId)) {
            throw new Exception("Guest " + newGuest.name + " is already in the group.");
        }
        guests.add(newGuest);
    }

    /** Removes a guest from the group. The manager cannot be removed. */
    public void removeGuest(Guest target) throws Exception {
        if (target.guestId == manager.guestId) {
            throw new Exception("Cannot remove the group manager (" + manager.name + ").");
        }
        if (!containsGuest(target.guestId)) {
            throw new Exception("Guest " + target.name + " is not in this group.");
        }
        guests.removeIf(g -> g.guestId == target.guestId);
    }

    /** Returns true if a guest with the given ID is already in the group. */
    public boolean containsGuest(int guestId) {
        return guests.stream().anyMatch(g -> g.guestId == guestId);
    }

    public Guest            getManager() { return manager; }
    public int              size()       { return guests.size(); }
    public List<Guest>      getGuests()  { return Collections.unmodifiableList(guests); }

    // ------------------------------------------------------------------ //
    //  CSV helpers  (used by GuestRepository)
    //
    //  Group header row:   GROUP,<groupName>,<managerGuestId>
    //  Each member row:    MEMBER,<guestId>,<name>,<age>,<ticketType>
    // ------------------------------------------------------------------ //

    /**
     * Serialises the group to several CSV lines:
     *   GROUP,<groupName>,<managerGuestId>
     *   MEMBER,<guestId>,<name>,<age>,<ticketType>
     *   MEMBER,...
     */
    public String toCsvBlock() {
        StringBuilder sb = new StringBuilder();
        sb.append("GROUP,").append(groupName).append(",").append(manager.guestId).append("\n");
        for (Guest g : guests) {
            sb.append("MEMBER,").append(g.toCsv()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Group '%s' (%d members, manager: %s)\n",
                groupName, guests.size(), manager.name));
        for (Guest g : guests) {
            sb.append("  ").append(g).append("\n");
        }
        return sb.toString();
    }
}
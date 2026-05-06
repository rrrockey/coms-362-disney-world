import guest.*;
import java.util.List;

public class GuestTest {
    public static void main(String[] args) {
        try {
            GuestRepository.initialise();
            
            System.out.println("--- Testing Guest Creation ---");
            Guest g1 = new Guest(100, "Mickey", 90, "Annual Pass", 100.0, 50.0, true);
            Guest g2 = new Guest(101, "Minnie", 88, "Annual Pass", 100.0, 50.0, true);
            System.out.println("Created: " + g1);
            System.out.println("Created: " + g2);

            System.out.println("\n--- Testing Guest Repository Persistence ---");
            GuestRepository.saveGuest(g1);
            GuestRepository.saveGuest(g2);
            
            Guest loaded = GuestRepository.findGuestById(100);
            if (loaded != null && loaded.name.equals("Mickey")) {
                System.out.println("SUCCESS: Guest 100 loaded correctly.");
            } else {
                System.out.println("FAILURE: Could not load Guest 100.");
            }

            System.out.println("\n--- Testing Guest Groups ---");
            GuestGroup group = new GuestGroup(g1, "The Mice");
            group.addGuest(g2);
            System.out.println("Group created: " + group.groupName + " with manager " + group.getManager().name);
            System.out.println("Group members: " + group.getGuests().size());

            GuestRepository.saveGroup(group);
            List<GuestGroup> allGroups = GuestRepository.loadAllGroups();
            boolean found = allGroups.stream().anyMatch(grp -> grp.groupName.equals("The Mice"));
            if (found) {
                System.out.println("SUCCESS: Group 'The Mice' persisted and loaded.");
            } else {
                System.out.println("FAILURE: Group 'The Mice' not found.");
            }

            // Cleanup
            GuestRepository.deleteGuest(100);
            GuestRepository.deleteGuest(101);
            GuestRepository.deleteGroup("The Mice");
            System.out.println("\nCleanup complete.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

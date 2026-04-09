package app;

import guest.Guest;
import guest.GuestGroup;
import guest.GuestRepository;
import java.util.Scanner;


public class CheckInGuests {
	
    private static final Scanner sc = new Scanner(System.in); 
	
	public static void checkInGuests() {
        System.out.println("\n--- Check-In Guest / Party ---");

        try {
            // Collect manager (primary guest) info
            System.out.print("Primary guest name: ");
            sc.nextLine(); // consume leftover newline
            String name = sc.nextLine().trim();

            System.out.print("Age: ");
            int age = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Ticket type (e.g. 1-Day, Annual Pass): ");
            String ticket = sc.nextLine().trim();

            int     id      = GuestRepository.nextGuestId();
            Guest   manager = new Guest(id, name, age, ticket);
            GuestRepository.saveGuest(manager);

            // Optionally create a group
            System.out.print("Is this guest part of a party? (y/n): ");
            String hasParty = sc.nextLine().trim().toLowerCase();

            if (hasParty.equals("y")) {
                System.out.print("Group / party name: ");
                String groupName = sc.nextLine().trim();

                GuestGroup group = new GuestGroup(manager, groupName);

                System.out.print("How many additional guests in the party? ");
                int extras = Integer.parseInt(sc.nextLine().trim());

                for (int i = 0; i < extras; i++) {
                    System.out.printf("\nGuest %d of %d:%n", i + 1, extras);

                    System.out.print("  Name: ");
                    String eName = sc.nextLine().trim();

                    System.out.print("  Age: ");
                    int eAge = Integer.parseInt(sc.nextLine().trim());

                    System.out.print("  Ticket type: ");
                    String eTicket = sc.nextLine().trim();

                    int   eId    = GuestRepository.nextGuestId();
                    Guest eGuest = new Guest(eId, eName, eAge, eTicket);
                    GuestRepository.saveGuest(eGuest);
                    group.addGuest(eGuest);
                }

                GuestRepository.saveGroup(group);
                System.out.println("\nCheck-in complete!\n" + group);

            } else {
                System.out.println("\nCheck-in complete!\n" + manager);
            }

        } catch (Exception e) {
            System.err.println("Check-in failed: " + e.getMessage());
        }
    }
}

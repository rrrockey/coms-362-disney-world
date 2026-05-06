package app;

import guest.Guest;
import guest.GuestGroup;
import guest.GuestRepository;
import java.util.Scanner;

/**
 * CLI check-in flow for a single guest or a full party.
 */
public class CheckInGuests {

    private static final Scanner sc = app.DisneyWorld.sc;

    public static void checkInGuests() {
        System.out.println();
        printDivider("GUEST CHECK-IN");

        try {
            // ── Primary guest ────────────────────────────────────────────
            System.out.println("  Let's start with the primary guest.\n");
            Guest manager = collectGuest("Primary guest", 1, 1);
            GuestRepository.saveGuest(manager);

            // ── Party? ───────────────────────────────────────────────────
            System.out.println();
            System.out.print("  Is this guest checking in with a party? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
                printDivider("");
                System.out.println("  ✓ Check-in complete!");
                printGuestConfirmation(manager);
                pause();
                return;
            }

            // ── Group name ───────────────────────────────────────────────
            System.out.print("  Party / group name: ");
            String groupName = sc.nextLine().trim();
            GuestGroup group = new GuestGroup(manager, groupName);

            // ── Additional members ───────────────────────────────────────
            System.out.print("  How many additional guests are in the party? ");
            int extras = parseIntOrDefault(sc.nextLine().trim(), 0);

            for (int i = 0; i < extras; i++) {
                System.out.println();
                printDivider("PARTY MEMBER " + (i + 1) + " OF " + extras);
                Guest member = collectGuest("Guest", i + 1, extras);
                GuestRepository.saveGuest(member);
                group.addGuest(member);
            }

            GuestRepository.saveGroup(group);

            // ── Confirmation ─────────────────────────────────────────────
            printDivider("");
            System.out.println("  ✓ Party check-in complete!\n");
            System.out.printf("  Group   : %s%n", group.groupName);
            System.out.printf("  Manager : %s (ID %d)%n", manager.name, manager.guestId);
            System.out.printf("  Members : %d total%n", group.getGuests().size());
            System.out.println();
            System.out.printf("  %-4s  %-16s  %-4s  %-12s%n", "ID", "Name", "Age", "Ticket");
            System.out.println("  " + "-".repeat(42));
            for (Guest g : group.getGuests()) {
                String tag = g.guestId == manager.guestId ? " (manager)" : "";
                System.out.printf("  %-4d  %-16s  %-4d  %-12s%s%n",
                        g.guestId, g.name, g.age, g.ticketType, tag);
            }
            printDivider("");

        } catch (NumberFormatException e) {
            System.out.println("\n  Invalid number entered - check-in cancelled.");
        } catch (Exception e) {
            System.err.println("\n  Check-in failed: " + e.getMessage());
        }
        pause();
    }

    // ------------------------------------------------------------------ //
    //  Helpers
    // ------------------------------------------------------------------ //

    /** Prompts for name / age / ticket and returns a saved-ready Guest. */
    private static Guest collectGuest(String label, int current, int total) throws Exception {
        String prompt = total > 1
                ? String.format("  [%d/%d] ", current, total)
                : "  ";

        System.out.print(prompt + "Name: ");
        String name = sc.nextLine().trim();

        System.out.print(prompt + "Age: ");
        int age = Integer.parseInt(sc.nextLine().trim());

        System.out.println(prompt + "Ticket type:");
        System.out.println("    [1] 1-Day");
        System.out.println("    [2] Annual Pass");
        System.out.println("    [3] Other");
        System.out.print(prompt + "Choice: ");
        String ticketChoice = sc.nextLine().trim();
        String ticket = switch (ticketChoice) {
            case "1" -> "1-Day";
            case "2" -> "Annual Pass";
            default  -> {
                System.out.print(prompt + "Enter ticket type: ");
                yield sc.nextLine().trim();
            }
        };

        System.out.print(prompt + "Does this guest have a membership? (y/n): ");
        boolean isMember = sc.nextLine().trim().equalsIgnoreCase("y");

        int id = GuestRepository.nextGuestId();
        return new Guest(id, name, age, ticket, 50.0, 0.0, isMember);
    }

    private static void printGuestConfirmation(Guest g) {
        System.out.println();
        System.out.printf("  %-12s %s%n",  "Name:",       g.name);
        System.out.printf("  %-12s %d%n",  "Age:",        g.age);
        System.out.printf("  %-12s %s%n",  "Ticket:",     g.ticketType);
        System.out.printf("  %-12s %d%n",  "Guest ID:",   g.guestId);
        System.out.printf("  %-12s %s%n",  "Member:",     g.isMember ? "Yes" : "No");
        printDivider("");
    }

    private static void printDivider(String label) {
        if (label.isEmpty()) {
            System.out.println("  " + "─".repeat(52));
        } else {
            String padded = "─── " + label + " ";
            int remaining = Math.max(0, 52 - padded.length());
            System.out.println("  " + padded + "─".repeat(remaining));
        }
    }

    private static void pause() {
        System.out.print("\n  Press ENTER to continue...");
        sc.nextLine();
    }

    private static int parseIntOrDefault(String s, int fallback) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return fallback; }
    }
}
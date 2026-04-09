package app;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import guest.Guest;
import guest.GuestGroup;
import guest.GuestRepository;

/**
 * CLI interface for browsing guests and groups.
 *
 * Main menu
 *  [1] View all guests
 *  [2] View all groups
 *  [3] View guests in a group
 *  [4] Search guest by ID
 *  [5] Search guest by name
 *  [0] Back
 */
public class ViewGuests {

    private static final Scanner sc = new Scanner(System.in);

    // ------------------------------------------------------------------ //
    //  Entry point
    // ------------------------------------------------------------------ //

    public static void viewGuests() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> viewAllGuests();
                case "2" -> viewAllGroups();
                case "3" -> viewGuestsInGroup();
                case "4" -> searchById();
                case "5" -> searchByName();
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option, try again.");
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  Menu options
    // ------------------------------------------------------------------ //

    private static void viewAllGuests() {
        try {
            List<Guest> guests = GuestRepository.loadAllGuests();
            System.out.println();
            printDivider("ALL GUESTS (" + guests.size() + ")");
            if (guests.isEmpty()) {
                System.out.println("  No guests on record.");
            } else {
                printGuestTableHeader();
                for (Guest g : guests) printGuestRow(g);
            }
            printDivider("");
        } catch (IOException e) {
            System.out.println("  ERROR: Could not load guests — " + e.getMessage());
        }
        pause();
    }

    private static void viewAllGroups() {
        try {
            List<GuestGroup> groups = GuestRepository.loadAllGroups();
            System.out.println();
            printDivider("ALL GROUPS (" + groups.size() + ")");
            if (groups.isEmpty()) {
                System.out.println("  No groups on record.");
            } else {
                for (GuestGroup grp : groups) {
                    printGroupSummary(grp);
                }
            }
            printDivider("");
        } catch (IOException e) {
            System.out.println("  ERROR: Could not load groups — " + e.getMessage());
        }
        pause();
    }

    private static void viewGuestsInGroup() {
        try {
            List<GuestGroup> groups = GuestRepository.loadAllGroups();
            if (groups.isEmpty()) {
                System.out.println("  No groups on record.");
                pause();
                return;
            }

            // Show numbered list of group names for easy selection
            System.out.println();
            printDivider("SELECT A GROUP");
            for (int i = 0; i < groups.size(); i++) {
                GuestGroup grp = groups.get(i);
                System.out.printf("  [%d] %s  (%d member%s)%n",
                        i + 1,
                        grp.groupName,
                        grp.getGuests().size(),
                        grp.getGuests().size() == 1 ? "" : "s");
            }
            System.out.println("  [0] Cancel");
            printDivider("");
            System.out.print("  Choice: ");

            int idx = parseIntOrNeg(sc.nextLine().trim());
            if (idx <= 0 || idx > groups.size()) {
                System.out.println("  Cancelled.");
                return;
            }

            GuestGroup chosen = groups.get(idx - 1);
            System.out.println();
            printDivider("GROUP: " + chosen.groupName.toUpperCase());
            System.out.println("  Manager : " + formatGuestInline(chosen.getManager()));
            System.out.println("  Members : " + chosen.getGuests().size());
            System.out.println();
            printGuestTableHeader();
            for (Guest g : chosen.getGuests()) {
                String marker = (g.guestId == chosen.getManager().guestId) ? " *" : "  ";
                printGuestRow(g, marker);
            }
            System.out.println("  * = group manager");
            printDivider("");

        } catch (IOException e) {
            System.out.println("  ERROR: Could not load groups — " + e.getMessage());
        }
        pause();
    }

    private static void searchById() {
        System.out.print("\n  Enter Guest ID: ");
        int id = parseIntOrNeg(sc.nextLine().trim());
        if (id < 0) { System.out.println("  Invalid ID."); return; }

        try {
            Guest g = GuestRepository.findGuestById(id);
            System.out.println();
            if (g == null) {
                System.out.println("  No guest found with ID " + id + ".");
            } else {
                printDivider("GUEST DETAIL");
                printGuestDetail(g);
                printDivider("");
            }
        } catch (IOException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }

    private static void searchByName() {
        System.out.print("\n  Enter name (or partial): ");
        String query = sc.nextLine().trim().toLowerCase();
        if (query.isEmpty()) { System.out.println("  Nothing entered."); return; }

        try {
            List<Guest> all = GuestRepository.loadAllGuests();
            List<Guest> matches = all.stream()
                    .filter(g -> g.name.toLowerCase().contains(query))
                    .toList();

            System.out.println();
            printDivider("SEARCH RESULTS FOR \"" + query + "\" (" + matches.size() + " found)");
            if (matches.isEmpty()) {
                System.out.println("  No guests matched.");
            } else {
                printGuestTableHeader();
                for (Guest g : matches) printGuestRow(g);
            }
            printDivider("");
        } catch (IOException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        pause();
    }

    // ------------------------------------------------------------------ //
    //  Formatting helpers
    // ------------------------------------------------------------------ //

    private static void printMainMenu() {
        System.out.println();
        printDivider("GUEST VIEWER");
        System.out.println("  [1] View all guests");
        System.out.println("  [2] View all groups");
        System.out.println("  [3] View guests in a group");
        System.out.println("  [4] Search by ID");
        System.out.println("  [5] Search by name");
        System.out.println("  [0] Back");
        printDivider("");
        System.out.print("  Choice: ");
    }

    private static void printGuestTableHeader() {
        System.out.println();
        System.out.printf("  %-4s  %-16s  %-4s  %-12s  %8s  %10s%n",
                "ID", "Name", "Age", "Ticket", "Money", "GiftCredit");
        System.out.println("  " + "-".repeat(64));
    }

    private static void printGuestRow(Guest g) {
        printGuestRow(g, "  ");
    }

    private static void printGuestRow(Guest g, String prefix) {
        System.out.printf("%s%-4d  %-16s  %-4d  %-12s  %8.2f  %10.2f%n",
                prefix,
                g.guestId,
                truncate(g.name, 16),
                g.age,
                truncate(g.ticketType, 12),
                g.money,
                g.giftCredit);
    }

    private static void printGuestDetail(Guest g) {
        System.out.printf("  ID          : %d%n",   g.guestId);
        System.out.printf("  Name        : %s%n",   g.name);
        System.out.printf("  Age         : %d%n",   g.age);
        System.out.printf("  Ticket Type : %s%n",   g.ticketType);
        System.out.printf("  Money       : $%.2f%n", g.money);
        System.out.printf("  Gift Credit : $%.2f%n", g.giftCredit);
    }

    private static void printGroupSummary(GuestGroup grp) {
        System.out.printf("  %-20s  manager: %-16s  %d member%s%n",
                grp.groupName,
                grp.getManager().name,
                grp.getGuests().size(),
                grp.getGuests().size() == 1 ? "" : "s");
    }

    private static String formatGuestInline(Guest g) {
        return String.format("%s (ID %d, age %d, %s)", g.name, g.guestId, g.age, g.ticketType);
    }

    private static void printDivider(String label) {
        if (label.isEmpty()) {
            System.out.println("  " + "─".repeat(64));
        } else {
            String padded = "─── " + label + " ";
            int remaining = Math.max(0, 64 - padded.length());
            System.out.println("  " + padded + "─".repeat(remaining));
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static void pause() {
        System.out.print("\n  Press ENTER to continue...");
        sc.nextLine();
    }

    private static int parseIntOrNeg(String s) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return -1; }
    }
}
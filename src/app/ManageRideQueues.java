package app;

import guest.Guest;
import guest.GuestRepository;
import rides.Ride;
import rides.RideRepository;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * CLI flow for managing ride queues.
 * Implements the "Manage Ride Queue" use case with options to view, add, and remove guests.
 *
 * @author ryanrockey
 */
public class ManageRideQueues {

    private static final Scanner sc = DisneyWorld.sc;

    public static void manageRideQueues() {
        System.out.println();
        printDivider("MANAGE RIDE QUEUE");

        boolean exit = false;

        try {
            while (!exit) {
                // ── Load operational rides ────────────────────────────────────
                List<Ride> operationalRides = RideRepository.loadOperationalRides();

                // Precondition 0a: Check if any rides are available
                if (operationalRides.isEmpty()) {
                    System.out.println("  No rides available at this time.");
                    printDivider("");
                    pause();
                    return;
                }

                // ── Display available rides ───────────────────────────────────
                System.out.println("  Available Rides:\n");
                System.out.printf("  %-2s  %-20s  %-8s  %-10s%n", "ID", "Ride Name", "Queue", "Wait (min)");
                System.out.println("  " + "-".repeat(45));
                for (int i = 0; i < operationalRides.size(); i++) {
                    Ride r = operationalRides.get(i);
                    System.out.printf("  %-2d  %-20s  %-8d  %-10d%n",
                            i + 1,
                            truncate(r.rideName, 20),
                            r.queueSize,
                            r.getEstimatedWaitMinutes());
                }
                System.out.println();
                System.out.println("  0. Exit to main menu");
                System.out.println();

                // ── Select ride ───────────────────────────────────────────────
                System.out.print("  Select ride (0-" + operationalRides.size() + "): ");
                int rideChoice = Integer.parseInt(sc.nextLine().trim());

                // Check if user wants to exit
                if (rideChoice == 0) {
                    exit = true;
                    return;
                }

                // Alternate 5a: Invalid ride selection
                if (rideChoice < 1 || rideChoice > operationalRides.size()) {
                    System.out.println("  Invalid selection.");
                    printDivider("");
                    pause();
                    continue;
                }

                Ride selectedRide = operationalRides.get(rideChoice - 1);

                // ── Display ride queue management menu ─────────────────────────
                showRideQueueMenu(selectedRide);
            }

            printDivider("");

        } catch (NumberFormatException e) {
            System.out.println("\n  Invalid number entered - operation cancelled.");
            printDivider("");
        } catch (IOException e) {
            System.err.println("\n  System error (file issue): " + e.getMessage());
            System.err.println("  Operation cancelled.");
            printDivider("");
        } catch (Exception e) {
            System.err.println("\n  Unexpected error: " + e.getMessage());
            printDivider("");
        }
        pause();
    }

    /**
     * Display the ride queue management submenu with options to view, add, or delete guests.
     */
    private static void showRideQueueMenu(Ride ride) throws IOException {
        boolean done = false;

        while (!done) {
            System.out.println();
            printDivider("QUEUE: " + ride.rideName);
            System.out.println("  Queue Size: " + ride.queueSize + " guests");
            System.out.println("  Est. Wait: ~" + ride.getEstimatedWaitMinutes() + " minutes\n");
            System.out.println("  1. View queue");
            System.out.println("  2. Add guest to queue");
            System.out.println("  3. Remove guest from queue");
            System.out.println("  4. Back to ride selection");
            System.out.print("\n  Select option (1-4): ");

            try {
                int choice = Integer.parseInt(sc.nextLine().trim());

                switch (choice) {
                    case 1:
                        viewRideQueue(ride);
                        break;
                    case 2:
                        addGuestToQueue(ride);
                        break;
                    case 3:
                        removeGuestFromQueue(ride);
                        break;
                    case 4:
                        done = true;
                        break;
                    default:
                        System.out.println("  Invalid selection. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a number.");
            }
        }

        printDivider("");
    }

    /**
     * Display all guests currently in the ride queue.
     * @throws IOException 
     */
    private static void viewRideQueue(Ride ride) throws IOException {
        System.out.println();
        printDivider("");

        if (ride.queueSize == 0) {
            System.out.println("  The queue is empty.");
        } else {
            System.out.println("  Queue for " + ride.rideName + ":\n");
            System.out.printf("  %-2s  %-30s  %-8s%n", "Pos", "Guest Name", "Guest ID");
            System.out.println("  " + "-".repeat(42));

            // Assuming the Ride class has a way to retrieve guest IDs in order
            List<Integer> queuedGuestIds = ride.getQueuedGuestIds();
            for (int i = 0; i < queuedGuestIds.size(); i++) {
                int guestId = queuedGuestIds.get(i);
                Guest guest = GuestRepository.findGuestById(guestId);
                if (guest != null) {
                    System.out.printf("  %-2d  %-30s  %-8d%n",
                            i + 1,
                            truncate(guest.name, 30),
                            guest.guestId);
                }
            }
        }

        printDivider("");
        pause();
    }

    /**
     * Add a guest to the ride queue.
     */
    private static void addGuestToQueue(Ride ride) throws IOException {
        System.out.println();
        printDivider("");

        // ── Get guest ID ──────────────────────────────────────────────
        System.out.print("  Enter guest ID: ");
        int guestId = Integer.parseInt(sc.nextLine().trim());

        // Alternate 3a: Guest not found
        Guest guest = GuestRepository.findGuestById(guestId);
        if (guest == null) {
            System.out.println("  Guest not found.");
            printDivider("");
            pause();
            return;
        }

        // ── Check if guest is already in a queue ──────────────────────
        // Alternate 6a: Guest already in a queue
        for (Ride r : RideRepository.loadAllRides()) {
            if (r.isGuestInQueue(guestId)) {
                System.out.println("  Guest already in a queue for " + r.rideName + ".");
                printDivider("");
                pause();
                return;
            }
        }

        // ── Add guest to ride queue ───────────────────────────────────
        int position = ride.addGuestToQueue(guestId);

        // ── Update ride data ──────────────────────────────────────────
        RideRepository.saveRide(ride);

        // ── Display confirmation ──────────────────────────────────────
        printDivider("");
        System.out.println("  ✓ Guest added to queue!\n");
        System.out.printf("  Guest    : %s (ID %d)%n", guest.name, guest.guestId);
        System.out.printf("  Ride     : %s%n", ride.rideName);
        System.out.printf("  Position : %d%n", position);
        System.out.printf("  Wait Time: ~%d minutes%n", ride.getEstimatedWaitMinutes());
        printDivider("");
        pause();
    }

    /**
     * Remove a guest from the ride queue.
     */
    private static void removeGuestFromQueue(Ride ride) throws IOException {
        System.out.println();
        printDivider("");

        if (ride.queueSize == 0) {
            System.out.println("  The queue is empty. Cannot remove guests.");
            printDivider("");
            pause();
            return;
        }

        // ── Display current queue ─────────────────────────────────────
        System.out.println("  Current queue:\n");
        System.out.printf("  %-2s  %-30s  %-8s%n", "Pos", "Guest Name", "Guest ID");
        System.out.println("  " + "-".repeat(42));

        List<Integer> queuedGuestIds = ride.getQueuedGuestIds();
        for (int i = 0; i < queuedGuestIds.size(); i++) {
            int guestId = queuedGuestIds.get(i);
            Guest guest = GuestRepository.findGuestById(guestId);
            if (guest != null) {
                System.out.printf("  %-2d  %-30s  %-8d%n",
                        i + 1,
                        truncate(guest.name, 30),
                        guest.guestId);
            }
        }

        System.out.println();

        // ── Get guest ID to remove ────────────────────────────────────
        System.out.print("  Enter guest ID to remove: ");
        int guestId = Integer.parseInt(sc.nextLine().trim());

        // ── Check if guest is in this queue ───────────────────────────
        if (!ride.isGuestInQueue(guestId)) {
            System.out.println("  Guest is not in this queue.");
            printDivider("");
            pause();
            return;
        }

        // ── Get guest info for confirmation ───────────────────────────
        Guest guest = GuestRepository.findGuestById(guestId);
        if (guest == null) {
            System.out.println("  Guest not found.");
            printDivider("");
            pause();
            return;
        }

        // ── Confirm removal ───────────────────────────────────────────
        System.out.printf("  Remove %s (ID %d) from queue? (y/n): ", guest.name, guest.guestId);
        String confirmation = sc.nextLine().trim().toLowerCase();

        if (!confirmation.equals("y") && !confirmation.equals("yes")) {
            System.out.println("  Removal cancelled.");
            printDivider("");
            pause();
            return;
        }

        // ── Remove guest from queue ───────────────────────────────────
        ride.removeGuestFromQueue(guestId);

        // ── Update ride data ──────────────────────────────────────────
        RideRepository.saveRide(ride);

        // ── Display confirmation ──────────────────────────────────────
        printDivider("");
        System.out.println("  ✓ Guest removed from queue!\n");
        System.out.printf("  Guest    : %s (ID %d)%n", guest.name, guest.guestId);
        System.out.printf("  Ride     : %s%n", ride.rideName);
        System.out.printf("  New Queue Size: %d%n", ride.queueSize);
        printDivider("");
        pause();
    }

    // ------------------------------------------------------------------ //
    //  Helpers
    // ------------------------------------------------------------------ //

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

    private static String truncate(String s, int maxLen) {
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
    }
}
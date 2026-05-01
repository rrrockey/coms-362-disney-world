package app;

import guest.Guest;
import guest.GuestRepository;
import rides.Ride;
import rides.RideRepository;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * CLI flow for assigning guests to ride queues.
 * Implements the "Assign Guest to Ride Queue" use case.
 *
 * @author ryanrockey
 */
public class ManageRideQueues {

    private static final Scanner sc = new Scanner(System.in);

    public static void manageRideQueues() {
        System.out.println();
        printDivider("MANAGE RIDE QUEUE");

        try {
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

            // ── Select ride ───────────────────────────────────────────────
            System.out.print("  Select ride (1-" + operationalRides.size() + "): ");
            int rideChoice = Integer.parseInt(sc.nextLine().trim());

            // Alternate 5a: Invalid ride selection
            if (rideChoice < 1 || rideChoice > operationalRides.size()) {
                System.out.println("  Invalid selection.");
                printDivider("");
                pause();
                return;
            }

            Ride selectedRide = operationalRides.get(rideChoice - 1);

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
            int position = selectedRide.addGuestToQueue(guestId);

            // ── Update ride data ──────────────────────────────────────────
            RideRepository.saveRide(selectedRide);

            // ── Display confirmation ──────────────────────────────────────
            printDivider("");
            System.out.println("  ✓ Guest added to queue!\n");
            System.out.printf("  Guest    : %s (ID %d)%n", guest.name, guest.guestId);
            System.out.printf("  Ride     : %s%n", selectedRide.rideName);
            System.out.printf("  Position : %d%n", position);
            System.out.printf("  Wait Time: ~%d minutes%n", selectedRide.getEstimatedWaitMinutes());
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
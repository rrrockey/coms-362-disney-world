
package app;

import guest.*;

import java.util.Scanner;

/**
 * @author ryanrockey
 */
public class DisneyWorld {

    private static final Scanner sc = new Scanner(System.in); // fixed: was __sc__ but referenced as sc

    // ------------------------------------------------------------------ //

    public static void main(String[] args) {
        printBanner();
        printMenu();

        // Initialise persistence layer (creates data/ dir + CSV files if absent)
        try {
            GuestRepository.initialise();
        } catch (Exception e) {
            System.err.println("Failed to initialise data directory: " + e.getMessage());
            return;
        }

        String command = sc.next().trim();
        switch (command) {
            case "1" -> checkInGuest();
            case "2" -> System.out.println("[stub] View Attractions & Queues");
            case "3" -> System.out.println("[stub] Book Lightning Lane / Reservation");
            case "4" -> System.out.println("[stub] Manage Dining Orders");
            case "5" -> System.out.println("[stub] Process Payments");
            case "6" -> System.out.println("[stub] View Park Stats / Reports");
            case "7" -> System.out.println("[stub] Transportation / Shuttle Info");
            case "0" -> System.out.println("Goodbye!");
            default  -> System.out.println("Invalid command. Please enter 0-7.");
        }
    }

    // ------------------------------------------------------------------ //
    //  Menu option 1
    // ------------------------------------------------------------------ //

    private static void checkInGuest() {
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

    // ------------------------------------------------------------------ //
    //  UI helpers
    // ------------------------------------------------------------------ //

    private static void printBanner() {
    	System.out.println("              .=-.-.  ,-,--.  .-._           ,----.                 \n"
    			+ "  _,..---._  /==/_ /,-.'-  _\\/==/ \\  .-._ ,-.--` , \\ ,--.-.  .-,--. \n"
    			+ "/==/,   -  \\|==|, |/==/_ ,_.'|==|, \\/ /, /==|-  _.-`/==/- / /=/_ /  \n"
    			+ "|==|   _   _\\==|  |\\==\\  \\   |==|-  \\|  ||==|   `.-.\\==\\, \\/=/. /   \n"
    			+ "|==|  .=.   |==|- | \\==\\ -\\  |==| ,  | -/==/_ ,    / \\==\\  \\/ -/    \n"
    			+ "|==|,|   | -|==| ,| _\\==\\ ,\\ |==| -   _ |==|    .-'   |==|  ,_/     \n"
    			+ "|==|  '='   /==|- |/==/\\/ _ ||==|  /\\ , |==|_  ,`-._  \\==\\-, /      \n"
    			+ "|==|-,   _`//==/. /\\==\\ - , //==/, | |- /==/ ,     /  /==/._/       \n"
    			+ "`-.`.____.' `--`-`  `--`---' `--`./  `--`--`-----``   `--`-`        \n"
    			+ "         ,-.-.    _,.---._                                          \n"
    			+ ",-..-.-./  \\==\\ ,-.' , -  `.   .-.,.---.    _.-.     _,..---._      \n"
    			+ "|, \\=/\\=|- |==|/==/_,  ,  - \\ /==/  `   \\ .-,.'|   /==/,   -  \\     \n"
    			+ "|- |/ |/ , /==/==|   .=.     |==|-, .=., |==|, |   |==|   _   _\\    \n"
    			+ " \\, ,     _|==|==|_ : ;=:  - |==|   '='  /==|- |   |==|  .=.   |    \n"
    			+ " | -  -  , |==|==| , '='     |==|- ,   .'|==|, |   |==|,|   | -|    \n"
    			+ "  \\  ,  - /==/ \\==\\ -    ,_ /|==|_  . ,'.|==|- `-._|==|  '='   /    \n"
    			+ "  |-  /\\ /==/   '.='. -   .' /==/  /\\ ,  )==/ - , ,/==|-,   _`/     \n"
    			+ "  `--`  `--`      `--`--''   `--`-`--`--'`--`-----'`-.`.____.'      \n");
    }

    private static void printMenu() {
        System.out.print(
            " +--------------------------------------------------+\n"
          + " |                                                  |\n"
          + " | 1) Check-in Guest / Party                        |\n"
          + " | 2) View Attractions & Queues                     |\n"
          + " | 3) Book Lightning Lane / Reservation             |\n"
          + " | 4) Manage Dining Orders                          |\n"
          + " | 5) Process Payments                              |\n"
          + " | 6) View Park Stats / Reports                     |\n"
          + " | 7) Transportation / Shuttle Info                 |\n"
          + " | 0) Exit                                          |\n"
          + " |                                                  |\n"
          + " +--------------------------------------------------+\n\n"
          + " Please enter a command (0-7): ");
    }
}

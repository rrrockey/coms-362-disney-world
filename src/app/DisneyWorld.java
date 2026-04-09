
package app;

import guest.*;
import retailsales.*;
import employee.*;


import app.CheckInGuests;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author ryanrockey
 */
public class DisneyWorld {


    private static final Scanner sc = new Scanner(System.in); // fixed: was __sc__ but referenced as sc
    private static final RetailSalesRegister register = new RetailSalesRegister();
    private static final RetailSalesEmployee employee = new RetailSalesEmployee();
    private static final List<RetailItem> inventory = new ArrayList<>();

    // ------------------------------------------------------------------ //

    public static void main(String[] args) {
        printBanner();
        printMenu();

        // Initialize persistence layer (creates data/ dir + CSV files if absent)
        try {

            GuestRepository.initialise();
            initInventory();
        } catch (Exception e) {
            System.err.println("Failed to initialise data directory: " + e.getMessage());
            return;
        }
        while (true) {
        	
        	System.out.print("\nEnter a command: ");

	        String command = sc.next().trim();
	        switch (command) {
	            case "1" -> CheckInGuests.checkInGuests();
	            case "2" -> ViewGuests.viewGuests();
	            case "3" -> System.out.println("[stub] Book Lightning Lane / Reservation");
	            case "4" -> System.out.println("[stub] Manage Dining Orders");
	            case "5" -> processRetailSales();
	            case "6" -> System.out.println("[stub] View Park Stats / Reports");
	            case "7" -> System.out.println("[stub] Transportation / Shuttle Info");
	            case "0" -> System.out.println("Goodbye!");
	            default  -> System.out.println("Invalid command. Please enter 0-7.");
	        }
        }
    }

    private static void initInventory() {
        if (inventory.isEmpty()) {
            inventory.add(new RetailItem("Mickey Ears", 25.0, 10, 20));
            inventory.add(new RetailItem("Disney Pin", 12.5, 50, 100));
            inventory.add(new RetailItem("MagicBand+", 45.0, 5, 15));
        }
    }

    // ------------------------------------------------------------------ //
    //  Menu option 5: Retail Sales Simulation
    // ------------------------------------------------------------------ //

    private static void processRetailSales() {
        while (true) {
            System.out.println("\n--- Retail Sales Simulation ---");
            System.out.println("1) Purchase Item");
            System.out.println("2) Return Item");
            System.out.println("3) Restock Item (Employee)");
            System.out.println("4) View Sales Ledger");
            System.out.println("0) Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = sc.next().trim();
            sc.nextLine(); // consume newline

            switch (choice) {
                case "1" -> handlePurchase();
                case "2" -> handleReturn();
                case "3" -> handleRestock();
                case "4" -> viewLedger();
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void handlePurchase() {
        try {
            List<Guest> guests = GuestRepository.loadAllGuests();
            if (guests.isEmpty()) {
                System.out.println("No guests found. Please check-in a guest first.");
                return;
            }

            System.out.println("\nSelect Guest:");
            for (int i = 0; i < guests.size(); i++) {
                System.out.println((i + 1) + ") " + guests.get(i).name + " (Money: " + guests.get(i).money + ", Credit: " + guests.get(i).giftCredit + ")");
            }
            int gIdx = Integer.parseInt(sc.nextLine()) - 1;
            Guest guest = guests.get(gIdx);

            System.out.println("\nSelect Item:");
            for (int i = 0; i < inventory.size(); i++) {
                RetailItem item = inventory.get(i);
                System.out.println((i + 1) + ") " + item.name + " ($" + item.price + ") [Stock: " + item.stock + "]");
            }
            int iIdx = Integer.parseInt(sc.nextLine()) - 1;
            RetailItem item = inventory.get(iIdx);

            System.out.print("Payment Method (Cash/Gift Credit): ");
            String paymentType = sc.nextLine().trim();

            register.processPurchase(guest, item, paymentType);
            GuestRepository.saveGuest(guest); // Persist updated guest funds
        } catch (Exception e) {
            System.err.println("Purchase failed: " + e.getMessage());
        }
    }

    private static void handleReturn() {
        try {
            List<Transaction> ledger = register.getSalesLedger();
            if (ledger.isEmpty()) {
                System.out.println("No transactions in ledger to return.");
                return;
            }

            System.out.println("\nSelect Transaction to Return:");
            for (int i = 0; i < ledger.size(); i++) {
                System.out.println((i + 1) + ") " + ledger.get(i));
            }
            int tIdx = Integer.parseInt(sc.nextLine()) - 1;
            Transaction tx = ledger.get(tIdx);

            List<Guest> guests = GuestRepository.loadAllGuests();
            System.out.println("\nSelect Guest for Refund:");
            for (int i = 0; i < guests.size(); i++) {
                System.out.println((i + 1) + ") " + guests.get(i).name);
            }
            int gIdx = Integer.parseInt(sc.nextLine()) - 1;
            Guest guest = guests.get(gIdx);

            System.out.print("Refund Type (Cash/Gift Card Credit): ");
            String refundType = sc.nextLine().trim();

            register.processReturn(guest, tx, refundType);
            GuestRepository.saveGuest(guest); // Persist updated guest funds
        } catch (Exception e) {
            System.err.println("Return failed: " + e.getMessage());
        }
    }

    private static void handleRestock() {
        try {
            System.out.println("\nSelect Item to Restock:");
            for (int i = 0; i < inventory.size(); i++) {
                RetailItem item = inventory.get(i);
                System.out.println((i + 1) + ") " + item.name + " [Stock: " + item.stock + "/" + item.capacity + "]");
            }
            int iIdx = Integer.parseInt(sc.nextLine()) - 1;
            RetailItem item = inventory.get(iIdx);

            System.out.print("Enter quantity to restock: ");
            int qty = Integer.parseInt(sc.nextLine());

            employee.restockItem(item, qty);
        } catch (Exception e) {
            System.err.println("Restock failed: " + e.getMessage());
        }
    }

    private static void viewLedger() {
        List<Transaction> ledger = register.getSalesLedger();
        if (ledger.isEmpty()) {
            System.out.println("Sales ledger is empty.");
        } else {
            System.out.println("\n--- Sales Ledger ---");
            ledger.forEach(System.out::println);
        }
    }

    // ------------------------------------------------------------------ //
    //  UI helpers
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
          + " | 2) View Guests / Guest Groups                    |\n"
          + " | 3) Book Lightning Lane / Reservation             |\n"
          + " | 4) Manage Dining Orders                          |\n"
          + " | 5) Process Retail Sales                          |\n"
          + " | 6) View Park Stats / Reports                     |\n"
          + " | 7) Transportation / Shuttle Info                 |\n"
          + " | 0) Exit                                          |\n"
          + " |                                                  |\n"
          + " +--------------------------------------------------+\n");
    }
}

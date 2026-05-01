
package app;

import guest.*;
import parkevents.EventRepository;
import parkevents.ManageParkEvents;
import retailsales.*;
import employee.*;
import hotel.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author ryanrockey
 */
public class DisneyWorld {


    private static final Scanner sc = new Scanner(System.in); 
    
    public static void main(String[] args) {
        printBanner();
        printMenu();
        boolean running = true;

        // Initialize persistence layer (creates data/ dir + CSV files if absent)
        try {

            GuestRepository.initialise();
            HotelRepository.initialise();
            EventRepository.initialise();
            RetailSales.initInventory();
        } catch (Exception e) {
            System.err.println("Failed to initialise data directory: " + e.getMessage());
            return;
        }
        while (running) {
        	
        	System.out.print("\nEnter a command [0-7]: ");

	        String command = sc.next().trim();
	        switch (command) {
	            case "1" -> {
	            	CheckInGuests.checkInGuests();
	            	printMenu(); // print the menu again when the user falls out
	            }
	            case "2" -> {
	            	ViewGuests.viewGuests();
	            	printMenu();
	            }
	            case "3" -> {
	            	RetailSales.processRetailSales();
	            	printMenu();

	            }
	            case "4" -> {
                    ManageDining.manageDining();
                    printMenu();
                }
	            case "5" -> {
	            	ManageHotels.manageHotels();
	            	printMenu();
	            }
                case "6" -> {
                    ManageParkEvents.manageParkEvents();
                    printMenu();
                }
	            case "0" -> {System.out.println("Goodbye!"); running = false;}
	            default  -> System.out.println("Invalid command. Please enter 0-6.");

	        }
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
            " \n+--------------------------------------------------+\n"
          + " |                                                  |\n"
          + " | 1) Check-in Guest / Party                        |\n"
          + " | 2) View Guests / Guest Groups                    |\n"
          + " | 3) Process Retail Sales                          |\n"
          + " | 4) Manage Dining                                 |\n"
          + " | 5) Manage Hotels                                 |\n"
          + " | 6) Manage Park Events                            |\n"
          + " | 0) Exit                                          |\n"
          + " |                                                  |\n"
          + " +--------------------------------------------------+\n");
    }
}

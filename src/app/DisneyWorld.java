
package app;

import guest.*;

import app.CheckInGuests;

import java.util.Scanner;

/**
 * @author ryanrockey
 */
public class DisneyWorld {

    private static final Scanner sc = new Scanner(System.in); 

    // ------------------------------------------------------------------ //

    public static void main(String[] args) {
        printBanner();
        printMenu();

        // Initialize persistence layer (creates data/ dir + CSV files if absent)
        try {
            GuestRepository.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialise data directory: " + e.getMessage());
            return;
        }
        
        while (true) {
        	System.out.print("Please enter a command (0-7): ");
	        String command = sc.next().trim();
	        switch (command) {
	            case "1" -> CheckInGuests.checkInGuests();
	            case "2" -> System.out.println("[stub] View Attractions & Queues");
	            case "3" -> System.out.println("[stub] Book Lightning Lane / Reservation");
	            case "4" -> System.out.println("[stub] Manage Dining Orders");
	            case "5" -> System.out.println("[stub] Process Payments");
	            case "6" -> System.out.println("[stub] View Park Stats / Reports");
	            case "7" -> System.out.println("[stub] Transportation / Shuttle Info");
	            case "8" -> ViewGuests.ViewGuests();
	            case "0" -> {
	            	System.out.println("Goodbye!"); 
	            	break;
            	}
	            default  -> System.out.println("Invalid command. Please enter 0-7.");
	        }
        }
    }

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
          + " | 2) View Attractions & Queues                     |\n"
          + " | 3) Book Lightning Lane / Reservation             |\n"
          + " | 4) Manage Dining Orders                          |\n"
          + " | 5) Process Payments                              |\n"
          + " | 6) View Park Stats / Reports                     |\n"
          + " | 7) Transportation / Shuttle Info                 |\n"
          + " | 8) View Guests / Guest Groups                    |\n"
          + " | 0) Exit                                          |\n"
          + " |                                                  |\n"
          + " +--------------------------------------------------+\n\n");
    }
}

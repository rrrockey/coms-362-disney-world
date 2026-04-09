import guest.Guest;
import retailsales.*;
import employee.RetailSalesEmployee;

public class RetailTest {
    public static void main(String[] args) {
        // Setup
        Guest guest = new Guest(1, "Charlie", 25, "Annual Pass", 100.0, 50.0);
        RetailItem mickeyEars = new RetailItem("Mickey Ears", 25.0, 5, 10);
        RetailSalesRegister register = new RetailSalesRegister();
        RetailSalesEmployee employee = new RetailSalesEmployee();

        System.out.println("Initial State:");
        System.out.println(guest);
        System.out.println("Stock: " + mickeyEars.stock);

        // 1. Purchase with Cash
        System.out.println("\nTesting Purchase with Cash:");
        register.processPurchase(guest, mickeyEars, "Cash");
        System.out.println(guest);
        System.out.println("Stock: " + mickeyEars.stock);

        // 2. Purchase with Gift Credit
        System.out.println("\nTesting Purchase with Gift Credit:");
        register.processPurchase(guest, mickeyEars, "Gift Credit");
        System.out.println(guest);
        System.out.println("Stock: " + mickeyEars.stock);

        // 3. Return with Cash Refund
        System.out.println("\nTesting Return with Cash Refund:");
        Transaction t1 = register.getSalesLedger().get(0);
        register.processReturn(guest, t1, "Cash");
        System.out.println(guest);
        System.out.println("Stock: " + mickeyEars.stock);

        // 4. Employee Restocks
        System.out.println("\nTesting Employee Restock:");
        employee.restockItem(mickeyEars, 10); // Exceeds capacity (5+1=6, 6+10=16 > 10)
        System.out.println("Stock: " + mickeyEars.stock);
    }
}

import guest.Guest;
import retailsales.*;
import retailsales.command.*;
import employee.RetailSalesEmployee;

public class RetailTest {
    public static void main(String[] args) {
        // Setup — member guest with $100 cash and $50 gift credit
        Guest guest = new Guest(1, "Charlie", 25, "Annual Pass", 100.0, 50.0, true);
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

        // ---------------------------------------------------------------
        // Use Case: Apply membership discount to purchase
        // ---------------------------------------------------------------

        // Reset guest funds for discount tests
        guest.money      = 100.0;
        guest.giftCredit = 50.0;
        RetailItem mug = new RetailItem("Disney Mug", 20.0, 5, 10);

        // 5. Membership discount — Main success scenario (cash, 10% off $20 = $18)
        System.out.println("\n--- UC: Membership Discount (Cash - Main Success Scenario) ---");
        register.processMembershipDiscountPurchase(employee, guest, mug, "Cash");
        System.out.println(guest);
        System.out.println("Stock: " + mug.stock);

        // 6. Membership discount — Alternate flow A: gift credit payment (10% off $20 = $18)
        System.out.println("\n--- UC: Membership Discount (Gift Credit - Alternate Flow A) ---");
        register.processMembershipDiscountPurchase(employee, guest, mug, "Gift Credit");
        System.out.println(guest);
        System.out.println("Stock: " + mug.stock);

        // 7. Membership discount — Alternate flow B: payment declined (broke guest)
        System.out.println("\n--- UC: Membership Discount (Payment Declined - Alternate Flow B) ---");
        Guest brokeGuest = new Guest(2, "Daisy", 30, "Annual Pass", 0.0, 0.0, true);
        register.processMembershipDiscountPurchase(employee, brokeGuest, mug, "Cash");
        System.out.println(brokeGuest);

        // 8. Membership discount — No membership (precondition not met)
        System.out.println("\n--- UC: Membership Discount (No Membership - Precondition Failure) ---");
        Guest nonMember = new Guest(3, "Goofy", 40, "1-Day", 100.0, 0.0, false);
        register.processMembershipDiscountPurchase(employee, nonMember, mug, "Cash");
        System.out.println(nonMember);

        // ---------------------------------------------------------------
        // Command Pattern validation — same operations via RetailCommand
        // ---------------------------------------------------------------
        System.out.println("\n========== Command Pattern Tests ==========");

        Guest cmdGuest = new Guest(10, "CmdGuest", 25, "Annual Pass", 100.0, 50.0, true);
        RetailItem cmdItem = new RetailItem("MagicBand+", 45.0, 5, 10);

        // C1. PurchaseCommand (cash)
        System.out.println("\n[C1] PurchaseCommand (Cash):");
        register.executeCommand(new PurchaseCommand(register, cmdGuest, cmdItem, "Cash"));
        System.out.println(cmdGuest);
        System.out.println("Stock: " + cmdItem.stock);

        // C2. MembershipDiscountPurchaseCommand (gift credit)
        System.out.println("\n[C2] MembershipDiscountPurchaseCommand (Gift Credit):");
        register.executeCommand(new MembershipDiscountPurchaseCommand(
                register, employee, cmdGuest, cmdItem, "Gift Credit"));
        System.out.println(cmdGuest);
        System.out.println("Stock: " + cmdItem.stock);

        // C3. ReturnCommand — return the first command-based transaction
        System.out.println("\n[C3] ReturnCommand (Cash refund):");
        Transaction cmdTx = register.getSalesLedger().get(register.getSalesLedger().size() - 2);
        register.executeCommand(new ReturnCommand(register, cmdGuest, cmdTx, "Cash"));
        System.out.println(cmdGuest);
        System.out.println("Stock: " + cmdItem.stock);

        // C4. RestockCommand
        System.out.println("\n[C4] RestockCommand (+3 units):");
        register.executeCommand(new RestockCommand(employee, cmdItem, 3));
        System.out.println("Stock: " + cmdItem.stock);
    }
}

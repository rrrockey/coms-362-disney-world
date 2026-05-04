package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import employee.RetailSalesEmployee;
import guest.Guest;
import guest.GuestRepository;
import retailsales.RetailItem;
import retailsales.RetailSalesRegister;
import retailsales.RetailWarehouse;
import retailsales.Transaction;

public class RetailSales {
    private static final Scanner sc = new Scanner(System.in); 
    private static final RetailSalesRegister register = new RetailSalesRegister();
    private static final RetailSalesEmployee employee = new RetailSalesEmployee();
    private static final RetailWarehouse warehouse = new RetailWarehouse();
    private static final List<RetailItem> inventory = new ArrayList<>();
    
    // ------------------------------------------------------------------ //
    //  Menu option 3: Retail Sales Simulation
    // ------------------------------------------------------------------ //

    public static void initInventory() {
        if (inventory.isEmpty()) {
            inventory.add(new RetailItem("Mickey Ears", 25.0, 10, 20));
            inventory.add(new RetailItem("Disney Pin", 12.5, 50, 100));
            inventory.add(new RetailItem("MagicBand+", 45.0, 5, 15));

            // Initial warehouse stock
            warehouse.receiveShipment("Mickey Ears", 50);
            warehouse.receiveShipment("Disney Pin", 200);
            warehouse.receiveShipment("MagicBand+", 30);
        }
    }

    public static void processRetailSales() {
        while (true) {
            System.out.println("\n--- Retail Sales Simulation ---");
            System.out.println("1) Purchase Item");
            System.out.println("2) Return Item");
            System.out.println("3) Restock from Warehouse");
            System.out.println("4) Receive Shipment (Warehouse)");
            System.out.println("5) View Sales Ledger");
            System.out.println("6) View Warehouse Inventory");
            System.out.println("0) Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = sc.next().trim();
            sc.nextLine(); // consume newline

            switch (choice) {
                case "1" -> handlePurchase();
                case "2" -> handleReturn();
                case "3" -> handleWarehouseRestock();
                case "4" -> handleReceiveShipment();
                case "5" -> viewLedger();
                case "6" -> viewWarehouseInventory();
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void handlePurchase() {
        try {
            List<Guest> guests = GuestRepository.loadAllGuests();
            if (guests.isEmpty()) {
                System.out.println("No guests found. Please check-in a guest first.");
                return;
            }

            System.out.println("\nSelect Guest:");
            for (int i = 0; i < guests.size(); i++) {
                Guest g = guests.get(i);
                String memberTag = g.isMember ? " [Member]" : "";
                System.out.println((i + 1) + ") " + g.name + memberTag + " (Money: " + g.money + ", Credit: " + g.giftCredit + ")");
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

            // Apply membership discount if the guest is a member and opts in
            if (guest.isMember) {
                System.out.print("Apply membership discount (10% off)? (y/n): ");
                boolean applyDiscount = sc.nextLine().trim().equalsIgnoreCase("y");
                if (applyDiscount) {
                    register.processMembershipDiscountPurchase(employee, guest, item, paymentType);
                    GuestRepository.saveGuest(guest);
                    return;
                }
            }

            register.processPurchase(guest, item, paymentType);
            GuestRepository.saveGuest(guest); // Persist updated guest funds
        } catch (Exception e) {
            System.err.println("Purchase failed: " + e.getMessage());
        }
    }

    public static void handleReturn() {
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

    public static void handleWarehouseRestock() {
        try {
            System.out.println("\nSelect Item to Restock from Warehouse:");
            for (int i = 0; i < inventory.size(); i++) {
                RetailItem item = inventory.get(i);
                System.out.println((i + 1) + ") " + item.name + " [Stock: " + item.stock + "/" + item.capacity + "]");
            }
            int iIdx = Integer.parseInt(sc.nextLine()) - 1;
            RetailItem item = inventory.get(iIdx);

            System.out.print("Enter quantity to request from warehouse: ");
            int qty = Integer.parseInt(sc.nextLine());

            employee.requestWarehouseRestock(warehouse, item, qty);
        } catch (Exception e) {
            System.err.println("Warehouse restock failed: " + e.getMessage());
        }
    }

    public static void handleReceiveShipment() {
        try {
            System.out.println("\nSelect Item for Warehouse Shipment:");
            for (int i = 0; i < inventory.size(); i++) {
                System.out.println((i + 1) + ") " + inventory.get(i).name);
            }
            int iIdx = Integer.parseInt(sc.nextLine()) - 1;
            if (iIdx < 0 || iIdx >= inventory.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            String name = inventory.get(iIdx).name;

            System.out.print("Enter quantity received: ");
            int qty = Integer.parseInt(sc.nextLine());

            warehouse.receiveShipment(name, qty);
        } catch (Exception e) {
            System.err.println("Shipment receiving failed: " + e.getMessage());
        }
    }

    public static void viewLedger() {
        List<Transaction> ledger = register.getSalesLedger();
        if (ledger.isEmpty()) {
            System.out.println("Sales ledger is empty.");
        } else {
            System.out.println("\n--- Sales Ledger ---");
            ledger.forEach(System.out::println);
        }
    }

    public static void viewWarehouseInventory() {
        var inv = warehouse.getInventory();
        if (inv.isEmpty()) {
            System.out.println("Warehouse inventory is empty.");
        } else {
            System.out.println("\n--- Warehouse Inventory ---");
            inv.forEach((item, qty) -> System.out.println(item + ": " + qty));
        }
    }
}

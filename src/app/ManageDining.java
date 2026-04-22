package app;

import  dining.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ManageDining {

    private static final Scanner sc = new Scanner(System.in);

    private static final String CONCESSIONS_MENU_FILE = "data/menu.csv";
    private static final String ORDER_FILE = "data/orders.csv";
    private static final String INVENTORY_FILE = "data/inventory.csv";

    private static final FoodInventory concessionsInventory = new FoodInventory(ServiceType.CONCESSIONS);
    private static final Concessions concessions = new Concessions(concessionsInventory);

    public static void manageConcessions () {
        boolean running = true;

        concessionsInventory.loadStockFromCSV(INVENTORY_FILE);
        concessions.loadMenuFromCSV(CONCESSIONS_MENU_FILE);
        concessions.loadOrdersFromCSV(ORDER_FILE);

        while (running) {
            printConcessionsMenu();

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> viewMenu();
                case "2" -> openOrder();
                case "3" -> viewOrderStatus();
                case "0" -> {
                    concessions.saveOrdersToCSV(ORDER_FILE);
                    concessionsInventory.saveStockToCSV(INVENTORY_FILE);
                    running = false;
                }
                default -> System.out.println("Invalid option, try again");

            }
        }

        concessions.saveOrdersToCSV("orders.csv");
        concessionsInventory.saveStockToCSV("inventory.csv");
    }

    public static void viewMenu () {
        System.out.println();
        printDivider("CONCESSIONS MENU");

        List<MenuItem> menu = concessions.getMenu();

        if (menu.isEmpty()) {
            System.out.println("  No menu items available.");
            return;
        }

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.get(i);

            System.out.printf("  [%d] %s - $%.2f%n", i + 1, item.getName(), item.getPrice());
            System.out.printf("      %s%n", item.getDescription());

            int quantity = concessionsInventory.getQuantity(item.getName());
            if (quantity > 0) {
                System.out.printf("      In Stock: %d%n", quantity);
            }
            else {
                System.out.println("      OUT OF STOCK");
            }
            System.out.println();
        }
    }

    public static void openOrder() {
        List<MenuItem> menu = concessions.getMenu();

        if (menu.isEmpty()) {
            System.out.println("  Cannot open order: menu is empty.");
            return;
        }

        List<String> selectedItemNames = new ArrayList<>();
        boolean ordering = true;

        while (ordering) {
            System.out.println();
            printDivider("OPEN ORDER");

            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.get(i);
                System.out.printf("  [%d] %s - $%.2f%n", i + 1, item.getName(), item.getPrice());
            }
            System.out.println("  [0] Finish Order");
            System.out.print("  Choice: ");

            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                ordering = false;
                continue;
            }

            try {
                int itemIndex = Integer.parseInt(input) - 1;

                if (itemIndex < 0 || itemIndex >= menu.size()) {
                    System.out.println("  Invalid item number.");
                    continue;
                }

                MenuItem selectedItem = menu.get(itemIndex);

                if (!concessionsInventory.checkItemAvailability(selectedItem.getName())) {
                    System.out.println("  Sorry, that item is out of stock.");
                    continue;
                }

                selectedItemNames.add(selectedItem.getName());
                System.out.println("  Added: " + selectedItem.getName());

            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number.");
            }
        }

        if (selectedItemNames.isEmpty()) {
            System.out.println("  No items selected. Order cancelled.");
            return;
        }

        double total = 0.0;
        System.out.println();
        printDivider("ORDER SUMMARY");

        for (String name : selectedItemNames) {
            MenuItem item = concessions.viewItemDetails(name);
            if (item != null) {
                System.out.printf("  - %s ($%.2f)%n", item.getName(), item.getPrice());
                total += item.getPrice();
            }
        }

        System.out.printf("  Total: $%.2f%n", total);
        System.out.print("  Confirm order? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (!confirm.equals("y")) {
            System.out.println("  Order cancelled.");
            return;
        }

        Order order = concessions.placeOrder(selectedItemNames);

        if (order == null) {
            System.out.println("  No valid items were ordered.");
            return;
        }

        System.out.println("  Order has been saved successfully.");
        System.out.println("  Order ID: " + order.getOrderId());
        System.out.println("  Status: " + order.getStatus());
    }


    public static void viewOrderStatus() {
        System.out.print("  Enter order ID: ");
        String input = sc.nextLine().trim();

        try {
            int orderId = Integer.parseInt(input);
            String status = concessions.viewOrderStatus(orderId);
            System.out.println("  Order Status: " + status);
        } catch (NumberFormatException e) {
            System.out.println("  Invalid order ID.");
        }
    }


    private static void printConcessionsMenu() {
        System.out.println();
        printDivider("CONCESSIONS MANAGEMENT");
        System.out.println("  [1] View Concessions Menu");
        System.out.println("  [2] Open an Order");
        System.out.println("  [3] View Order Status");
        System.out.println("  [0] Back");
        printDivider("");
        System.out.print("  Choice: ");
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
}

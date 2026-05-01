package app;

import  dining.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ManageDining {

    private static final Scanner sc = new Scanner(System.in);

    private static final String MENU_FILE = "data/menu.csv";
    private static final String ORDER_FILE = "data/orders.csv";
    private static final String INVENTORY_FILE = "data/inventory.csv";
    private static final String RESERVATION_FILE = "data/reservations.csv";

    private static final DiningInventory concessionsInventory = new DiningInventory(ServiceType.CONCESSIONS);
    private static final Concessions concessions = new Concessions(concessionsInventory);

    private static final DiningInventory restaurantInventory = new DiningInventory(ServiceType.RESTAURANT);
    private static final Restaurant restaurant = new Restaurant(restaurantInventory);

    public static void manageDining () {
        boolean running = true;

        while (running) {

            printDiningMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> manageConcessions();
                case "2" -> manageRestaurant();
                case "0" -> running = false;
                default -> System.out.println("Invalid option, try again");

            }
        }
    }

    public static void manageConcessions() {
        boolean running = true;

        concessionsInventory.loadStockFromCSV(INVENTORY_FILE);
        concessions.loadMenuFromCSV(MENU_FILE);
        concessions.loadOrdersFromCSV(ORDER_FILE);

        while (running) {
            printConcessionsMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> viewMenu(concessions, concessionsInventory, "CONCESSIONS MENU");
                case "2" -> openOrder(concessions, concessionsInventory, ORDER_FILE, INVENTORY_FILE, "OPEN CONCESSIONS ORDER");
                case "3" -> viewOrderStatus(concessions);
                case "4" -> viewInventory(concessionsInventory);
                case "5" -> updateInventoryItems(concessions, concessionsInventory);
                case "0" -> {
                    concessions.saveOrdersToCSV(ORDER_FILE);
                    concessionsInventory.saveStockToCSV(INVENTORY_FILE);
                    running = false;
                }
                default -> System.out.println("Invalid option, try again");
            }
        }
    }

    public static void manageRestaurant() {
        boolean running = true;

        restaurantInventory.loadStockFromCSV(INVENTORY_FILE);
        restaurant.loadMenuFromCSV(MENU_FILE);
        restaurant.loadOrdersFromCSV(ORDER_FILE);
        restaurant.loadReservationsFromCSV(RESERVATION_FILE);

        while (running) {
            printRestaurantMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> viewMenu(restaurant, restaurantInventory, "RESTAURANT MENU");
                case "2" -> openOrder(restaurant, restaurantInventory, ORDER_FILE, INVENTORY_FILE, "OPEN RESTAURANT ORDER");
                case "3" -> viewOrderStatus(restaurant);
                case "4" -> makeRestaurantReservation();
                case "5" -> viewRestaurantReservation();
                case "6" -> cancelRestaurantReservation();
                case "7" -> viewInventory(restaurantInventory);
                case "8" -> updateInventoryItems(restaurant, restaurantInventory);
                case "0" -> {
                    restaurant.saveOrdersToCSV(ORDER_FILE);
                    restaurantInventory.saveStockToCSV(INVENTORY_FILE);
                    restaurant.saveReservationsToCSV(RESERVATION_FILE);
                    running = false;
                }
                default -> System.out.println("Invalid option, try again");
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  METHODS FOR CONCESSIONS FUNCTIONALITY
    // ------------------------------------------------------------------ //

    private static void viewMenu(FoodService service, DiningInventory inventory, String label) {
        System.out.println();
        printDivider(label);

        List<MenuItem> menu = service.getMenu();

        if (menu.isEmpty()) {
            System.out.println("  No menu items available.");
            return;
        }

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.get(i);

            System.out.printf("  [%d] %s - $%.2f%n", i + 1, item.getName(), item.getPrice());
            System.out.printf("      %s%n", item.getDescription());

            int quantity = inventory.getQuantity(item.getName());
            if (quantity > 0) {
                System.out.printf("      In Stock: %d%n", quantity);
            } else {
                System.out.println("      OUT OF STOCK");
            }
            System.out.println();
        }
    }

    private static void openOrder(FoodService service, DiningInventory inventory, String orderFile, String inventoryFile, String label) {
        List<MenuItem> menu = service.getMenu();

        if (menu.isEmpty()) {
            System.out.println("  Cannot open order: menu is empty.");
            return;
        }

        List<String> selectedItemNames = new ArrayList<>();
        boolean ordering = true;

        while (ordering) {
            System.out.println();
            printDivider(label);

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

                if (!inventory.checkItemAvailability(selectedItem.getName())) {
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
            MenuItem item = service.viewItemDetails(name);
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

        Order order = service.placeOrder(selectedItemNames);

        if (order == null) {
            System.out.println("  No valid items were ordered.");
            return;
        }

        service.saveOrdersToCSV(orderFile);
        inventory.saveStockToCSV(inventoryFile);

        System.out.println("  Order has been saved successfully.");
        System.out.println("  Order ID: " + order.getOrderId());
        System.out.println("  Status: " + order.getStatus());
    }

    private static void viewOrderStatus(FoodService service) {
        System.out.print("  Enter order ID: ");
        String input = sc.nextLine().trim();

        try {
            int orderId = Integer.parseInt(input);
            String status = service.viewOrderStatus(orderId);
            System.out.println("  Order Status: " + status);
        } catch (NumberFormatException e) {
            System.out.println("  Invalid order ID.");
        }
    }

    // ------------------------------------------------------------------ //
    //  METHODS FOR RESTAURANT FUNCTIONALITY
    // ------------------------------------------------------------------ //

    private static void makeRestaurantReservation() {
        System.out.println();
        printDivider("MAKE RESERVATION");

        System.out.print("  Guest name: ");
        String guestName = sc.nextLine().trim();

        System.out.print("  Party size: ");
        int partySize;
        try {
            partySize = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  Invalid party size.");
            return;
        }

        System.out.print("  Date (YYYY-MM-DD): ");
        String date = sc.nextLine().trim();

        System.out.print("  Time (HH:MM): ");
        String time = sc.nextLine().trim();

        Reservation reservation = restaurant.makeReservation(guestName, partySize, date, time);

        if (reservation == null) {
            System.out.println("  Reservation could not be created.");
            return;
        }

        restaurant.saveReservationsToCSV(RESERVATION_FILE);

        System.out.println("  Reservation created successfully.");
        System.out.println("  Guest: " + reservation.getGuestName());
        System.out.println("  Table: " + reservation.getTableNumber());
        System.out.println("  Date: " + reservation.getDate());
        System.out.println("  Time: " + reservation.getTime());
    }

    private static void viewRestaurantReservation() {
        System.out.print("  Enter guest name: ");
        String guestName = sc.nextLine().trim();

        Reservation reservation = restaurant.viewReservation(guestName);

        if (reservation == null) {
            System.out.println("  Reservation not found.");
            return;
        }

        System.out.println("  Guest: " + reservation.getGuestName());
        System.out.println("  Party Size: " + reservation.getPartySize());
        System.out.println("  Date: " + reservation.getDate());
        System.out.println("  Time: " + reservation.getTime());
        System.out.println("  Table: " + reservation.getTableNumber());
    }

    private static void cancelRestaurantReservation() {
        System.out.print("  Guest name: ");
        String guestName = sc.nextLine().trim();

        System.out.print("  Date (YYYY-MM-DD): ");
        String date = sc.nextLine().trim();

        System.out.print("  Time (HH:MM): ");
        String time = sc.nextLine().trim();

        boolean cancelled = restaurant.cancelReservation(guestName, date, time);

        if (cancelled) {
            restaurant.saveReservationsToCSV(RESERVATION_FILE);
            System.out.println("  Reservation cancelled.");
        } else {
            System.out.println("  Reservation not found.");
        }
    }

    // ------------------------------------------------------------------ //
    //  METHODS FOR INVENTORY MANAGEMENT
    // ------------------------------------------------------------------ //

    private static void updateInventoryItems(FoodService service, DiningInventory inventory) {
        System.out.println();
        printDivider("UPDATE INVENTORY");

        viewInventory(inventory);

        System.out.print("  Enter item name to update: ");
        String itemName = sc.nextLine().trim();

        boolean newItem = false;

        if (!inventory.containsItem(itemName)) {
            System.out.println("  Item not found in inventory.");
            System.out.print("  Would you like to add it? (y/n): ");
            String addChoice = sc.nextLine().trim().toLowerCase();

            if (!addChoice.equals("y")) {
                System.out.println("  Inventory update cancelled.");
                return;
            }

            newItem = true;
        }

        System.out.print("  Enter quantity to add, or press Enter to add default stock of 20: ");
        String input = sc.nextLine().trim();

        int amountToAdd;

        if (input.isEmpty()) {
            amountToAdd = 20;
        } else {
            try {
                amountToAdd = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid quantity. Please enter a valid whole number.");
                return;
            }
        }

        if (amountToAdd <= 0) {
            System.out.println("  Invalid quantity. Quantity must be greater than zero.");
            return;
        }

        if (newItem) {
            System.out.print("  Enter item description: ");
            String description = sc.nextLine().trim();

            System.out.print("  Enter item price: ");
            double price;

            try {
                price = Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid price.");
                return;
            }

            if (price < 0) {
                System.out.println("  Price cannot be negative.");
                return;
            }

            MenuItem newMenuItem = new MenuItem(
                    service.getServiceType(),
                    itemName,
                    description,
                    price
            );

            service.addMenuItem(newMenuItem);
            service.saveMenuToCSV(MENU_FILE);
        }

        inventory.addQuantity(itemName, amountToAdd);
        inventory.saveStockToCSV(INVENTORY_FILE);

        System.out.println("  Inventory item updated successfully.");
        System.out.println("  " + itemName + " quantity is now " + inventory.getQuantity(itemName) + ".");
    }

    // ------------------------------------------------------------------ //
    //  HELPERS TO PRINT OUT MENU FOR DINING
    // ------------------------------------------------------------------ //

    private static void printDiningMenu() {
        System.out.println();
        printDivider("DINING MANAGEMENT");
        System.out.println("  [1] Concessions");
        System.out.println("  [2] Restaurant");
        System.out.println("  [0] Back");
        printDivider("");
        System.out.print("  Choice: ");
    }

    private static void printConcessionsMenu() {
        System.out.println();
        printDivider("CONCESSIONS MANAGEMENT");
        System.out.println("  [1] View Concessions Menu");
        System.out.println("  [2] Open Concessions Order");
        System.out.println("  [3] View Order Status");
        System.out.println("  [4] View Inventory");
        System.out.println("  [5] Add/Update Inventory Item");
        System.out.println("  [0] Back");
        printDivider("");
        System.out.print("  Choice: ");
    }

    private static void printRestaurantMenu() {
        System.out.println();
        printDivider("RESTAURANT MANAGEMENT");
        System.out.println("  [1] View Restaurant Menu");
        System.out.println("  [2] Open Restaurant Order");
        System.out.println("  [3] View Order Status");
        System.out.println("  [4] Make Reservation");
        System.out.println("  [5] View Reservation");
        System.out.println("  [6] Cancel Reservation");
        System.out.println("  [7] View Inventory");
        System.out.println("  [8] Add/Update Inventory Item");
        System.out.println("  [0] Back");
        printDivider("");
        System.out.print("  Choice: ");
    }

    private static void viewInventory(DiningInventory inventory) {
        System.out.println();
        printDivider("CURRENT INVENTORY");

        if (inventory.getStock().isEmpty()) {
            System.out.println("No inventory items found.");
            return;
        }

        for (Map.Entry<String, Integer> entry : inventory.getStock().entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
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

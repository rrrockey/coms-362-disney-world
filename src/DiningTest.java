import dining.*;
import java.util.Arrays;
import java.util.List;

public class DiningTest {
    public static void main(String[] args) {
        System.out.println("--- Testing Dining Inventory ---");
        DiningInventory inventory = new DiningInventory(ServiceType.RESTAURANT);
        inventory.addQuantity("Steak", 10);
        inventory.addQuantity("Wine", 20);
        System.out.println("Steak quantity: " + inventory.getQuantity("Steak"));

        System.out.println("\n--- Testing Restaurant & Orders ---");
        Restaurant restaurant = new Restaurant(inventory);
        restaurant.addMenuItem(new MenuItem(ServiceType.RESTAURANT, "Steak", "Juicy sirloin", 25.0));
        restaurant.addMenuItem(new MenuItem(ServiceType.RESTAURANT, "Wine", "Red blend", 10.0));

        Order order = restaurant.placeOrder(Arrays.asList("Steak", "Wine"));
        if (order != null && order.getTotal() == 35.0) {
            System.out.println("SUCCESS: Order placed. Total: $" + order.getTotal());
            System.out.println("Remaining Steak: " + inventory.getQuantity("Steak"));
        } else {
            System.out.println("FAILURE: Order placement failed.");
        }

        System.out.println("\n--- Testing Reservations ---");
        Reservation res = restaurant.makeReservation("Alice", 4, "2026-05-10", "19:00");
        if (res != null) {
            System.out.println("SUCCESS: Reservation made for " + res.getGuestName() + " at table " + res.getTableNumber());
        } else {
            System.out.println("FAILURE: Reservation failed (check if tables are initialized).");
        }

        boolean cancelled = restaurant.cancelReservation("Alice", "2026-05-10", "19:00");
        if (cancelled) {
            System.out.println("SUCCESS: Reservation cancelled.");
        } else {
            System.out.println("FAILURE: Could not cancel reservation.");
        }
    }
}

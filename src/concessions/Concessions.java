package concessions;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a concessions stand from which a guest will order food / drink.
 *
 * @author cole-giles
 */
public class Concessions {

    private List<MenuItem> menu;
    private List<Order> orders;
    private ConcessionsInventory inventory;

    public Concessions(ConcessionsInventory inventory) {
        this.inventory = inventory;
        this.menu =  new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    // Returns the data from a menu item
    public MenuItem viewItemDetails(String itemName) {
        for (MenuItem item : menu) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null; // item not found
    }

    // Take a food order from a guest and return the order to be queued.
    public Order placeOrder(List<String> itemNames) {
        Order order = new Order();

        for (String name : itemNames) {
            if (inventory.checkItemAvailability(name)) {
                MenuItem item = viewItemDetails(name);
                order.addItem(item);
            }
            else {
                System.out.println(name + " is out of stock.");
            }
        }

        order.calculateTotal();
        orders.add(order);

        // deduct items from inventory
        inventory.deductItems(order);

        order.setStatus("In Progress");

        return order;
    }
}

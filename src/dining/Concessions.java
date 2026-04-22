package dining;

import java.util.List;

/**
 * Represents a concessions stand from which a guest will order food / drink.
 *
 * @author cole-giles
 */
public class Concessions extends FoodService {

    public Concessions(FoodInventory inventory) {
        super(ServiceType.CONCESSIONS, inventory);
    }

    // Take a food order from a guest and return the order to be queued.
    @Override
    public Order placeOrder(List<String> itemNames) {
        Order order = new Order(serviceType);

        for (String name : itemNames) {
            if (inventory.checkItemAvailability(name)) {
                MenuItem item = viewItemDetails(name);

                if (item != null) {
                    order.addItem(item);
                } else {
                    System.out.println(name + " is not on the menu.");
                }
            } else {
                System.out.println(name + " is out of stock.");
            }
        }

        if (order.getItems().isEmpty()) {
            return null;
        }

        order.calculateTotal();
        order.setStatus("In Progress");
        orders.add(order);

        inventory.deductItems(order);

        return order;
    }

}
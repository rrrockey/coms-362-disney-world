package dining.strategy;

import dining.DiningInventory;
import dining.FoodService;
import dining.MenuItem;

public class AddNewMenuItemStrategy implements InventoryUpdateStrategy {

    @Override
    public void update (
            FoodService service,
            DiningInventory inventory,
            String itemName,
            int quantity,
            String description,
            double price
    ) {
        MenuItem newItem = new MenuItem(service.getServiceType(), itemName, description, price);

        service.addMenuItem(newItem);
        inventory.addQuantity(itemName, quantity);
    }
}

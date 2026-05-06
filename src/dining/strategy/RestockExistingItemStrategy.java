package dining.strategy;

import dining.DiningInventory;
import dining.FoodService;

public class RestockExistingItemStrategy implements InventoryUpdateStrategy {

    @Override
    public void update (
            FoodService service,
            DiningInventory inventory,
            String itemName,
            int quantity,
            String description,
            double price
    ) {
        inventory.addQuantity(itemName, quantity);
    }
}

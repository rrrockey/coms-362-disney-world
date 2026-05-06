package dining.strategy;

import dining.DiningInventory;
import dining.FoodService;

public interface InventoryUpdateStrategy {

    void update (
            FoodService service,
            DiningInventory inventory,
            String itemName,
            int quantity,
            String description,
            double price
    );
}

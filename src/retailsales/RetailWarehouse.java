package retailsales;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the bulk inventory for retail warehouses at Disney World.
 */
public class RetailWarehouse {
    private Map<String, Integer> inventory = new HashMap<>();

    /**
     * Receives a bulk shipment into the warehouse.
     * @param itemName Name of the item
     * @param quantity Quantity received
     */
    public void receiveShipment(String itemName, int quantity) {
        receiveShipment(itemName, quantity, false);
    }

    public void receiveShipment(String itemName, int quantity, boolean silent) {
        inventory.put(itemName, inventory.getOrDefault(itemName, 0) + quantity);
        if (!silent) {
            System.out.println("Warehouse received shipment: " + quantity + " units of " + itemName + ".");
        }
    }

    /**
     * Fulfills a restock request for a retail shop item.
     * @param shopItem The item in the retail shop to restock
     * @param requestedQuantity The amount requested
     * @return The actual amount transferred
     */
    public int fulfillRestockRequest(RetailItem shopItem, int requestedQuantity) {
        int available = inventory.getOrDefault(shopItem.name, 0);
        
        if (available == 0) {
            System.out.println("Warehouse out of stock for " + shopItem.name + ". Request terminated.");
            return 0;
        }

        int toTransfer = Math.min(requestedQuantity, available);
        
        // Ensure we don't exceed shop capacity
        int spaceInShop = shopItem.capacity - shopItem.stock;
        toTransfer = Math.min(toTransfer, spaceInShop);

        if (toTransfer <= 0) {
            System.out.println("Shop already at capacity for " + shopItem.name + ".");
            return 0;
        }

        inventory.put(shopItem.name, available - toTransfer);
        shopItem.stock += toTransfer;

        if (toTransfer < requestedQuantity && available < requestedQuantity) {
            System.out.println("Warehouse had partial stock. Transferred " + toTransfer + " units of " + shopItem.name + ".");
        } else {
            System.out.println("Transferred " + toTransfer + " units of " + shopItem.name + " from warehouse to shop.");
        }

        return toTransfer;
    }

    /**
     * Returns the current warehouse inventory.
     * @return Map of item names to quantities
     */
    public Map<String, Integer> getInventory() {
        return new HashMap<>(inventory);
    }
}

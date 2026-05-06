package employee;

import guest.Guest;
import retailsales.RetailItem;
import retailsales.RetailWarehouse;

public class RetailSalesEmployee extends Employee {
    public String name = "Default Employee";

    /**
     * Verifies whether the given guest holds a valid membership.
     *
     * @param guest the guest to verify
     * @return true if the guest is a member, false otherwise
     */
    public boolean verifyMembership(Guest guest) {
        if (guest.isMember) {
            System.out.println("Membership verified for guest: " + guest.name);
            return true;
        } else {
            System.out.println("No valid membership found for guest: " + guest.name);
            return false;
        }
    }

    public void restockItem(RetailItem item, int quantity) {
        if (item.stock + quantity <= item.capacity) {
            item.stock += quantity;
            System.out.println("Restocked " + quantity + " items. New stock: " + item.stock);
        } else {
            int canStock = item.capacity - item.stock;
            int leftover = quantity - canStock;
            item.stock = item.capacity;
            System.out.println("Restocked " + canStock + " items. Display full. Leftover: " + leftover);
        }
    }

    /**
     * Requests a restock from the central warehouse.
     * @param warehouse The warehouse to request from
     * @param item The item to restock
     * @param quantity The requested quantity
     */
    public void requestWarehouseRestock(RetailWarehouse warehouse, RetailItem item, int quantity) {
        System.out.println("Employee " + this.name + " requesting " + quantity + " units of " + item.name + " from warehouse.");
        warehouse.fulfillRestockRequest(item, quantity);
    }
}

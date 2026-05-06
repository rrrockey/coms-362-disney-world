import guest.Guest;
import retailsales.*;
import employee.RetailSalesEmployee;

public class WarehouseTest {
    public static void main(String[] args) {
        RetailWarehouse warehouse = new RetailWarehouse();
        RetailItem mickeyEars = new RetailItem("Mickey Ears", 25.0, 5, 20);
        RetailSalesEmployee employee = new RetailSalesEmployee();
        employee.name = "John Warehouse";

        System.out.println("--- Testing Warehouse Shipment ---");
        warehouse.receiveShipment("Mickey Ears", 50);
        System.out.println("Warehouse Inventory: " + warehouse.getInventory());

        System.out.println("\n--- Testing Shop Restock from Warehouse ---");
        System.out.println("Initial Shop Stock: " + mickeyEars.stock);
        employee.requestWarehouseRestock(warehouse, mickeyEars, 10);
        System.out.println("New Shop Stock: " + mickeyEars.stock);
        System.out.println("Remaining Warehouse Stock: " + warehouse.getInventory().get("Mickey Ears"));

        System.out.println("\n--- Testing Partial Restock (Insufficient Warehouse Stock) ---");
        warehouse.receiveShipment("Disney Pin", 5);
        RetailItem pins = new RetailItem("Disney Pin", 12.5, 0, 100);
        employee.requestWarehouseRestock(warehouse, pins, 10);
        System.out.println("Shop Pin Stock: " + pins.stock);
        System.out.println("Warehouse Pin Stock: " + warehouse.getInventory().getOrDefault("Disney Pin", 0));

        System.out.println("\n--- Testing Shop Capacity Limit ---");
        warehouse.receiveShipment("Mickey Ears", 100);
        System.out.println("Mickey Ears Shop Stock before: " + mickeyEars.stock + "/" + mickeyEars.capacity);
        employee.requestWarehouseRestock(warehouse, mickeyEars, 50); // Should only take up to capacity
        System.out.println("Mickey Ears Shop Stock after: " + mickeyEars.stock + "/" + mickeyEars.capacity);

        System.out.println("\n--- Testing Out of Stock Warehouse ---");
        RetailItem magicBand = new RetailItem("MagicBand+", 45.0, 0, 10);
        employee.requestWarehouseRestock(warehouse, magicBand, 5);
    }
}

package employee;

import retailsales.RetailItem;

public class RetailSalesEmployee extends Employee {
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
}

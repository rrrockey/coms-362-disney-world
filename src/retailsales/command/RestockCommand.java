package retailsales.command;

import employee.RetailSalesEmployee;
import retailsales.RetailItem;

/**
 * Concrete command that wraps an employee restock action.
 * Receiver: {@link RetailSalesEmployee#restockItem(RetailItem, int)}
 */
public class RestockCommand implements RetailCommand {

    private final RetailSalesEmployee employee;
    private final RetailItem item;
    private final int quantity;

    public RestockCommand(RetailSalesEmployee employee,
                          RetailItem item,
                          int quantity) {
        this.employee = employee;
        this.item     = item;
        this.quantity = quantity;
    }

    @Override
    public void execute() {
        employee.restockItem(item, quantity);
    }
}

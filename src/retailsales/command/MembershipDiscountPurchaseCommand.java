package retailsales.command;

import employee.RetailSalesEmployee;
import guest.Guest;
import retailsales.RetailItem;
import retailsales.RetailSalesRegister;

/**
 * Concrete command that wraps a membership-discounted purchase.
 * Receiver: {@link RetailSalesRegister#processMembershipDiscountPurchase(
 *              RetailSalesEmployee, Guest, RetailItem, String)}
 */
public class MembershipDiscountPurchaseCommand implements RetailCommand {

    private final RetailSalesRegister register;
    private final RetailSalesEmployee employee;
    private final Guest guest;
    private final RetailItem item;
    private final String paymentType;

    public MembershipDiscountPurchaseCommand(RetailSalesRegister register,
                                             RetailSalesEmployee employee,
                                             Guest guest,
                                             RetailItem item,
                                             String paymentType) {
        this.register    = register;
        this.employee    = employee;
        this.guest       = guest;
        this.item        = item;
        this.paymentType = paymentType;
    }

    @Override
    public void execute() {
        register.processMembershipDiscountPurchase(employee, guest, item, paymentType);
    }
}

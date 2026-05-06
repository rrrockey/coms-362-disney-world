package retailsales.command;

import guest.Guest;
import retailsales.RetailItem;
import retailsales.RetailSalesRegister;

/**
 * Concrete command that wraps a standard (non-discounted) purchase.
 * Receiver: {@link RetailSalesRegister#processPurchase(Guest, RetailItem, String)}
 */
public class PurchaseCommand implements RetailCommand {

    private final RetailSalesRegister register;
    private final Guest guest;
    private final RetailItem item;
    private final String paymentType;

    public PurchaseCommand(RetailSalesRegister register,
                           Guest guest,
                           RetailItem item,
                           String paymentType) {
        this.register    = register;
        this.guest       = guest;
        this.item        = item;
        this.paymentType = paymentType;
    }

    @Override
    public void execute() {
        register.processPurchase(guest, item, paymentType);
    }
}

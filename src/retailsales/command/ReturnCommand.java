package retailsales.command;

import guest.Guest;
import retailsales.RetailSalesRegister;
import retailsales.Transaction;

/**
 * Concrete command that wraps a merchandise return / refund.
 * Receiver: {@link RetailSalesRegister#processReturn(Guest, Transaction, String)}
 */
public class ReturnCommand implements RetailCommand {

    private final RetailSalesRegister register;
    private final Guest guest;
    private final Transaction transaction;
    private final String refundType;

    public ReturnCommand(RetailSalesRegister register,
                         Guest guest,
                         Transaction transaction,
                         String refundType) {
        this.register    = register;
        this.guest       = guest;
        this.transaction = transaction;
        this.refundType  = refundType;
    }

    @Override
    public void execute() {
        register.processReturn(guest, transaction, refundType);
    }
}

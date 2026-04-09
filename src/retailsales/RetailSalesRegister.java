package retailsales;

import guest.Guest;
import java.util.ArrayList;
import java.util.List;

public class RetailSalesRegister {
    private List<Transaction> salesLedger = new ArrayList<>();

    public void processPurchase(Guest guest, RetailItem item, String paymentType) {
        if (item.stock <= 0) {
            System.out.println("Purchase failed: " + item.name + " is out of stock.");
            return;
        }

        double price = item.price;
        if (paymentType.equalsIgnoreCase("Cash")) {
            if (guest.money >= price) {
                guest.money -= price;
            } else {
                System.out.println("Purchase failed: Insufficient cash.");
                return;
            }
        } else if (paymentType.equalsIgnoreCase("Gift Credit")) {
            if (guest.giftCredit >= price) {
                guest.giftCredit -= price;
            } else {
                System.out.println("Purchase failed: Insufficient gift credit.");
                return;
            }
        } else {
            System.out.println("Purchase failed: Invalid payment type.");
            return;
        }

        item.stock--;
        Transaction transaction = new Transaction(item, price, paymentType);
        salesLedger.add(transaction);
        System.out.println("Purchase successful: " + transaction);
    }

    public void processReturn(Guest guest, Transaction transaction, String refundType) {
        if (!salesLedger.contains(transaction)) {
            System.out.println("Return failed: Transaction not found in ledger.");
            return;
        }

        if (refundType.equalsIgnoreCase("Cash")) {
            guest.money += transaction.amount;
        } else if (refundType.equalsIgnoreCase("Gift Card Credit")) {
            guest.giftCredit += transaction.amount;
        } else {
            System.out.println("Return failed: Invalid refund type.");
            return;
        }

        transaction.item.stock++;
        salesLedger.remove(transaction);
        System.out.println("Return successful. Refund issued as " + refundType + ".");
    }

    public List<Transaction> getSalesLedger() {
        return salesLedger;
    }
}

package retailsales;

import guest.Guest;
import employee.RetailSalesEmployee;
import retailsales.command.RetailCommand;
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

    /**
     * @param employee    the retail sales employee at the register
     * @param guest       the customer
     * @param item        the item being purchased
     * @param paymentType "Cash" or "Gift Credit"
     */
    public void processMembershipDiscountPurchase(RetailSalesEmployee employee,
                                                  Guest guest,
                                                  RetailItem item,
                                                  String paymentType) {
        // Employee verifies membership
        if (!employee.verifyMembership(guest)) {
            System.out.println("Membership discount purchase failed: guest does not have a valid membership.");
            return;
        }

        // Out of stock check
        if (item.stock <= 0) {
            System.out.println("Membership discount purchase failed: " + item.name + " is out of stock.");
            return;
        }

        // Calculate discounted price
        double discountedPrice = item.price * (1.0 - Guest.MEMBERSHIP_DISCOUNT);
        System.out.printf("Membership discount applied (%.0f%% off): $%.2f -> $%.2f%n",
                Guest.MEMBERSHIP_DISCOUNT * 100, item.price, discountedPrice);

        // Payment
        if (paymentType.equalsIgnoreCase("Cash")) {
            // customer pays with cash
            if (guest.money < discountedPrice) {
                // payment declined
                System.out.println("Transaction terminated: insufficient cash. Required: $"
                        + String.format("%.2f", discountedPrice) + ", available: $"
                        + String.format("%.2f", guest.money));
                return;
            }
            guest.money -= discountedPrice;

        } else if (paymentType.equalsIgnoreCase("Gift Credit")) {
            // customer uses gift credit instead of cash
            if (guest.giftCredit < discountedPrice) {
                // payment declined
                System.out.println("Transaction terminated: insufficient gift credit. Required: $"
                        + String.format("%.2f", discountedPrice) + ", available: $"
                        + String.format("%.2f", guest.giftCredit));
                return;
            }
            guest.giftCredit -= discountedPrice;

        } else {
            System.out.println("Membership discount purchase failed: invalid payment type '" + paymentType + "'.");
            return;
        }

        // Record transaction in sales ledger and print receipt
        item.stock--;
        Transaction transaction = new Transaction(item, discountedPrice, paymentType);
        salesLedger.add(transaction);
        System.out.println("Receipt: " + transaction);
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

    /**
     * Invoker method: executes any {@link RetailCommand}.
     * Decouples the caller from the specific operation being performed.
     *
     * @param cmd the command to execute
     */
    public void executeCommand(RetailCommand cmd) {
        cmd.execute();
    }
}

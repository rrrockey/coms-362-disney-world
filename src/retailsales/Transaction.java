package retailsales;

public class Transaction {
    public RetailItem item;
    public double amount;
    public String paymentType; // "Cash" or "Gift Credit"

    public Transaction(RetailItem item, double amount, String paymentType) {
        this.item = item;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    @Override
    public String toString() {
        return String.format("Transaction{item='%s', amount=%.2f, paymentType='%s'}",
                item.name, amount, paymentType);
    }
}

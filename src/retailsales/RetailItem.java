package retailsales;

public class RetailItem {
    public String name;
    public double price;
    public int stock;
    public int capacity;

    public RetailItem(String name, double price, int stock, int capacity) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.capacity = capacity;
    }
}

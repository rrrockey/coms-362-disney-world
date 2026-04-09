package concessions;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a guest's order for a concessions stand.
 *
 * @author cole-giles
 */
public class Order {
    private static int idCounter = 1;

    private int orderId;
    private List<MenuItem> items;
    private double total;
    private String status;

    public Order() {
        this.orderId = idCounter++;
        this.items = new ArrayList<>();
        this.status = "Received";
        this.total = 0;
    }

    public void addItem(MenuItem item){
        items.add(item);
    }

    public void calculateTotal(){
        total = 0;
        for(MenuItem item : items){
            total += item.getPrice();
        }
    }

    public int getOrderId() {return orderId;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
    public List<MenuItem> getItems() {return items;}
}

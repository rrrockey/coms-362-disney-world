package dining;

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
    private ServiceType serviceType;
    private List<MenuItem> items;
    private double total;
    private String status;

    public Order(ServiceType serviceType) {
        this.orderId = idCounter++;
        this.serviceType = serviceType;
        this.items = new ArrayList<>();
        this.status = "Received";
        this.total = 0.0;
    }

    public Order(int orderId, ServiceType serviceType, List<MenuItem> items, double total, String status) {
        this.orderId = orderId;
        this.serviceType = serviceType;
        this.items = items;
        this.total = total;
        this.status = status;

        if (orderId >= idCounter) {
            idCounter = orderId + 1;
        }
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

    public ServiceType getServiceType() {return serviceType;}
    public int getOrderId() {return orderId;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
    public List<MenuItem> getItems() {return items;}
    public double getTotal() {return total;}
}

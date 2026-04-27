package dining;

/**
 * Represents a singular menu item for a concessions stand.
 *
 * @author cole-giles
 */
public class MenuItem {
    private ServiceType serviceType;
    private String name;
    private String description;
    private double price;

    public MenuItem(ServiceType serviceType, String name, String description, double price) {
        this.serviceType = serviceType;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public ServiceType getServiceType() {return serviceType;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public double getPrice() {return price;}

}

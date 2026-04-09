package concessions;

/**
 * Represents a singular menu item for a concessions stand.
 *
 * @author cole-giles
 */
public class MenuItem {
    private String name;
    private String description;
    private double price;
    private boolean availability;

    public MenuItem(String name, String description, double price, boolean availability) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.availability = availability;
    }

    public String getName() {return name;}
    public String getDescription() {return description;}
    public double getPrice() {return price;}
}

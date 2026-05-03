package dining;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DiningInventory {

    private ServiceType serviceType;
    private Map<String, Integer> stock;

    // Initialize Inventory given the service it is being used for (i.e. concessions/restaurant)
    public DiningInventory(ServiceType serviceType) {
        this.serviceType = serviceType;
        stock = new HashMap<>();
    }

    // Check if a Menu Item exists and is in-stock
    public boolean checkItemAvailability(String itemName) {
        return stock.containsKey(itemName) && stock.get(itemName) > 0;
    }

    // Check if an item exists in the inventory by name
    public boolean containsItem(String itemName) {
        return stock.containsKey(itemName);
    }

    // Return the current quantity of an item of a given name
    public int getQuantity(String itemName) {
        return stock.getOrDefault(itemName, 0);
    }

    // Add an amount to the quantity of a chosen item, used for restock orders
    public void addQuantity(String itemName, int amt) {
        if (amt <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return;
        }

        int currentQty = stock.getOrDefault(itemName, 0);
        stock.put(itemName, currentQty + amt);
    }

    // Set the quantity of a specific item to a given amount
    public void setQuantity(String itemName, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be >= 0.");
            return;
        }

        stock.put(itemName, quantity);
    }

    public Map<String, Integer> getStock() {
        return stock;
    }

    // Decrement an item from the inventory when ordered from concessions
    public void deductItems(Order order) {
        for (MenuItem item : order.getItems()) {
            String name = item.getName();
            int currentQty = stock.getOrDefault(name, 0);

            if (currentQty > 0) {
                stock.put(name, currentQty - 1);
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  HELPERS TO READ/WRITE TO A CSV FILE
    // ------------------------------------------------------------------ //

    public void loadStockFromCSV(String fileName) {
        stock.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    ServiceType rowServiceType = ServiceType.valueOf(parts[0].trim().toUpperCase());
                    String itemName = parts[1].trim();
                    int quantity = Integer.parseInt(parts[2].trim());
                    if (rowServiceType == this.serviceType) {
                        stock.put(itemName, quantity);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading inventory from CSV: " + e.getMessage());
        }
    }

    public void saveStockToCSV(String fileName) {
        Map<String, String> otherRows = new HashMap<>();

        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        ServiceType serviceType = ServiceType.valueOf(parts[0].trim().toUpperCase());
                        String itemName = parts[1].trim();

                        if (serviceType != this.serviceType) {
                            otherRows.put(itemName, line);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading inventory from CSV: " + e.getMessage());
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("serviceType,itemName,quantity");

            for (String row : otherRows.values()) {
                pw.println(row);
            }

            for (Map.Entry<String, Integer> entry : stock.entrySet()) {
                pw.println(serviceType + "," + entry.getKey() + "," + entry.getValue());
            }
        } catch  (IOException e) {
            System.out.println("Error writing inventory to CSV: " + e.getMessage());
        }
    }
}

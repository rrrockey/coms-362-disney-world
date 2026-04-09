package concessions;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConcessionsInventory {

    private Map<String, Integer> stock;

    public ConcessionsInventory() {
        stock = new HashMap<>();
    }

    // Check if a Menu Item exists and is in-stock
    public boolean checkItemAvailability(String itemName) {
        return stock.containsKey(itemName) && stock.get(itemName) > 0;
    }

    // Decrement an item from the inventory when ordered from concessions
    public void deductItems(Order order) {
        for (MenuItem item : order.getItems()) {
            String name = item.getName();
            stock.put(name, stock.get(name) - 1);
        }
    }

    // ------------------------------------------------------------------ //
    //  HELPERS TO READ/WRITE TO A CSV FILE
    // ------------------------------------------------------------------ //

    public void loadStockFromCSV(String fileName) {
        stock.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String itemName = parts[0].trim();
                    int quantity = Integer.parseInt(parts[1].trim());
                    stock.put(itemName, quantity);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading inventory from CSV: " + fileName);
        }
    }

    public void saveStockToCSV(String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("itemName,quantity");

            for (Map.Entry<String, Integer> entry : stock.entrySet()) {
                pw.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Error saving inventory to CSV: " + e.getMessage());
        }
    }


}

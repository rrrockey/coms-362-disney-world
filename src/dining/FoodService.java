package dining;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class FoodService implements OrderService {

    protected ServiceType serviceType;
    protected List<MenuItem> menu;
    protected List<Order> orders;
    protected DiningInventory inventory;

    public FoodService(ServiceType serviceType, DiningInventory inventory) {
        this.serviceType = serviceType;
        this.inventory = inventory;
        this.menu = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public List<MenuItem> getMenu() {
        return menu;
    }

    public void addMenuItem(MenuItem menuItem) {
        menu.add(menuItem);
    }

    public List<Order> getOrders() {
        return orders;
    }

    // Returns the data from a menu item
    @Override
    public MenuItem viewItemDetails(String itemName) {
        for (MenuItem item : menu) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null; // item not found
    }

    //View the status of the order, or return that the order cannot be found
    @Override
    public String viewOrderStatus(int orderId) {
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                return order.getStatus();
            }
        }
        return "Order not found";
    }

    public abstract Order placeOrder(List<String> itemNames);

    // ------------------------------------------------------------------ //
    //  HELPERS TO READ/WRITE TO A CSV FILE
    // ------------------------------------------------------------------ //
    public void loadMenuFromCSV(String fileName) {
        menu.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    ServiceType rowServiceType = ServiceType.valueOf(parts[0].trim().toUpperCase());
                    String name = parts[1].trim();
                    String description = parts[2].trim();
                    double price = Double.parseDouble(parts[3].trim());

                    if (rowServiceType == this.serviceType) {
                        menu.add(new MenuItem(rowServiceType, name, description, price));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading menu from CSV: " + e.getMessage());
        }
    }

    public void saveMenuToCSV(String fileName) {
        List<String> otherRows = new ArrayList<>();
        File file = new File(fileName);

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine(); // skip header

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        ServiceType rowServiceType = ServiceType.valueOf(parts[0].trim().toUpperCase());
                        if (rowServiceType != this.serviceType) {
                            otherRows.add(line);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading existing menu CSV: " + e.getMessage());
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("serviceType,name,description,price");

            for (String row : otherRows) {
                pw.println(row);
            }

            for (MenuItem item : menu) {
                pw.println(item.getServiceType() + "," +
                        item.getName() + "," +
                        item.getDescription() + "," +
                        item.getPrice());
            }
        } catch (IOException e) {
            System.out.println("Error saving menu to CSV: " + e.getMessage());
        }
    }

    public void loadOrdersFromCSV(String fileName) {
        orders.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    ServiceType rowServiceType = ServiceType.valueOf(parts[0].trim().toUpperCase());
                    int orderId = Integer.parseInt(parts[1].trim());
                    String[] itemNames = parts[2].trim().split("\\|");
                    double total = Double.parseDouble(parts[3].trim());
                    String status = parts[4].trim();

                    if (rowServiceType == this.serviceType) {
                        List<MenuItem> orderItems = new ArrayList<>();

                        for (String itemName : itemNames) {
                            MenuItem item = viewItemDetails(itemName.trim());
                            if (item != null) {
                                orderItems.add(item);
                            }
                        }

                        orders.add(new Order(orderId, rowServiceType, orderItems, total, status));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading orders from CSV: " + e.getMessage());
        }
    }

    public void saveOrdersToCSV(String fileName) {
        List<String> otherRows = new ArrayList<>();
        File file = new File(fileName);

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine(); // skip header

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        ServiceType rowServiceType = ServiceType.valueOf(parts[0].trim().toUpperCase());
                        if (rowServiceType != this.serviceType) {
                            otherRows.add(line);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading existing orders CSV: " + e.getMessage());
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("serviceType,orderId,items,total,status");

            for (String row : otherRows) {
                pw.println(row);
            }

            for (Order order : orders) {
                StringBuilder itemList = new StringBuilder();

                for (int i = 0; i < order.getItems().size(); i++) {
                    itemList.append(order.getItems().get(i).getName());
                    if (i < order.getItems().size() - 1) {
                        itemList.append("|");
                    }
                }

                pw.println(order.getServiceType() + "," +
                        order.getOrderId() + "," +
                        itemList + "," +
                        order.getTotal() + "," +
                        order.getStatus());
            }
        } catch (IOException e) {
            System.out.println("Error saving orders to CSV: " + e.getMessage());
        }
    }
}
package dining;

import java.util.List;

public interface OrderService {

    void loadMenuFromCSV(String fileName);
    void saveMenuToCSV(String fileName);

    void loadOrdersFromCSV(String fileName);
    void saveOrdersToCSV(String fileName);

    MenuItem viewItemDetails(String itemName);
    String viewOrderStatus(int orderId);
    Order placeOrder(List<String> itemNames);
}

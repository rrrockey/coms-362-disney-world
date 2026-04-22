package dining;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Restaurant extends FoodService{

    private List<Reservation> reservations;
    private List<Table> tables;

    public Restaurant(FoodInventory inventory) {
        super(ServiceType.RESTAURANT, inventory);
        tables = new ArrayList<>();
        this.reservations = new ArrayList<>();
    }

    // Create a reservation given a guest's name, a size for the party, and a date and time
    public Reservation makeReservation (String guestName, int partySize, String date, String time, int tableNum) {

        //Basic validation to check for no guest name or invalid party size
        if (guestName == null || guestName.isBlank()) {
            System.out.println("Guest name is required.");
            return null;
        }

        if (partySize <= 0) {
            System.out.println("Party size must be greater than 0.");
            return null;
        }

        Table table = findAvailableTable(partySize, date, time);

        if (table == null) {
            System.out.println("No tables available for given time.");
            return null;
        }

        Reservation reservation = new Reservation(guestName, partySize, date, time, table.getTableNumber());

        reservations.add(reservation);

        System.out.println("Reservation for " + guestName + " on " + date + " at " + time + " has been confirmed.");

        return reservation;
    }

    // Cancel a reservation if it exists
    public boolean cancelReservation(String guestName, String date, String time) {

        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);

            if (r.getGuestName().equalsIgnoreCase(guestName) &&
                    r.getDate().equals(date) &&
                    r.getTime().equals(time)) {

                reservations.remove(i);
                System.out.println("Reservation cancelled.");
                return true;
            }
        }

        System.out.println("Reservation not found.");
        return false;
    }

    // Retrieve a reservation after it has been confirmed or added, or return nothing
    public Reservation viewReservation(String guestName) {
        for (Reservation r : reservations) {
            if (r.getGuestName().equalsIgnoreCase(guestName)) {
                return r;
            }
        }
        return null;
    }

    // Place an order for a table
    @Override
    public Order placeOrder(List<String> itemNames) {
        Order order = new Order(serviceType);

        for (String name : itemNames) {
            if (inventory.checkItemAvailability(name)) {
                MenuItem item = viewItemDetails(name);

                if (item != null) {
                    order.addItem(item);
                } else {
                    System.out.println(name + " is not on the menu.");
                }
            } else {
                System.out.println(name + " is out of stock.");
            }
        }

        if (order.getItems().isEmpty()) {
            return null;
        }

        order.calculateTotal();
        order.setStatus("Open");
        orders.add(order);
        inventory.deductItems(order);

        return order;
    }

    // Helper method to check availability of a reservation at a given date and time
    private Table findAvailableTable(int partySize, String date, String time) {

        for (Table table : tables) {

            // Skip the tables which are too small
            if (table.getCapacity() < partySize) {
                continue;
            }

            boolean isTaken = false;

            for (Reservation r : reservations) {
                if (r.getDate().equals(date) && r.getTime().equals(time) && r.getTableNumber() == table.getTableNumber()) {
                    isTaken = true;
                    break;
                }
            }

            if (!isTaken) {
                return table;
            }
        }

        return null; // No table available
    }

    // ------------------------------------------------------------------ //
    //  HELPERS TO READ/WRITE TO A CSV FILE
    // ------------------------------------------------------------------ //
    public void loadReservationsFromCSV(String fileName) {
        reservations.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 5) {
                    String guestName = parts[0].trim();
                    int partySize = Integer.parseInt(parts[1].trim());
                    String date = parts[2].trim();
                    String time = parts[3].trim();
                    int tableNumber = Integer.parseInt(parts[4].trim());

                    reservations.add(new Reservation(guestName, partySize, date, time, tableNumber));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading reservations from CSV: " + e.getMessage());
        }
    }

    public void saveReservationsToCSV(String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("guestName,partySize,date,time,tableNumber");

            for (Reservation reservation : reservations) {
                pw.println(reservation.getGuestName() + ","
                        + reservation.getPartySize() + ","
                        + reservation.getDate() + ","
                        + reservation.getTime() + ","
                        + reservation.getTableNumber());
            }
        } catch (IOException e) {
            System.out.println("Error saving reservations to CSV: " + e.getMessage());
        }
    }
}

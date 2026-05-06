import rides.*;
import java.util.List;

public class RideTest {
    public static void main(String[] args) {
        try {
            RideRepository.initialise();

            System.out.println("--- Testing Ride Creation ---");
            // Constructor: Ride(int rideId, String rideName, int maxCapacity, boolean isOperational)
            Ride r1 = new Ride(100, "Space Mountain", 30, true);
            RideRepository.saveRide(r1);
            System.out.println("Ride saved: " + r1.rideName);

            System.out.println("\n--- Testing Ride Retrieval ---");
            List<Ride> all = RideRepository.loadAllRides();
            boolean found = all.stream().anyMatch(r -> r.rideName.equals("Space Mountain"));
            if (found) {
                System.out.println("SUCCESS: 'Space Mountain' found in repository.");
            } else {
                System.out.println("FAILURE: Ride not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

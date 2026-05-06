import parkevents.*;
import parkevents.entities.*;
import java.time.LocalDate;
import java.util.List;

public class EventTest {
    public static void main(String[] args) {
        try {
            EventRepository.initialise();

            System.out.println("--- Testing Event Creation ---");
            Event e1 = new Concert("Magic Symphony", "Classical music under the stars", LocalDate.now());
            Event e2 = new Movie("Lion King", "Live action remake", LocalDate.now());
            
            EventRepository.saveEvent(e1);
            EventRepository.saveEvent(e2);
            System.out.println("Events saved.");

            System.out.println("\n--- Testing Event Retrieval ---");
            List<Event> all = EventRepository.getAllEvents();
            System.out.println("Number of events: " + all.size());
            boolean found = all.stream().anyMatch(e -> e.getTitle().equals("Magic Symphony"));
            if (found) {
                System.out.println("SUCCESS: Found 'Magic Symphony' in repository.");
            } else {
                System.out.println("FAILURE: Could not find event.");
            }

            System.out.println("\n--- Testing Event Update ---");
            int index = -1;
            for(int i=0; i<all.size(); i++) {
                if(all.get(i).getTitle().equals("Lion King")) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                EventRepository.updateEvent(index, "Lion King 2", "The sequel", LocalDate.now());
                Event updated = EventRepository.getEvent(index);
                if (updated.getTitle().equals("Lion King 2")) {
                    System.out.println("SUCCESS: Event updated correctly.");
                } else {
                    System.out.println("FAILURE: Update did not persist.");
                }
            }

            // Cleanup
            // EventRepository doesn't have a clear all, so we just leave it for now
            // or we could delete the newly added ones if we had an ID/Index based delete
            // (The repo uses index which is volatile)
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

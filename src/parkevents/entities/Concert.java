package parkevents.entities;

import java.time.LocalDate;
import parkevents.EventType;

public class Concert extends AbstractEvent {
    private String artistName;
    public Concert(String title, String description, LocalDate date) {
        super(title, description, date, EventType.CONCERT);
    }

    public String getArtistName() {
        return artistName;
    }
}
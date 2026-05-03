package parkevents.entities;

import java.time.LocalDate;
import parkevents.EventType;

public class Movie extends AbstractEvent {
    private String genre;
    public Movie(String title, String description, LocalDate date) {
        super(title, description, date, EventType.MOVIE);
    }

    public String getGenre() {
        return genre;
    }
}
package parkevents.entities;

import java.time.LocalDate;

import static parkevents.EventType.MOVIE;

public class Movie extends Event {
    public Movie(String title, String description, LocalDate date) {
        super(title, MOVIE, description, date);
    }
}

package parkevents.entities;

import static parkevents.EventType.MOVIE;

public class Movie extends Event {
    public Movie(String title, String description, String date) {
        super(title, MOVIE, description, date);
    }
}

package parkevents.entities;

import java.time.LocalDate;

import static parkevents.EventType.CONCERT;

public class Concert extends Event {
    public Concert(String title, String description, LocalDate date) {
        super(title, CONCERT, description, date);
    }
}
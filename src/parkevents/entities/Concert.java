package parkevents.entities;

import static parkevents.EventType.CONCERT;

public class Concert extends Event {
    public Concert(String title, String description, String date) {
        super(title, CONCERT, description, date);
    }
}
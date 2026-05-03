package parkevents.entities;

import java.time.LocalDate;
import parkevents.EventType;

public class Play extends AbstractEvent {
    private int durationMinutes;

    public Play(String title, String description, LocalDate date) {
        super(title, description, date, EventType.PLAY);
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}
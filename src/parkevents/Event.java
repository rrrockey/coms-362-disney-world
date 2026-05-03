package parkevents;

import java.time.LocalDate;

public interface Event {
    EventType getType();
    String getTitle();
    LocalDate getDate();
    String getDescription();
}
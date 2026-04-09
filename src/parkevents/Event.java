package parkevents;

import java.util.List;
import parkevents.EventType;

public interface Event {

    String getTitle();
    EventType getType();
    String getDescription();
    String getDate();

    void addPerformerEmployee(String employeeName);
    void removePerformerEmployee(String employeeName);
    List<String> getPerformerEmployees();

    void saveEvent();
    boolean isSaved();

    void updateEvent(String title, String description, String date);
}
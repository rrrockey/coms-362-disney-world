package parkevents.entities;

import parkevents.Event;
import parkevents.EventType;

import java.time.LocalDate;

public abstract class AbstractEvent implements Event {
    private EventType type;
    private String title;
    private String description;
    private LocalDate date;
    private boolean saved;

    public AbstractEvent(String title, String description, LocalDate date, EventType type) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.saved = false;
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void updateEvent(String title, String description, LocalDate date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public void saveEvent() {
        saved = true;
    }

    public boolean isSaved() {
        return saved;
    }

    @Override
    public String toString() {
        return "Type: " + type
                + " | Title: " + title
                + " | Date: " + date
                + " | Description: " + description
                + " | Saved: " + saved;
    }
}
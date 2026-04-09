package parkevents.entities;

import parkevents.EventType;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


    public class Event implements parkevents.Event {
        private String title;
        private EventType type;
        private String description;
        private String date;
        private final List<String> performerEmployees;
        private boolean saved;

        public Event(String title, EventType type, String description, String date) {
            this.title = title;
            this.type = type;
            this.description = description;
            this.date = date;
            this.performerEmployees = new ArrayList<>();
            this.saved = false;
        }

        public String getTitle() {
            return title;
        }

        public EventType getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getDate() {
            return date;
        }

        public List<String> getPerformerEmployees() {
            return Collections.unmodifiableList(performerEmployees);
        }

        public boolean isSaved() {
            return saved;
        }

        public void addPerformerEmployee(String employeeName) {
            if (employeeName != null && !employeeName.isBlank()) {
                performerEmployees.add(employeeName);
            }
        }

        public void removePerformerEmployee(String employeeName) {
            performerEmployees.remove(employeeName);
        }

        public void saveEvent() {
            this.saved = true;
        }

        public void updateEvent(String title, String description, String date) {
            this.title = title;
            this.description = description;
            this.date = date;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "title='" + title + '\'' +
                    ", type='" + type + '\'' +
                    ", description='" + description + '\'' +
                    ", date='" + date + '\'' +
                    ", performerEmployees=" + performerEmployees +
                    ", saved=" + saved +
                    '}';
        }
    }

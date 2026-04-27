package parkevents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import parkevents.entities.AbstractEvent;
import parkevents.entities.Concert;
import parkevents.entities.Movie;
import parkevents.entities.Play;

public class EventRepository {
    private static final List<Event> events = new ArrayList<>();
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path EVENTS_FILE = DATA_DIR.resolve("events.csv");
    private static boolean loaded = false;

    public static void initialise() throws IOException {
        Files.createDirectories(DATA_DIR);

        if (!Files.exists(EVENTS_FILE)) {
            Files.createFile(EVENTS_FILE);
        }

        loadEvents();
    }

    public static void loadEvents() throws IOException {
        if (loaded) {
            return;
        }

        events.clear();

        try (BufferedReader reader = Files.newBufferedReader(EVENTS_FILE)) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 4) {
                    continue;
                }

                try {
                    EventType type = parseEventType(unescape(parts[0]));
                    String title = unescape(parts[1]);
                    String description = unescape(parts[2]);
                    LocalDate date = LocalDate.parse(unescape(parts[3]));

                    Event event = createEventByType(type, title, description, date);
                    if (event == null) {
                        continue;
                    }

                    if (event instanceof AbstractEvent abstractEvent) {
                        abstractEvent.saveEvent();
                    }

                    events.add(event);
                } catch (Exception e) {
                    System.err.println("Skipping invalid event row: " + line);
                }
            }
        }

        loaded = true;
    }

    public static List<Event> getAllEvents() {
        return events;
    }

    public static Event getEvent(int index) {
        if (index < 0 || index >= events.size()) {
            throw new IndexOutOfBoundsException("Invalid event index.");
        }
        return events.get(index);
    }

    public static void saveEvent(Event event) throws IOException {
        if (event != null && !events.contains(event)) {
            if (event instanceof AbstractEvent abstractEvent) {
                abstractEvent.saveEvent();
            }

            events.add(event);
            writeAllEvents();
        }
    }

    public static void updateEvent(int index, String title, String description, LocalDate date) throws IOException {
        Event event = getEvent(index);

        if (event instanceof AbstractEvent abstractEvent) {
            abstractEvent.updateEvent(title, description, date);
            writeAllEvents();
            return;
        }

        throw new IllegalStateException("Event does not support updates.");
    }

    public static void deleteEvent(int index) throws IOException {
        getEvent(index);
        events.remove(index);
        writeAllEvents();
    }

    private static void writeAllEvents() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(EVENTS_FILE)) {
            for (Event event : events) {
                writer.write(toCsvLine(event));
                writer.newLine();
            }
        }
    }

    private static String toCsvLine(Event event) {
        return String.join(",",
                escape(event.getType().name()),
                escape(event.getTitle()),
                escape(event.getDescription()),
                escape(event.getDate().toString())
        );
    }

    private static Event createEventByType(EventType type, String title, String description, LocalDate date) {
        return switch (type) {
            case CONCERT -> new Concert(title, description, date);
            case MOVIE -> new Movie(title, description, date);
            case PLAY -> new Play(title, description, date);
        };
    }

    private static EventType parseEventType(String value) {
        return EventType.valueOf(value.trim().toUpperCase());
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace(",", "\\c")
                .replace("\n", "\\n");
    }

    private static String unescape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\n", "\n")
                .replace("\\c", ",")
                .replace("\\\\", "\\");
    }
}
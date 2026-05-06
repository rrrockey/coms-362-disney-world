package parkevents;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import parkevents.entities.Concert;
import parkevents.entities.Movie;
import parkevents.entities.Play;

public class ManageParkEvents {

    private static final Scanner sc = app.DisneyWorld.sc;

    public static void manageParkEvents() {
        boolean running = true;

        while (running) {
            System.out.println("\n--- Manage Park Events ---");
            System.out.println("1) Create Event");
            System.out.println("2) View Events");
            System.out.println("3) Edit Existing Event");
            System.out.println("4) Cancel Event");
            System.out.println("0) Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> createEvent();
                case "2" -> viewEvents();
                case "3" -> editEvent();
                case "4" -> cancelEvent();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void createEvent() {
        try {
            System.out.println("\nSelect Event Type:");
            System.out.println("1) Concert");
            System.out.println("2) Movie");
            System.out.println("3) Play");
            System.out.print("Choice: ");
            String typeChoice = sc.nextLine().trim();

            System.out.print("Enter event title: ");
            String title = sc.nextLine().trim();

            System.out.print("Enter event description: ");
            String description = sc.nextLine().trim();

            LocalDate date;
            while (true) {
                System.out.print("Enter event date (yyyy-MM-dd): ");
                String input = sc.nextLine().trim();

                try {
                    date = LocalDate.parse(input);
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Use yyyy-MM-dd.");
                }
            }

            parkevents.Event event;

            switch (typeChoice) {
                case "1" -> event = new Concert(title, description, date);
                case "2" -> event = new Movie(title, description, date);
                case "3" -> event = new Play(title, description, date);
                default -> {
                    System.out.println("Invalid event type.");
                    return;
                }
            }

            EventRepository.saveEvent(event);
            System.out.println("Event created and saved successfully.");
        } catch (Exception e) {
            System.err.println("Failed to create event: " + e.getMessage());
        }
    }

    private static void viewEvents() {
        List<parkevents.Event> events = EventRepository.getAllEvents();

        if (events.isEmpty()) {
            System.out.println("No events currently scheduled.");
            return;
        }

        System.out.println("\n--- Scheduled Events ---");
        for (int i = 0; i < events.size(); i++) {
            System.out.println((i + 1) + ") " + events.get(i));
        }
    }

    private static void editEvent() {
        try {
            List<parkevents.Event> events = EventRepository.getAllEvents();

            if (events.isEmpty()) {
                System.out.println("No events available to edit.");
                return;
            }

            viewEvents();

            System.out.print("\nSelect event number to edit: ");
            int index = Integer.parseInt(sc.nextLine().trim()) - 1;

            parkevents.Event event = EventRepository.getEvent(index);

            System.out.print("Enter new title [" + event.getTitle() + "]: ");
            String title = sc.nextLine().trim();
            if (title.isBlank()) {
                title = event.getTitle();
            }

            System.out.print("Enter new description [" + event.getDescription() + "]: ");
            String description = sc.nextLine().trim();
            if (description.isBlank()) {
                description = event.getDescription();
            }

            LocalDate date;
            while (true) {
                System.out.print("Enter new event date (yyyy-MM-dd): ");
                String input = sc.nextLine().trim();

                try {
                    date = LocalDate.parse(input);
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Use yyyy-MM-dd.");
                }
            }

            EventRepository.updateEvent(index, title, description, date);
            System.out.println("Event updated successfully.");
        } catch (Exception e) {
            System.err.println("Failed to edit event: " + e.getMessage());
        }
    }

    private static void cancelEvent() {
        try {
            List<parkevents.Event> events = EventRepository.getAllEvents();

            if (events.isEmpty()) {
                System.out.println("No events available to cancel");
                return;
            }

            viewEvents();

            System.out.print("\nSelect event number to cancel: ");
            int index = Integer.parseInt(sc.nextLine().trim()) - 1;

            parkevents.Event event = EventRepository.getEvent(index);

            System.out.println("\nSelected Event:");
            System.out.println(event);

            System.out.print("Are you sure you want to cancel this event? (y/n): ");
            String confirm = sc.nextLine().trim().toLowerCase();

            if (!confirm.equals("y")) {
                System.out.println("Cancellation aborted. Event was not changed.");
                return;
            }

            EventRepository.deleteEvent(index);
            System.out.println("Event cancelled successfully.");
        } catch (Exception e) {
            System.err.println("Failed to cancel event: " + e.getMessage());
        }
    }
}
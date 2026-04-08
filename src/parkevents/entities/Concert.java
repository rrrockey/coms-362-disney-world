package parkevents.entities;

import parkevents.Event;

import java.util.List;

public class Concert implements Event {
    private String name;
    private List<String> employees;
    private String date;

    public Concert(String name, String artist, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}

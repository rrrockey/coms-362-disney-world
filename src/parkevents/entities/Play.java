package parkevents.entities;

import java.time.LocalDate;

import static parkevents.EventType.PLAY;

public class Play extends Event {
        public Play(String title, String description, LocalDate date) {
            super(title, PLAY, description, date);
        }
}

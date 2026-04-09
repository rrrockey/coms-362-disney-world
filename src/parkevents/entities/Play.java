package parkevents.entities;

import static parkevents.EventType.PLAY;

public class Play extends Event {
        public Play(String title, String description, String date) {
            super(title, PLAY, description, date);
        }
}

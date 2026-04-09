package app;

import java.io.IOException;
import java.util.List;

import guest.Guest;
import guest.GuestRepository;

public class ViewGuests {
	public static void ViewGuests() {
		List<Guest> guests = null;
		try {
			guests = GuestRepository.loadAllGuests();
		} catch (IOException e) {
			System.out.println("Failed to load guests.");
		}
		System.out.println(guests);
	}
}

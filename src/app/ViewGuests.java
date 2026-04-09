package app;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import guest.Guest;
import guest.GuestRepository;

public class ViewGuests {
	
	private static Scanner sc;
	
	public static void ViewGuests() {
		List<Guest> guests = null;
		try {
			guests = GuestRepository.loadAllGuests();
		} catch (IOException e) {
			System.out.println("Failed to load guests.");
		}
//		System.out.println(guests);
		sc = new Scanner(guests.toString());
		while(sc.hasNext()) {			
			System.out.println(sc.next());
		}
	}
}

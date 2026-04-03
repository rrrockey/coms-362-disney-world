package guest;

import java.util.ArrayList;

/**
 * 
 * @author ryanrockey, 
 *
 */
public class GuestGroup {
	// the guests that belong to this group
	private ArrayList<Guest> guests;
	
	// the manager of this GuestGroup
	private Guest manager;
	
	// name of the group
	public String groupName;
	
	public GuestGroup(Guest manager, String groupName) {
		try {
			this.manager = manager;
			addGuest(manager);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// adds the specified guest from the guestGroup
	public void addGuest(Guest newGuest) throws Exception {
		if (this.guests.contains(newGuest)) {
			throw new Exception("Guest " + newGuest.name+ " already in group.");
		}
		
		this.guests.add(newGuest);
	}
	
	// removes the specified guest from the guestGroup
	public void removeGuest(Guest newGuest) throws Exception {
		if (!this.guests.contains(newGuest)) {
			throw new Exception("Guest " + newGuest.name+ " not in group.");
		}
		
		this.guests.remove(newGuest);
	}
}

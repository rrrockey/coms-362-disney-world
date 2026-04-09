package hotel;

public class Hotel {
public class CustomerBooksRoom extends UseCase {
	
	    public CustomerBooksRoom() {
	        super(
	            "Customer books a hotel room",
	            List.of("Customer", "Hotel Employee"),
	            "Customer has enough money to book a room; hotel has rooms available",
	            List.of(
	                "Customer goes online and selects a room to book",
	                "Customer selects dates they want the room and pays for them",
	                "Employee updates room to occupied for those dates"
	            ),
	            List.of(
	                "Customer calls to book room"
	            )
	        );
	    }
	}
	
	public class EmployeeCleansRoom extends UseCase {
	
	    public EmployeeCleansRoom() {
	        super(
	            "Employee cleans used hotel room",
	            List.of("Hotel Employee", "Housekeeper"),
	            "Room is empty and housekeeper has access",
	            List.of(
	                "Housekeeper enters room",
	                "Housekeeper takes out trash and cleans hotel room",
	                "Housekeeper confirms to employee that room is clean",
	                "Employee marks room as available for next guest"
	            ),
	            List.of() // no alternate flows provided
	        );
	    }
	}
	
	public class CustomerChecksIn extends UseCase {
	
	    public CustomerChecksIn() {
	        super(
	            "Customer checks into a hotel room",
	            List.of("Customer", "Front Desk Employee"),
	            "Customer has arrived and has a room booked to check into",
	            List.of(
	                "Customer goes to check in",
	                "Employee checks if customer has a room booked",
	                "Employee gives customer the key to the room",
	                "Employee marks customer as checked in"
	            ),
	            List.of() // no alternate flows provided
	        );
	    }
	}
	
	public class CustomerChecksOut extends UseCase {
	
	    public CustomerChecksOut() {
	        super(
	            "Customer checks out of a hotel room",
	            List.of("Customer", "Front Desk Employee"),
	            "Customer has stayed in room and needs to check out",
	            List.of(
	                "Customer leaves room",
	                "Customer returns room key to front desk employee",
	                "Employee marks room as checked out and ready for cleaning"
	            ),
	            List.of() // no alternate flows provided
	        );
	    }
	}
}

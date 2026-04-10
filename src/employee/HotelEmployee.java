package employee;

/**
 * A hotel staff member
 */
public class HotelEmployee extends Employee {

    public final int    employeeId;
    public final String name;
    public final String role;   // "Front Desk", "Housekeeper", "Manager"

    public HotelEmployee(int employeeId, String name, String role) {
        this.employeeId = employeeId;
        this.name       = name;
        this.role       = role;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (ID %d)", role, name, employeeId);
    }
}
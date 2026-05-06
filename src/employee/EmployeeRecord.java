package employee;

public class EmployeeRecord {
    private final int employeeId;
    private final String name;
    private final EmployeeType type;

    public EmployeeRecord(int employeeId, String name, EmployeeType type) {
        this.employeeId = employeeId;
        this.name = name;
        this.type = type;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public EmployeeType getType() {
        return type;
    }

    public String toCsv() {
        return employeeId + "," + name + "," + type;
    }

    public static EmployeeRecord fromCsv(String line) {
        String[] parts = line.split(",", -1);

        return new EmployeeRecord(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                EmployeeType.valueOf(parts[2].trim().toUpperCase())
        );
    }

    @Override
    public String toString() {
        return "Employee ID: " + employeeId
                + " | Name: " + name
                + " | Department: " + type;
    }
}
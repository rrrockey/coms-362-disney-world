package employee;

public class ManagerRecord {
    private final int managerId;
    private final String name;
    private final EmployeeType department;

    public ManagerRecord(int managerId, String name, EmployeeType department) {
        this.managerId = managerId;
        this.name = name;
        this.department = department;
    }

    public int getManagerId() {
        return managerId;
    }

    public String getName() {
        return name;
    }

    public EmployeeType getDepartment() {
        return department;
    }

    public String toCsv() {
        return managerId + "," + name + "," + department;
    }

    public static ManagerRecord fromCsv(String line) {
        String[] parts = line.split(",", -1);

        return new ManagerRecord(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                EmployeeType.valueOf(parts[2].trim().toUpperCase())
        );
    }

    @Override
    public String toString() {
        return "Manager ID: " + managerId
                + " | Name: " + name
                + " | Department: " + department;
    }
}
package employee;

import java.time.LocalDate;
import java.time.LocalTime;

public class ShiftAssignment {
    private final int shiftId;
    private final int employeeId;
    private final String employeeName;
    private final EmployeeType department;
    private final String role;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public ShiftAssignment(int shiftId, int employeeId, String employeeName,
                           EmployeeType department, String role,
                           LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.shiftId = shiftId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.role = role;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getShiftId() {
        return shiftId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public boolean conflictsWith(int employeeId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (this.employeeId != employeeId || !this.date.equals(date)) {
            return false;
        }

        return startTime.isBefore(this.endTime) && endTime.isAfter(this.startTime);
    }

    public String toCsv() {
        return shiftId + ","
                + employeeId + ","
                + employeeName + ","
                + department + ","
                + role + ","
                + date + ","
                + startTime + ","
                + endTime;
    }

    public static ShiftAssignment fromCsv(String line) {
        String[] parts = line.split(",", -1);

        return new ShiftAssignment(
                Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[1].trim()),
                parts[2].trim(),
                EmployeeType.valueOf(parts[3].trim().toUpperCase()),
                parts[4].trim(),
                LocalDate.parse(parts[5].trim()),
                LocalTime.parse(parts[6].trim()),
                LocalTime.parse(parts[7].trim())
        );
    }

    @Override
    public String toString() {
        return "Shift ID: " + shiftId
                + " | Employee: " + employeeName
                + " | Department: " + department
                + " | Role: " + role
                + " | Date: " + date
                + " | Time: " + startTime + "-" + endTime;
    }
}
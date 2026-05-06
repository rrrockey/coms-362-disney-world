package app;

import employee.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ManageEmployeeScheduling {
    private static final Scanner sc = new Scanner(System.in);

    public static void manageEmployeeScheduling() {
        ManagerRecord manager = selectManager();

        if (manager == null) {
            System.out.println("Manager selection cancelled.");
            return;
        }

        System.out.println("Manager profile selected: " + manager.getName());

        boolean running = true;

        while (running) {
            System.out.println("\n--- Employee Scheduling ---");
            System.out.println("Manager: " + manager.getName());
            System.out.println("1) Create Employee");
            System.out.println("2) Assign Employee to Shift");
            System.out.println("3) View Employees");
            System.out.println("4) View Shifts");
            System.out.println("0) Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> createEmployee();
                case "2" -> assignEmployeeToShift(manager);
                case "3" -> viewEmployees();
                case "4" -> viewShifts();
                case "0" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void createEmployee() {
        try {
            System.out.print("Enter employee name: ");
            String name = sc.nextLine().trim();

            if (name.isBlank()) {
                System.out.println("Employee name is required.");
                return;
            }

            System.out.println("Select employee department:");
            EmployeeType[] types = EmployeeType.values();

            for (int i = 0; i < types.length; i++) {
                System.out.println((i + 1) + ") " + types[i]);
            }

            System.out.print("Choice: ");
            int typeIndex = Integer.parseInt(sc.nextLine().trim()) - 1;

            if (typeIndex < 0 || typeIndex >= types.length) {
                System.out.println("Invalid employee type.");
                return;
            }

            int employeeId = EmployeeRepository.nextEmployeeId();
            EmployeeRecord employee = new EmployeeRecord(employeeId, name, types[typeIndex]);

            EmployeeRepository.saveEmployee(employee);
            System.out.println("Employee created successfully.");
            System.out.println(employee);
        } catch (Exception e) {
            System.err.println("Failed to create employee: " + e.getMessage());
        }
    }

    private static void assignEmployeeToShift(ManagerRecord manager) {
        try {
            List<EmployeeRecord> employees = EmployeeRepository.loadAllEmployees();

            if (employees.isEmpty()) {
                System.out.println("No employees available. Create an employee first.");
                return;
            }

            System.out.println("\nAvailable Employees:");
            EmployeeIterator employeeIterator = new EmployeeIterator(employees);
            int displayNumber = 1;

            while (employeeIterator.hasNext()) {
                System.out.println(displayNumber + ") " + employeeIterator.next());
                displayNumber++;
            }

            System.out.print("Select employee: ");
            int employeeIndex = Integer.parseInt(sc.nextLine().trim()) - 1;

            if (employeeIndex < 0 || employeeIndex >= employees.size()) {
                System.out.println("Invalid employee selection.");
                return;
            }

            EmployeeRecord employee = employees.get(employeeIndex);

            System.out.print("Enter role for shift: ");
            String role = sc.nextLine().trim();

            if (role.isBlank()) {
                System.out.println("Role is required.");
                return;
            }

            LocalDate date = readDate("Enter shift date (yyyy-MM-dd): ");
            LocalTime startTime = readTime("Enter start time (HH:mm): ");
            LocalTime endTime = readTime("Enter end time (HH:mm): ");

            if (!endTime.isAfter(startTime)) {
                System.out.println("Invalid shift time. End time must be after start time.");
                return;
            }

            ShiftAssignment conflict = ShiftRepository.findConflict(
                    employee.getEmployeeId(),
                    date,
                    startTime,
                    endTime
            );

            if (conflict != null) {
                System.out.println("Employee is already scheduled during that time.");
                System.out.println("Conflicting shift: " + conflict);
                return;
            }

            System.out.println("\nConfirm Shift:");
            System.out.println("Employee: " + employee.getName());
            System.out.println("Department: " + employee.getType());
            System.out.println("Role: " + role);
            System.out.println("Date: " + date);
            System.out.println("Time: " + startTime + "-" + endTime);
            System.out.print("Confirm assignment? (y/n): ");

            String confirm = sc.nextLine().trim().toLowerCase();

            if (!confirm.equals("y")) {
                System.out.println("Shift assignment cancelled.");
                return;
            }

            int shiftId = ShiftRepository.nextShiftId();

            ShiftAssignment shift = new ShiftAssignment(
                    shiftId,
                    employee.getEmployeeId(),
                    employee.getName(),
                    employee.getType(),
                    role,
                    date,
                    startTime,
                    endTime
            );

            ShiftRepository.saveShift(shift);
            System.out.println("Employee successfully assigned to shift.");
            System.out.println(shift);
        } catch (Exception e) {
            System.err.println("Failed to assign shift: " + e.getMessage());
        }
    }

    private static void viewEmployees() {
        try {
            EmployeeIterator iterator = EmployeeRepository.getEmployeeIterator();

            if (!iterator.hasNext()) {
                System.out.println("No employees found.");
                return;
            }

            System.out.println("\n--- Employees ---");
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        } catch (Exception e) {
            System.err.println("Failed to view employees: " + e.getMessage());
        }
    }

    private static void viewShifts() {
        try {
            ShiftIterator iterator = ShiftRepository.getShiftIterator();

            if (!iterator.hasNext()) {
                System.out.println("No shifts found.");
                return;
            }

            System.out.println("\n--- Shifts ---");
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        } catch (Exception e) {
            System.err.println("Failed to view shifts: " + e.getMessage());
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd.");
            }
        }
    }

    private static LocalTime readTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                return LocalTime.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Use HH:mm.");
            }
        }
    }

    private static ManagerRecord selectManager() {
        try {
            List<ManagerRecord> managers = ManagerRepository.loadAllManagers();

            if (managers.isEmpty()) {
                System.out.println("No managers found. Cannot access employee scheduling.");
                return null;
            }

            System.out.println("\n--- Select Manager Profile ---");

            ManagerIterator iterator = new ManagerIterator(managers);
            int displayNumber = 1;

            while (iterator.hasNext()) {
                System.out.println(displayNumber + ") " + iterator.next());
                displayNumber++;
            }

            System.out.println("0) Cancel");
            System.out.print("Select manager: ");

            int selection = Integer.parseInt(sc.nextLine().trim());

            if (selection == 0) {
                return null;
            }

            int index = selection - 1;

            if (index < 0 || index >= managers.size()) {
                System.out.println("Invalid manager selection.");
                return null;
            }

            return managers.get(index);
        } catch (Exception e) {
            System.err.println("Failed to select manager: " + e.getMessage());
            return null;
        }
    }
}
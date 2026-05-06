package employee;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class EmployeeRepository {
    private static final String DATA_DIR = "data";
    private static final String EMPLOYEE_FILE = DATA_DIR + "/employees.csv";

    public static void initialise() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));

        Path path = Paths.get(EMPLOYEE_FILE);
        if (!Files.exists(path)) {
            Files.writeString(path, "employeeId,name,type\n");
        }
    }

    public static List<EmployeeRecord> loadAllEmployees() throws IOException {
        List<EmployeeRecord> employees = new ArrayList<>();

        for (String line : Files.readAllLines(Paths.get(EMPLOYEE_FILE))) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("employeeId")) {
                continue;
            }

            try {
                employees.add(EmployeeRecord.fromCsv(line));
            } catch (Exception e) {
                System.err.println("[EmployeeRepository] Skipping malformed employee line: " + line);
            }
        }

        return employees;
    }

    public static EmployeeIterator getEmployeeIterator() throws IOException {
        return new EmployeeIterator(loadAllEmployees());
    }

    public static void saveEmployee(EmployeeRecord employee) throws IOException {
        Map<Integer, EmployeeRecord> all = loadAllEmployeesMap();
        all.put(employee.getEmployeeId(), employee);
        writeEmployeesMap(all);
    }

    public static int nextEmployeeId() throws IOException {
        int max = 0;

        for (EmployeeRecord employee : loadAllEmployees()) {
            if (employee.getEmployeeId() > max) {
                max = employee.getEmployeeId();
            }
        }

        return max + 1;
    }

    private static Map<Integer, EmployeeRecord> loadAllEmployeesMap() throws IOException {
        Map<Integer, EmployeeRecord> map = new LinkedHashMap<>();

        for (EmployeeRecord employee : loadAllEmployees()) {
            map.put(employee.getEmployeeId(), employee);
        }

        return map;
    }

    private static void writeEmployeesMap(Map<Integer, EmployeeRecord> employees) throws IOException {
        StringBuilder sb = new StringBuilder("employeeId,name,type\n");

        for (EmployeeRecord employee : employees.values()) {
            sb.append(employee.toCsv()).append("\n");
        }

        Files.writeString(Paths.get(EMPLOYEE_FILE), sb.toString());
    }
}
package employee;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ShiftRepository {
    private static final String DATA_DIR = "data";
    private static final String SHIFT_FILE = DATA_DIR + "/shifts.csv";

    public static void initialise() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));

        Path path = Paths.get(SHIFT_FILE);
        if (!Files.exists(path)) {
            Files.writeString(path, "shiftId,employeeId,employeeName,department,role,date,startTime,endTime\n");
        }
    }

    public static List<ShiftAssignment> loadAllShifts() throws IOException {
        List<ShiftAssignment> shifts = new ArrayList<>();

        for (String line : Files.readAllLines(Paths.get(SHIFT_FILE))) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("shiftId")) {
                continue;
            }

            try {
                shifts.add(ShiftAssignment.fromCsv(line));
            } catch (Exception e) {
                System.err.println("[ShiftRepository] Skipping malformed shift line: " + line);
            }
        }

        return shifts;
    }

    public static ShiftIterator getShiftIterator() throws IOException {
        return new ShiftIterator(loadAllShifts());
    }

    public static void saveShift(ShiftAssignment shift) throws IOException {
        List<ShiftAssignment> shifts = loadAllShifts();
        shifts.add(shift);
        writeAllShifts(shifts);
    }

    public static ShiftAssignment findConflict(int employeeId, LocalDate date,
                                               LocalTime startTime, LocalTime endTime) throws IOException {
        ShiftIterator iterator = getShiftIterator();

        while (iterator.hasNext()) {
            ShiftAssignment shift = iterator.next();

            if (shift.conflictsWith(employeeId, date, startTime, endTime)) {
                return shift;
            }
        }

        return null;
    }

    public static int nextShiftId() throws IOException {
        int max = 0;

        ShiftIterator iterator = getShiftIterator();

        while (iterator.hasNext()) {
            ShiftAssignment shift = iterator.next();

            if (shift.getShiftId() > max) {
                max = shift.getShiftId();
            }
        }

        return max + 1;
    }

    private static void writeAllShifts(List<ShiftAssignment> shifts) throws IOException {
        StringBuilder sb = new StringBuilder("shiftId,employeeId,employeeName,department,role,date,startTime,endTime\n");

        for (ShiftAssignment shift : shifts) {
            sb.append(shift.toCsv()).append("\n");
        }

        Files.writeString(Paths.get(SHIFT_FILE), sb.toString());
    }
}
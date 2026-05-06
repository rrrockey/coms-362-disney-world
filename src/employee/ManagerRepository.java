package employee;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ManagerRepository {
    private static final String DATA_DIR = "data";
    private static final String MANAGER_FILE = DATA_DIR + "/managers.csv";

    public static void initialise() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));

        Path path = Paths.get(MANAGER_FILE);
        if (!Files.exists(path)) {
            Files.writeString(path,
                    "managerId,name,department\n"
                            + "1,General Manager,MANAGEMENT\n");
        }
    }

    public static List<ManagerRecord> loadAllManagers() throws IOException {
        List<ManagerRecord> managers = new ArrayList<>();

        for (String line : Files.readAllLines(Paths.get(MANAGER_FILE))) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("managerId")) {
                continue;
            }

            try {
                managers.add(ManagerRecord.fromCsv(line));
            } catch (Exception e) {
                System.err.println("[ManagerRepository] Skipping malformed manager line: " + line);
            }
        }

        return managers;
    }

    public static ManagerIterator getManagerIterator() throws IOException {
        return new ManagerIterator(loadAllManagers());
    }
}
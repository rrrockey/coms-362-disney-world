package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileIO {

        public static String readFile(String filePath) throws FileNotFoundException {
            File fileToRead = new File(filePath);
            Scanner scanner = new Scanner(fileToRead);

            StringBuilder fileContent = new StringBuilder();
            while (scanner.hasNextLine()) {
                fileContent.append(scanner.nextLine()).append("\n");
            }
            scanner.close();

            return fileContent.toString();
        }

        public static void writeFile(String filePath, String content) {
            try {
                java.io.FileWriter writer = new FileWriter(filePath);
                writer.write(content);
                writer.close();
            } catch (IOException e) {
                System.err.println("An error occurred while writing to the file: " + e.getMessage());
            }
        }
}

package org.example.utils;

import org.example.model.Person;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileExporter {

    public static boolean exportToFile(List<Person> persons, String operationType) {
        if (persons == null || persons.isEmpty()) {
            System.out.println("Нет данных для экспорта");
            return false;
        }

        String fileName = generateFileName(operationType);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("=== " + operationType + " ===");
            writer.newLine();
            writer.write("Экспортировано: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.newLine();
            writer.write("Количество записей: " + persons.size());
            writer.newLine();
            writer.newLine();

            for (int i = 0; i < persons.size(); i++) {
                Person person = persons.get(i);
                String line = String.format("%d. %s", i + 1, person.toString());
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Данные успешно экспортированы в файл: " + fileName);
            return true;

        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
            return false;
        }
    }

    private static String generateFileName(String operationType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        return String.format("%s_%s.txt", operationType.toLowerCase().replace(" ", "_"), timestamp);
    }
}
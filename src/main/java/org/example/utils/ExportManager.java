package org.example.utils;

import org.example.model.Person;
import java.util.List;
import java.util.Scanner;

public class ExportManager {

    public static void offerExport(List<Person> persons, String operationType, Scanner scanner) {
        System.out.println("\n=== ЭКСПОРТ В ФАЙЛ ===");
        System.out.println("1. Экспортировать результат в файл");
        System.out.println("2. Продолжить без экспорта");

        boolean validChoice = false;

        while (!validChoice) {
            System.out.print("Выберите вариант (1 или 2): ");
            scanner.nextLine();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    boolean success = FileExporter.exportToFile(persons, operationType);
                    if (success) {
                        System.out.println("Экспорт завершен успешно!");
                    } else {
                        System.out.println("Экспорт не удался.");
                    }
                    validChoice = true;
                    break;

                case "2":
                    System.out.println("Экспорт отменен.");
                    validChoice = true;
                    break;

                default:
                    System.out.println("Некорректный ввод. Пожалуйста, введите 1 или 2.");
                    break;
            }
        }

        System.out.println("\nНажмите Enter для продолжения...");
        scanner.nextLine();
    }
}
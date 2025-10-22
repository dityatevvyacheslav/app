package src.main.java.org.example.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

public class InputUtils {
    private List<Person> persons;
    private SimpleValidationManager validator;
    private Scanner scanner;

    public InputUtils() {
        this.persons = new ArrayList<>();
        this.validator = new SimpleValidationManager();
        this.scanner = new Scanner(System.in);
    }

    public int getIntInput(String message) {
        while (true) {
            try {
                System.out.print(message);
                return scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Ошибка: введите целое число.");
                scanner.nextLine();
            }
        }
    }

    public String getStringInput(String message) {
        System.out.print(message);
        return scanner.next();
    }

    public void closeScanner() {
        scanner.close();
    }

    public void loadFromFile(String fileName) {
        String fileError = validator.validateFile(fileName);
        if (fileError != null) {
            System.out.println(fileError);
            return;
        }

        try {
            Path filePath = Paths.get(fileName);
            List<String> allLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Person> loadedPersons = new ArrayList<>();

            for (int i = 0; i < allLines.size(); i++) {
                String line = allLines.get(i);
                int lineNumber = i + 1;

                String lineError = validator.validateDataLine(line, lineNumber);
                if (lineError != null) {
                    System.out.println(lineError);
                    continue;
                }

                String[] parts = line.split(",");
                Person person = Person.builder()
                        .name(parts[0].trim())
                        .age(Integer.parseInt(parts[1].trim()))
                        .score(Integer.parseInt(parts[2].trim()))
                        .build();

                loadedPersons.add(person);
            }

            this.persons = loadedPersons;
            System.out.println("Массив успешно загружен из файла. Загружено " + persons.size() + " записей.");

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

    public void generateRandom(int length) {
        String lengthError = validator.validateArrayLength(length);
        if (lengthError != null) {
            System.out.println(lengthError);
            return;
        }

        String[] names = {"Анна", "Иван", "Мария", "Петр", "Ольга", "Сергей", "Елена", "Дмитрий", "Наталья", "Алексей"};
        Random random = new Random();

        persons.clear();

        for (int i = 0; i < length; i++) {
            Person person = Person.builder()
                    .name(names[random.nextInt(names.length)] + "_" + (i + 1))
                    .age(18 + random.nextInt(50))
                    .score(random.nextInt(101))
                    .build();
            persons.add(person);
        }

        System.out.println("Массив из " + length + " случайных записей создан.");
    }

    public void createManually(int length) {
        String lengthError = validator.validateArrayLength(length);
        if (lengthError != null) {
            System.out.println(lengthError);
            return;
        }

        persons.clear();

        for (int i = 0; i < length; i++) {
            System.out.println("\nЗапись " + (i + 1) + ":");
            Person person = Person.builder()
                    .name(getValidatedName())
                    .age(getValidatedAge())
                    .score(getValidatedScore())
                    .build();

            persons.add(person);
        }

        System.out.println("Массив из " + length + " записей создан вручную.");
    }

    private String getValidatedName() {
        return Stream.generate(() -> getStringInput("Введите имя: "))
                .filter(name -> validator.validateString(name, "имя") == null)
                .findFirst()
                .orElse("");
    }

    private int getValidatedAge() {
        return Stream.generate(() -> getIntInput("Введите возраст: "))
                .filter(age -> validator.validateRange(age, 0, 150, "возраст") == null)
                .findFirst()
                .orElse(0);
    }

    private int getValidatedScore() {
        return Stream.generate(() -> getIntInput("Введите оценку: "))
                .filter(score -> validator.validateRange(score, 0, 100, "оценка") == null)
                .findFirst()
                .orElse(0);
    }

    public int size() {
        return persons.size();
    }

    public Person get(int index) {
        if (index >= 0 && index < persons.size()) {
            return persons.get(index);
        }
        return null;
    }

    public void clear() {
        persons.clear();
        System.out.println("Массив очищен.");
    }
}
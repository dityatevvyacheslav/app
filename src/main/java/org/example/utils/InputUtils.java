package org.example.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.Collections;

public class InputUtils {
    private final List<Person> persons = Collections.synchronizedList(new ArrayList<>());
    private final SimpleValidationManager validator;

    public InputUtils() {
        this.validator = new SimpleValidationManager();
    }

    public int getIntInput(String message) {
        try (Scanner scanner = new Scanner(System.in)) {
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
    }

    public String getStringInput(String message) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print(message);
            return scanner.next();
        }
    }

    private String getValidatedName() {
        while (true) {
            String name = getStringInput("Введите имя: ");
            if (validator.validateString(name, "имя") == null) {
                return name;
            }
            System.out.println("Ошибка: имя не может быть пустым.");
        }
    }

    private int getValidatedAge() {
        while (true) {
            int age = getIntInput("Введите возраст: ");
            if (validator.validateRange(age, 0, 150, "возраст") == null) {
                return age;
            }
            System.out.println("Ошибка: возраст должен быть от 0 до 150 лет.");
        }
    }

    private int getValidatedScore() {
        while (true) {
            int score = getIntInput("Введите оценку: ");
            if (validator.validateRange(score, 0, 100, "оценка") == null) {
                return score;
            }
            System.out.println("Ошибка: оценка должна быть от 0 до 100.");
        }
    }

    public void loadFromFile(String fileName) {
        String fileError = validator.validateFile(fileName);
        if (fileError != null) {
            System.out.println(fileError);
            return;
        }

        List<Person> loadedPersons = new ArrayList<>();

        try (Stream<String> lines = Files.lines(Paths.get(fileName), StandardCharsets.UTF_8)) {
            lines.forEach(line -> {
                int lineNumber = loadedPersons.size() + 1;
                line = line.trim();

                if (line.isEmpty()) {
                    System.out.println("Пустая строка " + lineNumber + " пропущена");
                    return;
                }

                String error = validator.validateDataLine(line, lineNumber);
                if (error != null) {
                    System.out.println(error);
                } else {
                    loadedPersons.add(createPersonFromLine(line));
                }
            });

            this.persons.clear();
            this.persons.addAll(loadedPersons);
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
            String simulatedLine = names[random.nextInt(names.length)] + "_" + (i + 1) + "," +
                    (18 + random.nextInt(50)) + "," +
                    random.nextInt(101);

            persons.add(createPersonFromLine(simulatedLine));
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

            String name = getValidatedName();
            int age = getValidatedAge();
            int score = getValidatedScore();

            String manualLine = name + "," + age + "," + score;
            persons.add(createPersonFromLine(manualLine));
        }

        System.out.println("Массив из " + length + " записей создан вручную.");
    }

    public int size() {
        return persons.size();
    }

    public Person get(int index) {
        synchronized (persons) {
            if (index >= 0 && index < persons.size()) {
                return persons.get(index);
            }
            return null;
        }
    }

    public void clear() {
        synchronized (persons) {
            persons.clear();
            System.out.println("Массив очищен.");
        }
    }

    public List<Person> getAllPersons() {
        synchronized (persons) {
            return new ArrayList<>(persons);
        }
    }

    private Person createPersonFromLine(String line) {
        String[] parts = line.split(",");
        return Person.builder()
                .name(parts[0].trim())
                .age(Integer.parseInt(parts[1].trim()))
                .score(Integer.parseInt(parts[2].trim()))
                .build();
    }
}
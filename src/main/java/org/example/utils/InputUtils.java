package src.main.java.org.example.utils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InputUtils {
    private Scanner scanner;
    private ValidatorsChain validatorsChain;
    private List<Person> list;

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
        try {
            Path filePath = Paths.get(fileName);
            if (!Files.exists(filePath)) {
                System.out.println("Ошибка: файл '" + fileName + "' не найден.");
                return;
            }
            if (!Files.isReadable(filePath)) {
                System.out.println("Ошибка: нет прав на чтение файла '" + fileName + "'.");
                return;
            }
            try (Stream<String> lines = Files.lines(filePath)) {
                this.list = lines
                        .map(line -> line.split(","))
                        .filter(parts -> parts.length == 3)
                        .map(parts -> {
                            return Person.builder()
                                    .name(parts[0].trim())
                                    .age(Integer.parseInt(parts[1].trim()))
                                    .score(Integer.parseInt(parts[2].trim()))
                                    .build();
                        }).filter(Objects::nonNull)
                        .filter(person -> validatorsChain.validate(person) == null)
                        .toList();
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

    public void generateRandom(int length) {
        String[] names = {"Анна", "Иван", "Мария", "Петр", "Ольга", "Сергей", "Елена", "Дмитрий", "Наталья", "Алексей"};
        Random random = new Random();

        this.list = IntStream.range(0, length)
                .mapToObj(i -> {
                    return Person.builder()
                            .name(names[random.nextInt(names.length)] + "_" + (i + 1))
                            .age(18 + random.nextInt(50))
                            .score(random.nextInt(101))
                            .build();
                })
                .collect(Collectors.toList());
        System.out.println("Массив из " + length + " случайных записей создан.");
    }

    public void createManually(int length) {

        list.clear();

        for (int i = 0; i < length; i++) {
            System.out.println("\nЗапись " + (i + 1) + ":");

            String name = getValidatedName();
            int age = getValidatedAge();
            int score = getValidatedScore();

            Person person = Person.builder()
                    .name(name)
                    .age(age)
                    .score(score)
                    .build();

            String error = validatorsChain.validate(person);
            if (error != null) {
                System.out.println(error);
                i--;
                continue;
            }

            list.add(person);
        }

        System.out.println("Массив из " + length + " записей создан вручную.");
    }

    private String getValidatedName() {
        return Stream.generate(() -> getStringInput("Введите имя: "))
                .filter(name -> name != null && !name.trim().isEmpty())
                .findFirst()
                .orElse("");
    }

    private int getValidatedAge() {
        return Stream.generate(() -> getIntInput("Введите возраст: "))
                .filter(age -> age >= 0 && age <= 150)
                .findFirst()
                .orElse(0);
    }

    private int getValidatedScore() {
        return Stream.generate(() -> getIntInput("Введите оценку: "))
                .filter(score -> score >= 0 && score <= 100)
                .findFirst()
                .orElse(0);
    }

    public Person get(int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    public void clear() {
        list.clear();
        System.out.println("Массив очищен.");
    }

    public void addValidator(ValidationUtils validator) {
        validatorsChain.addValidator(validator);
    }

    public String validatePerson(Person person) {
        return validatorsChain.validate(person);
    }
}
package org.example.app;

import org.example.model.Person;
import org.example.searching.BinarySearch;
import org.example.sorting.*;
import org.example.sorting.SortContext;
import org.example.utils.InputUtils;
import org.example.utils.ExportManager;
import org.example.sorting.async.*;

import java.util.*;

public class Application {
    private InputUtils inputUtils;
    private BinarySearch<Person> binarySearch;
    private SortContext<Person> sortContext;
    private List<Person> persons;
    private Scanner scanner;

    public Application() {
        this.inputUtils = new InputUtils();
        this.binarySearch = new BinarySearch<>();
        this.sortContext = new SortContext<>();
        this.persons = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Application app = new Application();
        app.run();
    }

    public void run() {
        while (true) {
            showMainMenu();
            int choice = getIntInput("Выберите пункт: ");

            switch (choice) {
                case 1:
                    selectInputMethod();
                    break;
                case 2:
                    testSorting();
                    break;
                case 3:
                    testSearch();
                    break;
                case 4:
                    viewPersonList();
                    break;
                case 5:
                    System.out.println("Выход...");
                    inputUtils.closeScanner();
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
        System.out.println("1. Ввод данных");
        System.out.println("2. Сортировка");
        System.out.println("3. Поиск");
        System.out.println("4. Просмотр списка");
        System.out.println("5. Выход");
    }

    private void selectInputMethod() {
        System.out.println("\n=== ВВОД ДАННЫХ ===");
        System.out.println("1. Из файла");
        System.out.println("2. Случайные данные");
        System.out.println("3. Вручную");
        System.out.println("4. Назад");

        int choice = getIntInput("Выберите: ");

        switch (choice) {
            case 1:
                String fileName = getStringInput("Имя файла: ");
                inputUtils.loadFromFile(fileName);
                persons = getPersonsFromInputUtils();
                break;
            case 2:
                int length = getIntInput("Количество записей: ");
                inputUtils.generateRandom(length);
                persons = getPersonsFromInputUtils();
                break;
            case 3:
                int manualLength = getIntInput("Количество записей: ");
                inputUtils.createManually(manualLength);
                persons = getPersonsFromInputUtils();
                break;
            case 4:
                return;
        }
    }

    private void testSorting() {
        if (persons.isEmpty()) {
            System.out.println("Данные не загружены");
            return;
        }

        System.out.println("\n=== СОРТИРОВКА ===");
        System.out.println("1. Пузырьковая");
        System.out.println("2. Вставками");
        System.out.println("3. Слиянием (параллельная)");
        System.out.println("4. Слиянием по числовому полю (четные)");
        System.out.println("5. Назад");

        int algorithmChoice = getIntInput("Выберите сортировку: ");

        if (algorithmChoice == 5) {
            return;
        }

        System.out.println("\n=== ПОЛЕ ДЛЯ СОРТИРОВКИ ===");
        System.out.println("1. По имени");
        System.out.println("2. По возрасту");
        System.out.println("3. По оценке");

        int fieldChoice = getIntInput("Выберите поле: ");

        List<Person> sortedList = new ArrayList<>(persons);

        System.out.println("\n=== ДО СОРТИРОВКИ ===");
        showList(sortedList);

        long start = System.currentTimeMillis();

        if (algorithmChoice == 4) {
            handleMergeSortNumericField(fieldChoice, sortedList);
        } else {
            Comparator<Person> comparator = getComparator(fieldChoice);
            if (comparator == null) {
                System.out.println("Неверный выбор поля");
                return;
            }
            setSortStrategy(algorithmChoice);
            sortContext.executeSort(sortedList, comparator);
        }

        long end = System.currentTimeMillis();

        System.out.println("\n=== ПОСЛЕ СОРТИРОВКИ ===");
        showList(sortedList);
        System.out.println("\nВремя выполнения: " + (end - start) + " мс");

        persons = sortedList;

        String algorithmName = getAlgorithmName(algorithmChoice);
        String fieldName = getFieldNameForExport(fieldChoice);
        String operationType = "Сортировка_" + algorithmName + "_по_" + fieldName;

        ExportManager.offerExport(persons, operationType, scanner);
    }

    private void handleMergeSortNumericField(int fieldChoice, List<Person> sortedList) {
        String fieldName;
        switch (fieldChoice) {
            case 1:
                System.out.println("Сортировка по числовому полю не поддерживает сортировку по имени");
                return;
            case 2:
                fieldName = "age";
                break;
            case 3:
                fieldName = "score";
                break;
            default:
                System.out.println("Неверный выбор поля");
                return;
        }

        int threadCount = Runtime.getRuntime().availableProcessors();
        try (MergeSortNumericField<Person> numericSorter = new MergeSortNumericField<>(threadCount, fieldName)) {


            List<Person> result = numericSorter.sortSync(sortedList);

            sortedList.clear();
            sortedList.addAll(result);

            System.out.println("Сортировка завершена. Отсортированы только элементы с четными значениями в поле " + fieldName);

        } catch (Exception e) {
            System.out.println("Ошибка при сортировке: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setSortStrategy(int algorithmChoice) {
        switch (algorithmChoice) {
            case 1:
                sortContext.setStrategy(new BubbleSort<Person>());
                System.out.println("Выбрана пузырьковая сортировка");
                break;
            case 2:
                sortContext.setStrategy(new InsertionSort<Person>());
                System.out.println("Выбрана сортировка вставками");
                break;
            case 3:
                int threadCount = Runtime.getRuntime().availableProcessors();
                sortContext.setStrategy(new MergeSort<Person>(threadCount));
                System.out.println("Выбрана параллельная сортировка слиянием");
                break;
        }
    }

    private void testSearch() {
        if (persons.isEmpty()) {
            System.out.println("Данные не загружены");
            return;
        }

        System.out.println("\n=== ПОИСК ===");
        System.out.println("1. По имени");
        System.out.println("2. По возрасту");
        System.out.println("3. По оценке");

        int fieldChoice = getIntInput("Выберите поле: ");
        Comparator<Person> comparator = getComparator(fieldChoice);

        if (comparator == null) return;

        List<Person> sortedList = new ArrayList<>(persons);


        int threadCount = Runtime.getRuntime().availableProcessors();
        sortContext.setStrategy(new MergeSort<Person>(threadCount));
        sortContext.executeSort(sortedList, comparator);

        System.out.print("Введите значение для поиска: ");
        scanner.nextLine();
        String searchValue = scanner.nextLine();

        Person target = createTargetPerson(fieldChoice, searchValue);
        if (target == null) return;

        int index = binarySearch.search(sortedList, target, comparator);

        List<Person> searchResult = new ArrayList<>();
        if (index != -1) {
            Person foundPerson = sortedList.get(index);
            searchResult.add(foundPerson);
            System.out.println("Найдено: " + foundPerson);
        } else {
            System.out.println("Не найдено");
        }

        String fieldName = getFieldNameForExport(fieldChoice);
        String operationType = "Поиск_по_" + fieldName + "_значение_" + searchValue;

        ExportManager.offerExport(searchResult, operationType, scanner);
    }

    private void viewPersonList() {
        if (persons.isEmpty()) {
            System.out.println("Список пуст");
            return;
        }

        System.out.println("\n=== ВЕСЬ СПИСОК ===");
        showList(persons);

        System.out.println("\nНажмите Enter для продолжения...");
        scanner.nextLine();
        scanner.nextLine();
    }

    private int getIntInput(String message) {
        System.out.print(message);
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Ошибка ввода");
            scanner.nextLine();
            return -1;
        }
    }

    private String getStringInput(String message) {
        System.out.print(message);
        return scanner.next();
    }

    private List<Person> getPersonsFromInputUtils() {
        List<Person> result = new ArrayList<>();
        for (int i = 0; i < inputUtils.size(); i++) {
            result.add(inputUtils.get(i));
        }
        return result;
    }

    private void showList(List<Person> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + list.get(i));
        }
    }

    private Comparator<Person> getComparator(int field) {
        switch (field) {
            case 1:
                return Comparator.comparing(Person::getName);
            case 2:
                return Comparator.comparingInt(Person::getAge);
            case 3:
                return Comparator.comparingInt(Person::getScore);
            default:
                return null;
        }
    }

    private Person createTargetPerson(int field, String value) {
        try {
            switch (field) {
                case 1:
                    return Person.builder().name(value).age(0).score(0).build();
                case 2:
                    return Person.builder().name("").age(Integer.parseInt(value)).score(0).build();
                case 3:
                    return Person.builder().name("").age(0).score(Integer.parseInt(value)).build();
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число для возраста/оценки");
            return null;
        }
    }

    private String getAlgorithmName(int algorithmChoice) {
        switch (algorithmChoice) {
            case 1:
                return "пузырьковая";
            case 2:
                return "вставками";
            case 3:
                return "слиянием";
            case 4:
                return "слиянием_по_числовому_полю";
            default:
                return "неизвестная";
        }
    }

    private String getFieldNameForExport(int fieldChoice) {
        switch (fieldChoice) {
            case 1:
                return "имени";
            case 2:
                return "возрасту";
            case 3:
                return "оценке";
            default:
                return "неизвестному_полю";
        }
    }
}
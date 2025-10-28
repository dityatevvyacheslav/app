package org.example.tests;

import org.example.app.Application;
import org.example.model.Person;
import org.example.searching.BinarySearch;
import org.example.sorting.*;
import org.example.utils.InputUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class FeatureApplicationCoreTest {

    private Application application;

    @BeforeEach
    void setUp() {
        application = new Application();
    }


    @Test
    @DisplayName("Выбор компаратора по имени — возвращает корректный компаратор")
    void testGetComparatorForName() throws Exception {
        Method getComparatorMethod = Application.class.getDeclaredMethod("getComparator", int.class);
        getComparatorMethod.setAccessible(true);

        Comparator<Person> comparator = (Comparator<Person>) getComparatorMethod.invoke(application, 1);
        assertNotNull(comparator);

        Person p1 = Person.builder().name("Alice").age(25).score(80).build();
        Person p2 = Person.builder().name("Bob").age(30).score(90).build();

        assertTrue(comparator.compare(p1, p2) < 0);
    }



    @Test
    @DisplayName("Создание целевого объекта для поиска по имени — корректные значения")
    void testCreateTargetPersonForName() throws Exception {
        Method createTargetPersonMethod = Application.class.getDeclaredMethod("createTargetPerson", int.class, String.class);
        createTargetPersonMethod.setAccessible(true);

        Person target = (Person) createTargetPersonMethod.invoke(application, 1, "TestName");

        assertEquals("TestName", target.getName());
        assertEquals(0, target.getAge());
        assertEquals(0, target.getScore());
    }

    @Test
    @DisplayName("Создание целевого объекта для поиска по возрасту — корректные значения")
    void testCreateTargetPersonForAge() throws Exception {
        Method createTargetPersonMethod = Application.class.getDeclaredMethod("createTargetPerson", int.class, String.class);
        createTargetPersonMethod.setAccessible(true);

        Person target = (Person) createTargetPersonMethod.invoke(application, 2, "25");

        assertEquals("", target.getName());
        assertEquals(25, target.getAge());
        assertEquals(0, target.getScore());
    }

    @Test
    @DisplayName("Создание целевого объекта для поиска по оценке — корректные значения")
    void testCreateTargetPersonForScore() throws Exception {
        Method createTargetPersonMethod = Application.class.getDeclaredMethod("createTargetPerson", int.class, String.class);
        createTargetPersonMethod.setAccessible(true);

        Person target = (Person) createTargetPersonMethod.invoke(application, 3, "85");

        assertEquals("", target.getName());
        assertEquals(0, target.getAge());
        assertEquals(85, target.getScore());
    }

    @Test
    @DisplayName("Создание целевого объекта с неверным номером поля — возвращает null")
    void testCreateTargetPersonInvalidField() throws Exception {
        Method createTargetPersonMethod = Application.class.getDeclaredMethod("createTargetPerson", int.class, String.class);
        createTargetPersonMethod.setAccessible(true);

        Person target = (Person) createTargetPersonMethod.invoke(application, 99, "test");
        assertNull(target);
    }


    @Test
    @DisplayName("Установка стратегии пузырьковой сортировки — корректно устанавливается")
    void testSetBubbleSortStrategy() throws Exception {
        Method setSortStrategyMethod = Application.class.getDeclaredMethod("setSortStrategy", int.class);
        setSortStrategyMethod.setAccessible(true);

        Field sortContextField = Application.class.getDeclaredField("sortContext");
        sortContextField.setAccessible(true);
        SortContext<Person> sortContext = (SortContext<Person>) sortContextField.get(application);

        setSortStrategyMethod.invoke(application, 1);

        List<Person> persons = Arrays.asList(
                Person.builder().name("Bob").age(30).score(90).build(),
                Person.builder().name("Alice").age(25).score(80).build()
        );

        assertDoesNotThrow(() -> sortContext.executeSort(persons, Comparator.comparing(Person::getName)));
    }


    @Test
    @DisplayName("Установка стратегии сортировки вставками — корректно устанавливается")
    void testSetInsertionSortStrategy() throws Exception {
        Method setSortStrategyMethod = Application.class.getDeclaredMethod("setSortStrategy", int.class);
        setSortStrategyMethod.setAccessible(true);

        Field sortContextField = Application.class.getDeclaredField("sortContext");
        sortContextField.setAccessible(true);
        SortContext<Person> sortContext = (SortContext<Person>) sortContextField.get(application);

        setSortStrategyMethod.invoke(application, 2);

        List<Person> persons = Arrays.asList(
                Person.builder().name("Bob").age(30).score(90).build(),
                Person.builder().name("Alice").age(25).score(80).build()
        );

        assertDoesNotThrow(() -> sortContext.executeSort(persons, Comparator.comparing(Person::getName)));
    }

    @Test
    @DisplayName("Установка стратегии параллельной сортировки слиянием — корректно устанавливается")
    void testSetMergeSortStrategy() throws Exception {
        Method setSortStrategyMethod = Application.class.getDeclaredMethod("setSortStrategy", int.class);
        setSortStrategyMethod.setAccessible(true);

        Field sortContextField = Application.class.getDeclaredField("sortContext");
        sortContextField.setAccessible(true);
        SortContext<Person> sortContext = (SortContext<Person>) sortContextField.get(application);

        setSortStrategyMethod.invoke(application, 3);

        List<Person> persons = Arrays.asList(
                Person.builder().name("Bob").age(30).score(90).build(),
                Person.builder().name("Alice").age(25).score(80).build(),
                Person.builder().name("Charlie").age(35).score(95).build()
        );

        assertDoesNotThrow(() -> sortContext.executeSort(persons, Comparator.comparing(Person::getName)));
    }

    @Test
    @DisplayName("Установка стратегии слиянием по числовому полю — метод не падает")
    void testSetMergeSortNumericFieldStrategyNoException() throws Exception {
        Method setSortStrategyMethod = Application.class.getDeclaredMethod("setSortStrategy", int.class);
        setSortStrategyMethod.setAccessible(true);

        assertDoesNotThrow(() -> setSortStrategyMethod.invoke(application, 4));
    }

    @Test
    @DisplayName("Установка неверной стратегии сортировки — метод не падает")
    void testSetInvalidSortStrategy() throws Exception {
        Method setSortStrategyMethod = Application.class.getDeclaredMethod("setSortStrategy", int.class);
        setSortStrategyMethod.setAccessible(true);

        assertDoesNotThrow(() -> setSortStrategyMethod.invoke(application, 99));
    }


    @Test
    @DisplayName("Бинарный поиск по имени — элемент найден")
    void testBinarySearchByNameFound() throws Exception {
        List<Person> persons = Arrays.asList(
                Person.builder().name("Alice").age(25).score(80).build(),
                Person.builder().name("Bob").age(30).score(90).build(),
                Person.builder().name("Charlie").age(35).score(95).build()
        );

        Method getComparatorMethod = Application.class.getDeclaredMethod("getComparator", int.class);
        getComparatorMethod.setAccessible(true);
        Comparator<Person> comparator = (Comparator<Person>) getComparatorMethod.invoke(application, 1);

        Method createTargetPersonMethod = Application.class.getDeclaredMethod("createTargetPerson", int.class, String.class);
        createTargetPersonMethod.setAccessible(true);
        Person target = (Person) createTargetPersonMethod.invoke(application, 1, "Bob");

        BinarySearch<Person> binarySearch = new BinarySearch<>();
        int result = binarySearch.search(persons, target, comparator);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("Бинарный поиск по возрасту — элемент не найден")
    void testBinarySearchByAgeNotFound() throws Exception {
        List<Person> persons = Arrays.asList(
                Person.builder().name("Alice").age(25).score(80).build(),
                Person.builder().name("Bob").age(30).score(90).build()
        );

        Method getComparatorMethod = Application.class.getDeclaredMethod("getComparator", int.class);
        getComparatorMethod.setAccessible(true);
        Comparator<Person> comparator = (Comparator<Person>) getComparatorMethod.invoke(application, 2);

        Method createTargetPersonMethod = Application.class.getDeclaredMethod("createTargetPerson", int.class, String.class);
        createTargetPersonMethod.setAccessible(true);
        Person target = (Person) createTargetPersonMethod.invoke(application, 2, "35");

        BinarySearch<Person> binarySearch = new BinarySearch<>();
        int result = binarySearch.search(persons, target, comparator);

        assertEquals(-1, result);
    }

    @Test
    @DisplayName("Бинарный поиск по оценке — находит правильный элемент")
    void testBinarySearchByScoreFound() throws Exception {
        List<Person> persons = Arrays.asList(
                Person.builder().name("Alice").age(25).score(80).build(),
                Person.builder().name("Bob").age(30).score(90).build(),
                Person.builder().name("Charlie").age(35).score(95).build()
        );

        Method getComparatorMethod = Application.class.getDeclaredMethod("getComparator", int.class);
        getComparatorMethod.setAccessible(true);
        Comparator<Person> comparator = (Comparator<Person>) getComparatorMethod.invoke(application, 3);

        Method createTargetPersonMethod = Application.class.getDeclaredMethod("createTargetPerson", int.class, String.class);
        createTargetPersonMethod.setAccessible(true);
        Person target = (Person) createTargetPersonMethod.invoke(application, 3, "90");

        BinarySearch<Person> binarySearch = new BinarySearch<>();
        int result = binarySearch.search(persons, target, comparator);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("Получение данных из InputUtils — возвращает корректный список")
    void testGetPersonsFromInputUtils() throws Exception {
        Field inputUtilsField = Application.class.getDeclaredField("inputUtils");
        inputUtilsField.setAccessible(true);
        InputUtils inputUtils = (InputUtils) inputUtilsField.get(application);

        List<Person> testData = Arrays.asList(
                Person.builder().name("Test1").age(20).score(75).build(),
                Person.builder().name("Test2").age(25).score(85).build()
        );

        Field personsField = inputUtils.getClass().getDeclaredField("persons");
        personsField.setAccessible(true);
        personsField.set(inputUtils, new ArrayList<>(testData));

        Method getPersonsMethod = Application.class.getDeclaredMethod("getPersonsFromInputUtils");
        getPersonsMethod.setAccessible(true);

        List<Person> result = (List<Person>) getPersonsMethod.invoke(application);

        assertEquals(2, result.size());
        assertEquals("Test1", result.get(0).getName());
        assertEquals("Test2", result.get(1).getName());
    }

    @Test
    @DisplayName("Отображение списка persons — не падает на пустом списке")
    void testShowListWithEmptyList() throws Exception {
        Method showListMethod = Application.class.getDeclaredMethod("showList", List.class);
        showListMethod.setAccessible(true);

        List<Person> emptyList = new ArrayList<>();

        assertDoesNotThrow(() -> showListMethod.invoke(application, emptyList));
    }

    @Test
    @DisplayName("Отображение списка persons — корректно показывает данные")
    void testShowListWithData() throws Exception {
        Method showListMethod = Application.class.getDeclaredMethod("showList", List.class);
        showListMethod.setAccessible(true);

        List<Person> testData = Arrays.asList(
                Person.builder().name("Alice").age(25).score(80).build(),
                Person.builder().name("Bob").age(30).score(90).build()
        );

        assertDoesNotThrow(() -> showListMethod.invoke(application, testData));
    }


    @Test
    @DisplayName("Попытка сортировки с пустым списком — корректно обрабатывается")
    void testSortingWithEmptyList() throws Exception {
        Field personsField = Application.class.getDeclaredField("persons");
        personsField.setAccessible(true);
        personsField.set(application, new ArrayList<>());

        Method testSortingMethod = Application.class.getDeclaredMethod("testSorting");
        testSortingMethod.setAccessible(true);

        assertDoesNotThrow(() -> testSortingMethod.invoke(application));
    }

    @Test
    @DisplayName("Попытка поиска с пустым списком — корректно обрабатывается")
    void testSearchWithEmptyList() throws Exception {
        Field personsField = Application.class.getDeclaredField("persons");
        personsField.setAccessible(true);
        personsField.set(application, new ArrayList<>());

        Method testSearchMethod = Application.class.getDeclaredMethod("testSearch");
        testSearchMethod.setAccessible(true);

        assertDoesNotThrow(() -> testSearchMethod.invoke(application));
    }

    @Test
    @DisplayName("Просмотр пустого списка — корректно обрабатывается")
    void testViewEmptyPersonList() throws Exception {
        Field personsField = Application.class.getDeclaredField("persons");
        personsField.setAccessible(true);
        personsField.set(application, new ArrayList<>());

        Method viewPersonListMethod = Application.class.getDeclaredMethod("viewPersonList");
        viewPersonListMethod.setAccessible(true);

        assertDoesNotThrow(() -> viewPersonListMethod.invoke(application));
    }


    @Test
    @DisplayName("Получение имени алгоритма — корректные значения для всех вариантов")
    void testGetAlgorithmName() throws Exception {
        Method getAlgorithmNameMethod = Application.class.getDeclaredMethod("getAlgorithmName", int.class);
        getAlgorithmNameMethod.setAccessible(true);

        assertEquals("пузырьковая", getAlgorithmNameMethod.invoke(application, 1));
        assertEquals("вставками", getAlgorithmNameMethod.invoke(application, 2));
        assertEquals("слиянием", getAlgorithmNameMethod.invoke(application, 3));
        assertEquals("слиянием_по_числовому_полю", getAlgorithmNameMethod.invoke(application, 4));
        assertEquals("неизвестная", getAlgorithmNameMethod.invoke(application, 99));
    }

    @Test
    @DisplayName("Получение имени поля для экспорта — корректные значения для всех вариантов")
    void testGetFieldNameForExport() throws Exception {
        Method getFieldNameMethod = Application.class.getDeclaredMethod("getFieldNameForExport", int.class);
        getFieldNameMethod.setAccessible(true);

        assertEquals("имени", getFieldNameMethod.invoke(application, 1));
        assertEquals("возрасту", getFieldNameMethod.invoke(application, 2));
        assertEquals("оценке", getFieldNameMethod.invoke(application, 3));
        assertEquals("неизвестному_полю", getFieldNameMethod.invoke(application, 99));
    }
}
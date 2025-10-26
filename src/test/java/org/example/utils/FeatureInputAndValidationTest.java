package org.example.utils;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FeatureInputAndValidationTest {

    @TempDir
    Path tempDir;

    @Test
    void testPersonNotNullValidator() {
        Validator<Person> validator = PersonValidators.notNull();

        assertNull(validator.validate(Person.builder().name("Test").age(25).score(80).build()));
        assertEquals("Ошибка: объект Person не может быть null.", validator.validate(null));
    }

    @Test
    void testPersonNameValidator() {
        Validator<Person> validator = PersonValidators.name();
        Person validPerson = Person.builder().name("John").age(25).score(80).build();
        Person emptyNamePerson = Person.builder().name("").age(25).score(80).build();
        Person nullNamePerson = Person.builder().name(null).age(25).score(80).build();

        assertNull(validator.validate(validPerson));
        assertEquals("Ошибка: имя не может быть пустым.", validator.validate(emptyNamePerson));
        assertEquals("Ошибка: имя не может быть пустым.", validator.validate(nullNamePerson));
    }

    @Test
    void testPersonAgeValidator() {
        Validator<Person> validator = PersonValidators.age();
        Person validPerson = Person.builder().name("John").age(25).score(80).build();
        Person negativeAgePerson = Person.builder().name("John").age(-1).score(80).build();
        Person tooOldPerson = Person.builder().name("John").age(151).score(80).build();

        assertNull(validator.validate(validPerson));
        assertEquals("Ошибка: возраст должен быть от 0 до 150 лет.", validator.validate(negativeAgePerson));
        assertEquals("Ошибка: возраст должен быть от 0 до 150 лет.", validator.validate(tooOldPerson));
    }

    @Test
    void testPersonScoreValidator() {
        Validator<Person> validator = PersonValidators.score();
        Person validPerson = Person.builder().name("John").age(25).score(80).build();
        Person negativeScorePerson = Person.builder().name("John").age(25).score(-1).build();
        Person tooHighScorePerson = Person.builder().name("John").age(25).score(101).build();

        assertNull(validator.validate(validPerson));
        assertEquals("Ошибка: оценка должна быть от 0 до 100.", validator.validate(negativeScorePerson));
        assertEquals("Ошибка: оценка должна быть от 0 до 100.", validator.validate(tooHighScorePerson));
    }

    @Test
    void testFullPersonValidator() {
        Validator<Person> validator = PersonValidators.fullPersonValidator();

        Person validPerson = Person.builder().name("John").age(25).score(80).build();
        assertNull(validator.validate(validPerson));

        Person invalidName = Person.builder().name("").age(25).score(80).build();
        assertEquals("Ошибка: имя не может быть пустым.", validator.validate(invalidName));

        Person invalidAge = Person.builder().name("John").age(-1).score(80).build();
        assertEquals("Ошибка: возраст должен быть от 0 до 150 лет.", validator.validate(invalidAge));

        Person invalidScore = Person.builder().name("John").age(25).score(101).build();
        assertEquals("Ошибка: оценка должна быть от 0 до 100.", validator.validate(invalidScore));
    }

    @Test
    void testFileNameValidator() {
        Validator<String> validator = FileValidators.fileName();

        assertNull(validator.validate("test.txt"));
        assertEquals("Ошибка: имя файла не может быть пустым.", validator.validate(""));
        assertEquals("Ошибка: имя файла не может быть пустым.", validator.validate(null));
    }

    @Test
    void testFileExistsValidator() throws Exception {
        Validator<String> validator = FileValidators.fileExists();

        Path testFile = tempDir.resolve("test.txt");
        java.nio.file.Files.writeString(testFile, "test content");

        assertNull(validator.validate(testFile.toString()));
        assertEquals("Ошибка: файл 'nonexistent.txt' не найден.", validator.validate("nonexistent.txt"));
    }

    @Test
    void testFileForLoadingValidator() {
        Validator<String> validator = FileValidators.fileForLoading();

        assertEquals("Ошибка: имя файла не может быть пустым.", validator.validate(""));
        assertEquals("Ошибка: имя файла не может быть пустым.", validator.validate(null));
    }

    @Test
    void testLineFormatValidator() {
        Validator<String> validator = DataLineValidators.lineFormat(1);

        assertNull(validator.validate("John,25,80"));
        assertEquals("Пустая строка 1", validator.validate(""));
        assertEquals("Пустая строка 1", validator.validate(null));
        assertEquals("Неверный формат данных в строке 1 (ожидается: имя,возраст,оценка)",
                validator.validate("John,25"));
        assertEquals("Неверный формат данных в строке 1 (ожидается: имя,возраст,оценка)",
                validator.validate("John,25,80,extra"));
    }

    @Test
    void testLineDataValidator() {
        Validator<String> validator = DataLineValidators.lineData(1);

        assertNull(validator.validate("John,25,80"));
        assertEquals("Ошибка в строке 1: возраст должен быть от 0 до 150 лет.",
                validator.validate("John,-1,80"));
        assertEquals("Ошибка в строке 1: оценка должна быть от 0 до 100.",
                validator.validate("John,25,101"));
        assertEquals("Ошибка формата чисел в строке 1",
                validator.validate("John,invalid,80"));
        assertEquals("Ошибка формата чисел в строке 1",
                validator.validate("John,25,invalid"));
    }

    @Test
    void testFullLineValidator() {
        Validator<String> validator = DataLineValidators.fullLineValidator(1);

        assertNull(validator.validate("John,25,80"));
        assertEquals("Пустая строка 1", validator.validate(""));
        assertEquals("Неверный формат данных в строке 1 (ожидается: имя,возраст,оценка)",
                validator.validate("John,25"));
        assertEquals("Ошибка в строке 1: возраст должен быть от 0 до 150 лет.",
                validator.validate("John,-1,80"));
    }

    @Test
    void testChainWithMultipleValidators() {
        Validator<String> notEmpty = value ->
                value == null || value.isEmpty() ? "Empty" : null;
        Validator<String> minLength = value ->
                value.length() < 3 ? "Too short" : null;

        Validator<String> chain = ValidationChains.chain(notEmpty, minLength);

        assertNull(chain.validate("Hello"));
        assertEquals("Empty", chain.validate(""));
        assertEquals("Too short", chain.validate("Hi"));
    }

    @Test
    void testChainStopsOnFirstError() {
        Validator<String> first = value -> "First error";
        Validator<String> second = value -> "Second error";

        Validator<String> chain = ValidationChains.chain(first, second);

        assertEquals("First error", chain.validate("test"));
    }

    @Test
    void testValidatePerson() {
        SimpleValidationManager manager = new SimpleValidationManager();

        Person validPerson = Person.builder().name("John").age(25).score(80).build();
        Person invalidPerson = Person.builder().name("").age(25).score(80).build();

        assertNull(manager.validatePerson(validPerson));
        assertEquals("Ошибка: имя не может быть пустым.", manager.validatePerson(invalidPerson));
    }

    @Test
    void testValidateArrayLength() {
        SimpleValidationManager manager = new SimpleValidationManager();

        assertNull(manager.validateArrayLength(5));
        assertEquals("Ошибка: длина массива должна быть положительным числом.",
                manager.validateArrayLength(0));
        assertEquals("Ошибка: длина массива должна быть положительным числом.",
                manager.validateArrayLength(-1));
    }

    @Test
    void testValidateString() {
        SimpleValidationManager manager = new SimpleValidationManager();

        assertNull(manager.validateString("test", "field"));
        assertEquals("Ошибка: field не может быть пустым.",
                manager.validateString("", "field"));
        assertEquals("Ошибка: field не может быть пустым.",
                manager.validateString(null, "field"));
    }

    @Test
    void testValidateRange() {
        SimpleValidationManager manager = new SimpleValidationManager();

        assertNull(manager.validateRange(5, 0, 10, "value"));
        assertEquals("Ошибка: value должен быть от 0 до 10.",
                manager.validateRange(-1, 0, 10, "value"));
        assertEquals("Ошибка: value должен быть от 0 до 10.",
                manager.validateRange(11, 0, 10, "value"));
    }

    @Test
    void testGetValidatedPersons() {
        SimpleValidationManager manager = new SimpleValidationManager();

        Person person1 = Person.builder().name("John").age(25).score(80).build();
        Person person2 = Person.builder().name("Alice").age(30).score(90).build();

        manager.validatePerson(person1);
        manager.validatePerson(person2);

        List<Person> validated = manager.getValidatedPersons();
        assertEquals(2, validated.size());
        assertEquals("John", validated.get(0).getName());
        assertEquals("Alice", validated.get(1).getName());
    }

    @Test
    void testCompleteValidationFlow() {
        SimpleValidationManager manager = new SimpleValidationManager();

        Person validPerson = Person.builder().name("Valid User").age(25).score(85).build();
        assertNull(manager.validatePerson(validPerson));

        Person invalidPerson = Person.builder().name("").age(200).score(-5).build();
        String error = manager.validatePerson(invalidPerson);
        assertTrue(error.contains("имя не может быть пустым") ||
                error.contains("возраст должен быть от 0 до 150") ||
                error.contains("оценка должна быть от 0 до 100"));
    }

    @Test
    void testPersonBuilder() {
        Person person = Person.builder()
                .name("Test")
                .age(30)
                .score(75)
                .build();

        assertEquals("Test", person.getName());
        assertEquals(30, person.getAge());
        assertEquals(75, person.getScore());
    }

    @Test
    void testEdgeCases() {
        SimpleValidationManager manager = new SimpleValidationManager();

        Person minValid = Person.builder().name("A").age(0).score(0).build();
        assertNull(manager.validatePerson(minValid));

        Person maxValid = Person.builder().name("Valid").age(150).score(100).build();
        assertNull(manager.validatePerson(maxValid));

        Person tooOld = Person.builder().name("Test").age(151).score(50).build();
        assertEquals("Ошибка: возраст должен быть от 0 до 150 лет.", manager.validatePerson(tooOld));

        Person tooHighScore = Person.builder().name("Test").age(50).score(101).build();
        assertEquals("Ошибка: оценка должна быть от 0 до 100.", manager.validatePerson(tooHighScore));
    }
}
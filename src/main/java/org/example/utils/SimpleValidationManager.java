package src.main.java.org.example.utils;

import java.util.List;

public class SimpleValidationManager {

    public final Validator<Person> personValidator = PersonValidators.fullPersonValidator();
    public final Validator<String> fileValidator = FileValidators.fileForLoading();

    public String validatePerson(Person person) {
        return personValidator.validate(person);
    }

    public String validateFile(String fileName) {
        return fileValidator.validate(fileName);
    }

    public String validateDataLine(String line, int lineNumber) {
        return DataLineValidators.fullLineValidator(lineNumber).validate(line);
    }

    public String validateArrayLength(int length) {
        return length <= 0 ? "Ошибка: длина массива должна быть положительным числом." : null;
    }

    public boolean validateAllPersons(List<Person> persons) {
        boolean allValid = true;
        for (int i = 0; i < persons.size(); i++) {
            String error = validatePerson(persons.get(i));
            if (error != null) {
                System.out.println("Ошибка в записи " + (i + 1) + ": " + error);
                allValid = false;
            }
        }
        return allValid;
    }

    public String validateString(String value, String fieldName) {
        return value == null || value.trim().isEmpty()
                ? "Ошибка: " + fieldName + " не может быть пустым." : null;
    }

    public String validateRange(int value, int min, int max, String fieldName) {
        return value < min || value > max
                ? String.format("Ошибка: %s должен быть от %d до %d.", fieldName, min, max) : null;
    }
}
package org.example.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import org.example.model.Person;

public class SimpleValidationManager {

    private final Validator<Person> personValidator;
    private final Validator<String> fileValidator;
    private final List<Person> validatedPersons = Collections.synchronizedList(new ArrayList<>());

    public SimpleValidationManager() {
        this.personValidator = PersonValidators.fullPersonValidator();
        this.fileValidator = FileValidators.fileForLoading();
    }

    public synchronized String validatePerson(Person person) {
        String result = personValidator.validate(person);
        if (result == null) {
            validatedPersons.add(person);
        }
        return result;
    }

    public synchronized String validateFile(String fileName) {
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

    public synchronized List<Person> getValidatedPersons() {
        return new ArrayList<>(validatedPersons);
    }
}
package src.main.java.org.example.utils;

public class NameValidator implements ValidationUtils {

    @Override
    public boolean validate(Person person) {
        return person.getName() != null && !person.getName().trim().isEmpty();
    }

    @Override
    public String getErrorMessage() {
        return "Ошибка: имя не может быть пустым.";
    }
}

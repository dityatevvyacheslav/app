package src.main.java.org.example.utils;

public class AgeValidator implements ValidationUtils {
    @Override
    public boolean validate(Person person) {
        return person.getAge() >= 0 && person.getAge() <= 150;
    }

    @Override
    public String getErrorMessage() {
        return "Ошибка: возраст должен быть от 0 до 150 лет.";
    }
}

package src.main.java.org.example.utils;

public class ScoreValidator implements ValidationUtils {
    @Override
    public boolean validate(Person person) {
        return person.getScore() >= 0 && person.getScore() <= 100;
    }

    @Override
    public String getErrorMessage() {
        return "Ошибка: оценка должна быть от 0 до 100.";
    }
}

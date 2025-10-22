package src.main.java.org.example.utils;

public class DataLineValidators {

    public static Validator<String> lineFormat(int lineNumber) {
        return line -> {
            if (line == null || line.trim().isEmpty()) {
                return "Пустая строка " + lineNumber;
            }

            String[] parts = line.split(",");
            if (parts.length != 3) {
                return "Неверный формат данных в строке " + lineNumber + " (ожидается: имя,возраст,оценка)";
            }

            return null;
        };
    }

    public static Validator<String> lineData(int lineNumber) {
        return line -> {
            String[] parts = line.split(",");

            try {
                int age = Integer.parseInt(parts[1].trim());
                if (age < 0 || age > 150) {
                    return "Ошибка в строке " + lineNumber + ": возраст должен быть от 0 до 150 лет.";
                }

                int score = Integer.parseInt(parts[2].trim());
                if (score < 0 || score > 100) {
                    return "Ошибка в строке " + lineNumber + ": оценка должна быть от 0 до 100.";
                }

                return null;

            } catch (NumberFormatException e) {
                return "Ошибка формата чисел в строке " + lineNumber;
            }
        };
    }

    public static Validator<String> fullLineValidator(int lineNumber) {
        return ValidationChains.chain(lineFormat(lineNumber), lineData(lineNumber));
    }
}
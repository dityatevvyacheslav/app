package src.main.java.org.example.utils;


public class PersonValidators {

    public static Validator<Person> notNull() {
        return person -> person == null ? "Ошибка: объект Person не может быть null." : null;
    }

    public static Validator<Person> name() {
        return person -> {
            String name = person.getName();
            return name == null || name.trim().isEmpty() ? "Ошибка: имя не может быть пустым." : null;
        };
    }

    public static Validator<Person> age() {
        return person -> {
            int age = person.getAge();
            return age < 0 || age > 150 ? "Ошибка: возраст должен быть от 0 до 150 лет." : null;
        };
    }

    public static Validator<Person> score() {
        return person -> {
            int score = person.getScore();
            return score < 0 || score > 100 ? "Ошибка: оценка должна быть от 0 до 100." : null;
        };
    }

    public static Validator<Person> fullPersonValidator() {
        return ValidationChains.chain(notNull(), name(), age(), score());
    }
}
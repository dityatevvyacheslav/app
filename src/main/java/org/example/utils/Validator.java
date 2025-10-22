package src.main.java.org.example.utils;

public interface Validator<T> {
    String validate(T value);
}
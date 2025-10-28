package org.example.utils;

public interface Validator<T> {
    String validate(T value);
}
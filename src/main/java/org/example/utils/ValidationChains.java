package org.example.utils;

public class ValidationChains {

    @SafeVarargs
    public static <T> Validator<T> chain(Validator<T>... validators) {
        return value -> {
            for (Validator<T> validator : validators) {
                String error = validator.validate(value);
                if (error != null) {
                    return error;
                }
            }
            return null;
        };
    }
}
package src.main.java.org.example.utils;

import java.nio.file.*;

public class FileValidators {

    public static Validator<String> fileName() {
        return fileName -> fileName == null || fileName.trim().isEmpty()
                ? "Ошибка: имя файла не может быть пустым." : null;
    }

    public static Validator<String> fileExists() {
        return fileName -> {
            try {
                Path filePath = Paths.get(fileName);

                if (!Files.exists(filePath)) {
                    return "Ошибка: файл '" + fileName + "' не найден.";
                }

                if (!Files.isReadable(filePath)) {
                    return "Ошибка: нет прав на чтение файла '" + fileName + "'.";
                }

                return null;

            } catch (Exception e) {
                return "Ошибка: неверный путь к файлу '" + fileName + "'.";
            }
        };
    }

    public static Validator<String> fileForLoading() {
        return ValidationChains.chain(fileName(), fileExists());
    }
}
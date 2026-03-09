package ru.yandex.practicum;

public class WordleDictionaryLoaderException extends RuntimeException {
    public WordleDictionaryLoaderException(String message) {
        super(message);
    }

    public WordleDictionaryLoaderException(String message, Throwable exception) {
        super(message, exception);
    }
}

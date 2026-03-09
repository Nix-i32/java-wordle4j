package ru.yandex.practicum;

public class WordleException extends RuntimeException {
    public WordleException(String message) {
        super(message);
    }

    public WordleException(String message, Throwable exception) {
        super(message, exception);
    }

}

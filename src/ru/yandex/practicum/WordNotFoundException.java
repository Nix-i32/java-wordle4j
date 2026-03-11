package ru.yandex.practicum;

public class WordNotFoundException extends WordleException {
    public WordNotFoundException(String message) {
        super(message);
    }

    public WordNotFoundException(String message, Throwable exception) {
        super(message, exception);
    }

}

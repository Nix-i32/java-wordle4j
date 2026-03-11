package ru.yandex.practicum;

public class LogFileException extends RuntimeException {
    public LogFileException(String message) {
        super(message);
    }

    public LogFileException(String message, Throwable exception) {
        super(message, exception);
    }

}

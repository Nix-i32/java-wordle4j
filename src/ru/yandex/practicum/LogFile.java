package ru.yandex.practicum;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class LogFile {
    private String path;

    public LogFile(Scanner scanner) {
        this("logFile.txt", scanner);
    }

    public LogFile(String path, Scanner scanner) throws LogFileException {
        this.path = path;
        createLogFile(toTxt(path), scanner);
    }

    private void createLogFile(String path, Scanner scannerFromConstructor) throws LogFileException {
        int tries = 3;

        while (tries > 0) {
            try {
                Path pathFile;
                Path parent;
                try {
                    pathFile = Paths.get(path);
                } catch (InvalidPathException exception) {
                    throw new LogFileException("Путь содержит некорректные символы: " + path, exception);
                }

                if (Files.exists(pathFile)) {
                    this.path = path;
                    return;
                }

                parent = pathFile.getParent();
                if (parent != null && !Files.exists(parent)) {
                    try {
                        Files.createDirectories(parent);
                    } catch (AccessDeniedException exception) {
                        throw new LogFileException("Нет прав на создание директории: " + parent, exception);
                    } catch (IOException exception) {
                        throw new LogFileException("Не удалось создать директорию: " + parent, exception);
                    }
                }

                try {
                    Files.createFile(pathFile);
                } catch (AccessDeniedException exception) {
                    throw new LogFileException("Нет прав на создание файла: " + pathFile, exception);
                } catch (IOException exception) {
                    throw new LogFileException("Не удалось создать файл: " + pathFile, exception);
                }

                this.path = path;
                return;

            } catch (LogFileException exception) {
                tries--;

                if (tries == 0) {
                    throw exception;
                }

                System.out.printf("%s%nВведите новый путь для создания файла или попробуйте тот же. " +
                        "Для использования того же пути нажмите Enter%n" +
                        "Путь: ", exception.getMessage());

                String newFilePath = scannerFromConstructor.nextLine();
                if (!newFilePath.isBlank()) {
                    path = toTxt(newFilePath);
                }
                System.out.println();
            }
        }
    }


    public void addToLog(Throwable exception) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8, true))) {
            writer.newLine();
            String head = String.format("[%s]: %s. %s. Полная информация:", LocalDateTime.now(),
                    exception.getClass().getSimpleName(),
                    exception.getMessage() != null ? exception.getMessage() : "Нет описания");
            writer.write(head);
            writer.newLine();
            for (StackTraceElement element : exception.getStackTrace()) {
                writer.write(String.format("%20s%s", "", element.toString()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Не удалось добавить ошибку в log");
        }
    }

    private String toTxt(String path) {
        String pathFile = path;
        int lastSlash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        int lastDot = path.lastIndexOf('.');
        if (lastDot <= lastSlash) {
            pathFile += ".txt";
        }
        return pathFile;
    }

    public String getPath() {
        return path;
    }
}

package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.nio.file.Files;


import static org.junit.jupiter.api.Assertions.*;

public class LogFileTest {

    private Scanner scanner;

    @BeforeEach
    void setUp() {
        scanner = new Scanner("\n\n");
    }

    @TempDir
    Path tempDir;


    @Test
    void creationLogFileInUnrealPath() {
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            assertThrows(LogFileException.class, () ->
                    new LogFile("KASD&a\\u/////0000 sd?", scanner));
        }
    }

    @Test
    void logFileCreation() {
        Path logPath = tempDir.resolve("logFile1");
        LogFile logFile = new LogFile(logPath.toString(), scanner);
        assertTrue(Files.exists(Path.of(logFile.getPath())), "Файл должен существовать");
    }

    @Test
    void logAddCheck() {
        Path logPath = tempDir.resolve("logFile1");
        LogFile logFile = new LogFile(logPath.toString(), scanner);
        WordNotFoundException wordNotFoundException = new WordNotFoundException("Тестовая ошибка");
        logFile.addToLog(wordNotFoundException);

        long size;
        try {
            size = Files.size(Paths.get(logFile.getPath()));
        } catch (IOException exception) {
            fail("Не удалось прочитать размер файла");
            return;
        }
        assertTrue(size > 0, "Файл не должен быть пуст");

        String filesText = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile.getPath(), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                filesText += line;
            }
        } catch (IOException exception) {
            fail("Не должно быть ошибки чтения файла");
        }

        assertTrue(filesText.contains("WordNotFoundException"));
        assertTrue(filesText.contains("Тестовая ошибка"));
    }


}

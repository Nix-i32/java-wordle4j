package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;


public class WordleDictionaryLoader {
    private static final String DEFAULT_DICTIONARY_FILE_PATH = "words_ru.txt";
    private static final int MAX_LOAD_ATTEMPTS = 3;
    private static final  int INITIAL_DICTIONARY_CAPACITY = 7000;
    private final Set<Word> allWords;
    private String path;
    private final LogFile logFile;
    private final Scanner scanner;

    public WordleDictionaryLoader(LogFile logFile, Scanner scanner) {
        this(DEFAULT_DICTIONARY_FILE_PATH, logFile, scanner);
    }

    public WordleDictionaryLoader(String path, LogFile logFile, Scanner scanner)
            throws WordleDictionaryLoaderException {
        allWords = new HashSet<>(INITIAL_DICTIONARY_CAPACITY);
        this.path = path;
        this.logFile = logFile;
        this.scanner = scanner;
        loadWordsFromFile();
    }

    private void loadWordsFromFile() throws WordleDictionaryLoaderException {
        int tries = MAX_LOAD_ATTEMPTS;
        Path filePath;
        while (tries > 0) {
            try {
                try {
                    filePath = Paths.get(path);
                } catch (InvalidPathException exception) {
                    throw new WordleDictionaryLoaderException(
                            "Путь содержит некорректные символы: " + path, exception);
                }

                if (!Files.exists(filePath)) throw new WordleDictionaryLoaderException(
                        String.format("Файла: %s - не существует", path));

                try {
                    if (Files.size(filePath) == 0) {
                        throw new WordleDictionaryLoaderException(String.format("Файл: %s - пуст", path));
                    }
                } catch (IOException exception) {
                    throw new WordleDictionaryLoaderException(
                            String.format("Не удалось прочитать размер файла. %s", path), exception);
                }

                try (BufferedReader reader = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim().toLowerCase();
                        if (line.matches("[а-яё]{5}")) {
                            allWords.add(new Word(line));
                        }
                    }
                } catch (AccessDeniedException exception) {
                    throw new WordleDictionaryLoaderException(String.format("Нет прав для чтения файла: %s", path),
                            exception);
                } catch (IOException exception) {
                    throw new WordleDictionaryLoaderException(String.format("Невозможно прочитать файл: %s", path),
                            exception);
                }

                if (allWords.isEmpty()) {
                    throw new WordleDictionaryLoaderException(String.format("Файл: %s - не содержит слов из 5 букв",
                            path));
                }
                return;
            } catch (WordleDictionaryLoaderException exception) {
                logFile.addToLog(exception);
                System.out.printf("Не удалось загрузить словарь. Подробности об ошибке в %s. " +
                        "Укажите путь к новому словарь или попробуйте тот же." +
                        " Нажмите Enter чтобы загрузить тот же файл %nФайл:", logFile.getPath());
                String newFilePath = scanner.nextLine();
                if (!newFilePath.isBlank()) {
                    this.path = newFilePath;
                }
                tries--;
                if (tries == 0) {
                    throw exception;
                }
            }
        }
    }

    public Set<Word> getAllWords() {
        return allWords;
    }
}

package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    private Scanner scanner;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        scanner = new Scanner("\n\n\n\n\n\n");
    }

    @Test
    void creationFileInUnrealPathAndCheckLog() {
        LogFile logFile = new LogFile(scanner);
        assertThrows(WordleDictionaryLoaderException.class, () ->
                new WordleDictionaryLoader("KASDASD:\\IdeaProjects\\java-wordle4j\\word.txt",
                        logFile, scanner));

        String filesText = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile.getPath(), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                filesText += line;
            }
        } catch (IOException exception) {
            fail("Не должно быть ошибки чтения файла");
        }

        assertTrue(filesText.contains("WordleDictionaryLoaderException"));

    }

    @Test
    void OnlyFiveLetterRussianWordsNoDuplicates() throws Exception {
        Path dictionaryFile = tempDir.resolve("words.txt");

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(dictionaryFile.toFile(),
                             StandardCharsets.UTF_8, true))) {
            writer.write("арбуз\n");
            writer.write("дом\n");
            writer.write("столы\n");
            writer.write("ёлкаа\n");
            writer.write("hello\n");
            writer.write("столы\n");
            writer.write("мир!!\n");
            writer.write("вишня\n");
        }

        LogFile logFile = new LogFile(tempDir.resolve("log.txt").toString(), scanner);
        WordleDictionaryLoader loader =
                new WordleDictionaryLoader(dictionaryFile.toString(), logFile, scanner);
        Set<Word> words = loader.getAllWords();

        assertEquals(4, words.size());
        assertTrue(words.contains(new Word("арбуз")));
        assertTrue(words.contains(new Word("ёлкаа")));
        assertTrue(words.contains(new Word("столы")));
        assertTrue(words.contains(new Word("вишня")));
    }

    @Test
    void emptyDictionary() {
        Path dictionaryFile = tempDir.resolve("words1.txt");
        LogFile logFile = new LogFile(tempDir.resolve("log.txt").toString(), scanner);

        assertThrows(WordleDictionaryLoaderException.class, () ->
                new WordleDictionaryLoader(dictionaryFile.toString(), logFile, scanner));

        String filesText = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile.getPath(), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                filesText += line;
            }
        } catch (IOException exception) {
            fail("Не должно быть ошибки чтения файла");
        }

        assertTrue(filesText.contains("WordleDictionaryLoaderException"));
    }
}
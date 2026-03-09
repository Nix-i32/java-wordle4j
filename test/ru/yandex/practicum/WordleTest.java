package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    private Scanner scanner;
    private WordleDictionaryLoader wordleDictionaryLoader;
    private LogFile logFile;
    private WordleDictionary wordleDictionary;

    String getLogText(LogFile logFile) {
        String filesText = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile.getPath(), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                filesText += line;
            }
        } catch (IOException exception) {
            fail("Не должно быть ошибки чтения файла");
        }
        return filesText;
    }

    @TempDir
    Path tempDir;


    @BeforeEach
    void setUp() {
        Path logFilePath = tempDir.resolve("logFIle1.txt");
        Path dictionaryFilePath = tempDir.resolve("word1.txt");
        try {
            Files.writeString(dictionaryFilePath, """
                    арбуз
                    столы
                    вишня
                    книга
                    аборт
                    амбар
                    """, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            fail("Ошибка записи временного файла");
        }
        scanner = new Scanner("\n\n\n\n\n\n");
        logFile = new LogFile(logFilePath.toString(), scanner);
        wordleDictionaryLoader = new WordleDictionaryLoader(dictionaryFilePath.toString(), logFile, scanner);
        wordleDictionary = new WordleDictionary(wordleDictionaryLoader, logFile);
    }

    @Test
    void incorrectInPut() {
        WordleGame wordle = new WordleGame(wordleDictionary, logFile);
        assertThrows(IllegalArgumentException.class, () ->
                wordle.correctInPut("asdas123"));
        assertThrows(IllegalArgumentException.class, () ->
                wordle.correctInPut("фывффывфывфыв"));
        assertThrows(IllegalArgumentException.class, () ->
                wordle.correctInPut("ма ма"));
        assertThrows(IllegalArgumentException.class, () ->
                wordle.correctInPut(""));
        assertThrows(IllegalArgumentException.class, () ->
                wordle.correctInPut("ма.ма"));
        assertThrows(WordleException.class, () ->
                wordle.correctInPut("лилия"));
    }


    @Test
    void checkCluesAndAttempts() {
        String wordToInput = "книга";
        wordleDictionary = new WordleDictionary(wordleDictionaryLoader, logFile, new Word("арбуз"));
        WordleGame wordle = new WordleGame(wordleDictionary, logFile);
        List<String> clues = new ArrayList<>();
        clues.add("арбуз");
        clues.add("аборт");
        clues.add("амбар");
        List<String> cluesFromGame = wordle.clue(wordToInput);
        assertEquals(clues, cluesFromGame);
        assertEquals(5, wordle.getAttempts());
    }

    @Test
    void checkStatus() {
        String wordToInput = "книга";
        wordleDictionary = new WordleDictionary(wordleDictionaryLoader, logFile, new Word("арбуз"));
        WordleGame wordle = new WordleGame(wordleDictionary, logFile);
        wordle.clue(wordToInput);
        assertEquals("----^", wordle.status(wordToInput));

        wordToInput = "амбар";
        wordle.clue(wordToInput);
        assertEquals("а_б__ р", wordle.status());
    }

    @Test
    void attemptsEmpty() {
        wordleDictionary = new WordleDictionary(wordleDictionaryLoader, logFile, new Word("арбуз"));
        WordleGame wordle = new WordleGame(wordleDictionary, logFile);
        wordle.clue("книга");
        wordle.clue("книга");
        wordle.clue("книга");
        wordle.clue("книга");
        wordle.clue("книга");
        wordle.clue("книга");
        assertThrows(AttemptsLeftException.class, () ->
                wordle.clue("книга"));
    }

    @Test
    void noClue() {
        wordleDictionary = new WordleDictionary(wordleDictionaryLoader, logFile, new Word("арбуз"));
        WordleGame wordle = new WordleGame(wordleDictionary, logFile);
        assertThrows(NoClueException.class, () ->
                wordle.clue("вишня"));
    }


}

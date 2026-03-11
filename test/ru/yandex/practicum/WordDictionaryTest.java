package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class WordDictionaryTest {
    private Scanner scanner;
    private WordleDictionaryLoader wordleDictionaryLoader;
    private LogFile logFile;

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
    }

    @Test
    void inDictionaryCheck() {
        WordleDictionary wordleDictionary = new WordleDictionary(wordleDictionaryLoader, logFile);
        assertTrue(wordleDictionary.isInDictionary("вишня"));
        assertFalse(wordleDictionary.isInDictionary("лилия"));
    }

    @Test
    void wordContainsLetters() {
        WordleDictionary wordleDictionary =
                new WordleDictionary(wordleDictionaryLoader, logFile, new Word("арбуз"));

        Map<WordCharacter, Set<Word>> wordsByLetterPos = new HashMap<>();

        Set<Word> letterA = new HashSet<>();
        letterA.add(new Word("арбуз"));
        letterA.add(new Word("аборт"));
        letterA.add(new Word("амбар"));
        wordsByLetterPos.put(new WordCharacter(0, 'а'), letterA);

        Set<Word> letterR = new HashSet<>();
        letterR.add(new Word("арбуз"));
        wordsByLetterPos.put(new WordCharacter(1, 'р'), letterR);

        Set<Word> letterB = new HashSet<>();
        letterB.add(new Word("арбуз"));
        letterB.add(new Word("амбар"));
        wordsByLetterPos.put(new WordCharacter(2, 'б'), letterB);

        Set<Word> letterU = new HashSet<>();
        letterU.add(new Word("арбуз"));
        wordsByLetterPos.put(new WordCharacter(3, 'у'), letterU);

        Set<Word> letterZ = new HashSet<>();
        letterZ.add(new Word("арбуз"));
        wordsByLetterPos.put(new WordCharacter(4, 'з'), letterZ);

        Set<Word> atLeastOneLetterAnywhere = new HashSet<>();
        atLeastOneLetterAnywhere.add(new Word("книга"));
        atLeastOneLetterAnywhere.add(new Word("арбуз"));
        atLeastOneLetterAnywhere.add(new Word("амбар"));
        atLeastOneLetterAnywhere.add(new Word("аборт"));

        //Привел к сетам, так как может быть разный порядок, а он не важен
        assertEquals(new HashSet<>(atLeastOneLetterAnywhere),
                new HashSet<>(wordleDictionary.getAtLeastOneLetterAnywhere()));
        assertEquals(wordsByLetterPos, wordleDictionary.getWordsByLetterPos());
    }

}

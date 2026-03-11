package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class WordleDictionary {
    private final Word puzzleWord;
    private final Set<Word> words;
    private final ArrayList<Word> atLeastOneLetterAnywhere;
    private final Map<WordCharacter, Set<Word>> wordsByLetterPos;
    private final LogFile logFile;

    public WordleDictionary(WordleDictionaryLoader wordleDictionaryLoader, LogFile logFile, Word puzzleWord) {
        this.words = wordleDictionaryLoader.getAllWords();
        this.logFile = logFile;
        this.atLeastOneLetterAnywhere = new ArrayList<>();
        this.wordsByLetterPos = new HashMap<>(5);
        this.puzzleWord = puzzleWord;
        fillDictionary();
    }

    public WordleDictionary(WordleDictionaryLoader wordleDictionaryLoader, LogFile logFile) {
        this.words = wordleDictionaryLoader.getAllWords();
        this.logFile = logFile;
        this.atLeastOneLetterAnywhere = new ArrayList<>();
        this.wordsByLetterPos = new HashMap<>(5);
        this.puzzleWord = getRandomWord();
        fillDictionary();
    }

    private void fillDictionary() {
        List<WordCharacter> wordsLetter = puzzleWord.getLetters();
        for (WordCharacter letter : wordsLetter) {
            wordsByLetterPos.put(letter, new HashSet<>(100));
        }


        for (Word word : words) {
            if (word.isAtLeastOneLetterContains(puzzleWord)) {
                atLeastOneLetterAnywhere.add(word);
            }

            ArrayList<WordCharacter> samePosLetters = word.samePosAndLetters(puzzleWord);
            if (!samePosLetters.isEmpty()) {
                for (WordCharacter letter : samePosLetters) {
                    wordsByLetterPos.get(letter).add(word);
                }
            }
        }
    }


    private Word getRandomWord() {
        Random random = new Random();
        int randomWordIndex = random.nextInt(words.size());
        int i = 0;
        Word randomWord = null;
        for (Word word : words) {
            if (i == randomWordIndex) {
                randomWord = word;
                break;
            }
            i++;
        }
        return randomWord;
    }

    public boolean isInDictionary(Word word) {
        return words.contains(word);
    }

    public boolean isInDictionary(String word) {
        Word wordToCheck = new Word(word);
        return isInDictionary(wordToCheck);
    }


    public Word getPuzzleWord() {
        return puzzleWord;
    }

    public Set<Word> getWords() {
        return words;
    }

    public ArrayList<Word> getAtLeastOneLetterAnywhere() {
        return atLeastOneLetterAnywhere;
    }

    public Map<WordCharacter, Set<Word>> getWordsByLetterPos() {
        return wordsByLetterPos;
    }
}

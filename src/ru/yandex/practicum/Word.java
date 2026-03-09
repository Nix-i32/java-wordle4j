package ru.yandex.practicum;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

public class Word {
    private final String word;
    private final List<WordCharacter> letters;

    public Word(String word) {
        this.word = word;
        this.letters = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            letters.add(new WordCharacter(i, word.charAt(i)));
        }
    }

    public Word(List<WordCharacter> letters) {
        this.letters = new ArrayList<>(letters);
        StringBuilder sb = new StringBuilder();
        for (WordCharacter letter : letters) {
            sb.append(letter.getLetter());
        }
        this.word = sb.toString();
    }

    public int length() {
        return letters.size();
    }

    public String getWord() {
        return word;
    }

    public List<WordCharacter> getLetters() {
        return List.copyOf(letters);
    }

    public List<Character> letterWithoutPos() {
        List<Character> letters = new ArrayList<>(5);
        for (WordCharacter letter : this.letters) {
            letters.add(letter.getLetter());
        }
        return letters;
    }

    public boolean isAtLeastOneLetterContains(Word otherWord) {
        Set<Character> thisLetters = new HashSet<>();
        for (WordCharacter thisLetter : this.letters) {
            thisLetters.add(thisLetter.getLetter());
        }

        for (WordCharacter otherLetter : otherWord.getLetters()) {
            if (thisLetters.contains(otherLetter.getLetter())) return true;
        }

        return false;
    }

    public ArrayList<Character> LetterContains(Word otherWord) {
        Set<Character> thisWord = new HashSet<>(this.letterWithoutPos());
        Set<Character> wordToCompare = new HashSet<>(otherWord.letterWithoutPos());
        thisWord.retainAll(wordToCompare);
        return new ArrayList<>(thisWord);
    }

    public boolean LetterContains(List<Character> otherLetters) {
        List<Character> letters = letterWithoutPos();
        return letters.containsAll(otherLetters);
    }


    public ArrayList<WordCharacter> samePosAndLetters(Word otherWord) {
        Set<WordCharacter> thisWord = new HashSet<>(this.letters);
        Set<WordCharacter> wordToCompare = new HashSet<>(otherWord.getLetters());
        thisWord.retainAll(wordToCompare);
        return new ArrayList<>(thisWord);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Word word1 = (Word) o;
        return Objects.equals(word, word1.word);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(word);
    }

}

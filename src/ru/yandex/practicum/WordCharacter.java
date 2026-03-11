package ru.yandex.practicum;

import java.util.Objects;

public class WordCharacter {
    private final int index;
    private final char letter;

    public WordCharacter(int index, char letter) {
        this.index = index;
        this.letter = letter;
    }

    public int getIndex() {
        return index;
    }

    public char getLetter() {
        return letter;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WordCharacter that = (WordCharacter) o;
        return index == that.index && letter == that.letter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, letter);
    }
}

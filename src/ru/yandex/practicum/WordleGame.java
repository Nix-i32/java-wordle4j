package ru.yandex.practicum;

import java.util.*;

public class WordleGame {
    private Word answers;
    private int totalAttempts;
    private int attempts;
    private WordleDictionary dictionary;
    private LogFile logFile;
    private Map<WordCharacter, LetterStatus> letterStatus;

    public WordleGame(WordleDictionary dictionary, LogFile logFile) {
        this.dictionary = dictionary;
        this.attempts = 6;
        this.totalAttempts = 6;
        this.answers = dictionary.getPuzzleWord();
        this.logFile = logFile;
        this.letterStatus = new HashMap<>(5);

        List<WordCharacter> wordsLetter = answers.getLetters();
        for (WordCharacter letter : wordsLetter) {
            letterStatus.put(letter, LetterStatus.UNKNOWN);
        }
    }

    public boolean isWinningGuess(String inPut) {
        return answers.getWord().equals(inPut);
    }

    public String winningGuess() {
        if (totalAttempts - attempts == 0) {
            return "Вы угадали с первой попытки!";
        } else {
            return String.format("Вы отгадали слово! Количество потраченных попыток: %d", totalAttempts - attempts);
        }
    }

    public List<String> clue(String inPut, int cluesToPrint) throws AttemptsLeftException, NoClueException {
        int clues = cluesToPrint;
        if (clues < 1) clues = 10;
        if (attempts == 0) {
            AttemptsLeftException exception = new AttemptsLeftException("Закончились попытки на угадывание");
            logFile.addToLog(exception);
            throw exception;

        }
        Word tryWord = new Word(inPut);
        boolean isAnyPosGuessed;
        boolean isAnyLetterGuessed;
        List<WordCharacter> samePosLetters = tryWord.samePosAndLetters(answers);
        if (!samePosLetters.isEmpty()) {
            for (WordCharacter letter : samePosLetters) {
                letterStatus.put(letter, LetterStatus.CORRECT_POSITION);
            }
        }

        ArrayList<Character> justSameLetters = answers.LetterContains(tryWord);
        if (!justSameLetters.isEmpty()) {
            for (Character cha : justSameLetters) {
                for (Map.Entry<WordCharacter, LetterStatus> entry : letterStatus.entrySet()) {
                    if (entry.getKey().getLetter() == cha && entry.getValue() != LetterStatus.CORRECT_POSITION) {
                        letterStatus.put(entry.getKey(), LetterStatus.UNKNOWN_POSITION);
                    }
                }
            }
        }

        isAnyPosGuessed = isAnyPosGuessed();
        isAnyLetterGuessed = isAnyLetterGuessed();
        Set<Word> words = new HashSet<>();

        if (isAnyPosGuessed) {
            boolean isFirstIterator = true;
            samePosLetters = getPosCorrect();
            for (WordCharacter letter : samePosLetters) {
                if (isFirstIterator) {
                    words = new HashSet<>(dictionary.getWordsByLetterPos().get(letter));
                    isFirstIterator = false;
                } else {
                    words.retainAll(dictionary.getWordsByLetterPos().get(letter));
                }
            }
        } else if (isAnyLetterGuessed) {
            List<WordCharacter> unknownPosition = getLetterCorrect();
            List<Character> unknownPositionToChar = new ArrayList<>();
            for (WordCharacter letter : unknownPosition) {
                unknownPositionToChar.add(letter.getLetter());
            }
            for (Word word1 : dictionary.getAtLeastOneLetterAnywhere()) {
                if (word1.LetterContains(unknownPositionToChar)) {
                    words.add(word1);
                }
            }
        } else {
            NoClueException exception = new NoClueException("Ни одна буква не отгадана");
            logFile.addToLog(exception);
            throw exception;
        }
        newAttempt();

        words.remove(tryWord);
        List<String> listToPrint = new ArrayList<>(words.size());
        int index = 0;
        for (Word word1 : words) {
            listToPrint.add(word1.getWord());
            if (index == clues) break;
            index++;
        }
        return listToPrint;
    }

    public List<String> clue(String inPut) throws AttemptsLeftException, NoClueException {
        return clue(inPut, 9);
    }

    private boolean isAnyPosGuessed() {
        boolean isAnyGuessed = false;
        for (Map.Entry<WordCharacter, LetterStatus> entry : letterStatus.entrySet()) {
            if (entry.getValue() == LetterStatus.CORRECT_POSITION) {
                isAnyGuessed = true;
                break;
            }
        }
        return isAnyGuessed;
    }

    private boolean isAnyLetterGuessed() {
        boolean isAnyGuessed = false;
        for (Map.Entry<WordCharacter, LetterStatus> entry : letterStatus.entrySet()) {
            if (entry.getValue() == LetterStatus.UNKNOWN_POSITION) {
                isAnyGuessed = true;
                break;
            }
        }
        return isAnyGuessed;
    }

    private List<WordCharacter> getLetterCorrect() {
        List<WordCharacter> correctLetter = new ArrayList<>();
        for (Map.Entry<WordCharacter, LetterStatus> entry : letterStatus.entrySet()) {
            if (entry.getValue() == LetterStatus.UNKNOWN_POSITION) correctLetter.add(entry.getKey());
        }
        return correctLetter;
    }

    private List<WordCharacter> getPosCorrect() {
        List<WordCharacter> correctLetter = new ArrayList<>();
        for (Map.Entry<WordCharacter, LetterStatus> entry : letterStatus.entrySet()) {
            if (entry.getValue() == LetterStatus.CORRECT_POSITION) correctLetter.add(entry.getKey());
        }
        return correctLetter;
    }

    public String correctInPut(String inPut) throws IllegalArgumentException, WordNotFoundException {
        String wordToCheck = inPut.trim();
        wordToCheck = wordToCheck.toLowerCase();

        if (wordToCheck == null) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    String.format("Слово не может быть null. Слово: %s", wordToCheck));
            logFile.addToLog(exception);
            throw exception;
        }

        if (wordToCheck.isBlank()) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    "Пустое слово или слово состоящие только из пробелов");
            logFile.addToLog(exception);
            throw exception;
        }

        if (wordToCheck.length() != 5) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    String.format("Символов в слове: %d", wordToCheck.length()));
            logFile.addToLog(exception);
            throw exception;
        }

        if (wordToCheck.contains(" ")) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    String.format("Слово содержит пробелы. Слово %s", wordToCheck));
            logFile.addToLog(exception);
            throw exception;
        }

        if (wordToCheck.matches("[0-9]+")) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    String.format("Слово содержит цифры. Слово %s", wordToCheck));
            logFile.addToLog(exception);
            throw exception;
        }

        if (!wordToCheck.matches("[а-яё]+")) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    String.format("Слово содержит иностранные буквы или спец символы. Слово %s", wordToCheck));
            logFile.addToLog(exception);
            throw exception;
        }

        if (!dictionary.isInDictionary(wordToCheck)) {
            WordNotFoundException exception = new WordNotFoundException(
                    String.format("Слова нет в словаре. Слово %s", wordToCheck));
            logFile.addToLog(exception);
            throw exception;
        }

        return wordToCheck;
    }

    public String status(String inPut) {
        Word word = new Word(inPut);
        StringBuilder message = new StringBuilder();
        String[] listToPrint = new String[word.length()];
        Arrays.fill(listToPrint, "-");
        for (int i = 0; i < word.length(); i++) {
            WordCharacter letterInPut = word.getLetters().get(i);
            WordCharacter letterAnswer = answers.getLetters().get(i);
            char charInPut = letterInPut.getLetter();
            if (letterInPut.equals(letterAnswer)) {
                listToPrint[i] = "+";
            }

            for (int n = 0; n < answers.length(); n++) {
                char charAnswerToCheck = answers.getLetters().get(n).getLetter();
                if (charInPut == charAnswerToCheck && listToPrint[i] != "+") {
                    listToPrint[i] = "^";
                }
            }
        }

        for (int i = 0; i < listToPrint.length; i++) {
            message.append(listToPrint[i]);
        }
        return message.toString();
    }

    public String status() {
        StringBuilder message = new StringBuilder();
        StringBuilder unknownPosLetters = new StringBuilder();
        List<Map.Entry<WordCharacter, LetterStatus>> statusList = new ArrayList<>(letterStatus.entrySet());
        statusList.sort((e1, e2) ->
                Integer.compare(e1.getKey().getIndex(), e2.getKey().getIndex()));
        for (int i = 0; i < statusList.size(); i++) {
            if (statusList.get(i).getValue() == LetterStatus.CORRECT_POSITION) {
                message.append(statusList.get(i).getKey().getLetter());
            } else if (statusList.get(i).getValue() == LetterStatus.UNKNOWN_POSITION) {
                unknownPosLetters.append(statusList.get(i).getKey().getLetter());
                unknownPosLetters.append(", ");
                message.append("_");
            } else {
                message.append("_");
            }
        }
        if (unknownPosLetters.length() > 0) {
            unknownPosLetters.setLength(unknownPosLetters.length() - 2);
            message.append(" ");
            message.append(unknownPosLetters);
        }
        return message.toString();
    }

    public int getAttempts() {
        return attempts;
    }

    public Word getAnswers() {
        return answers;
    }

    private void newAttempt() {
        attempts--;
    }

    public boolean isGameOn() {
        return attempts > 0;
    }

}

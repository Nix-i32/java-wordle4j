package ru.yandex.practicum;

import java.util.List;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LogFile logFile;
        WordleDictionaryLoader wordleDictionaryLoader;
        try {
            logFile = new LogFile(scanner);
            wordleDictionaryLoader = new WordleDictionaryLoader(logFile, scanner);
        } catch (LogFileException exception) {
            System.out.println("Не удалось создать лог. Выход их программы...");
            return;
        } catch (WordleDictionaryLoaderException exception) {
            System.out.println("Не удалось прочитать словарь или в словаре отсутствуют нужные слова" +
                    ". Выход их программы...");
            return;
        }
        WordleDictionary wordleDictionary = new WordleDictionary(wordleDictionaryLoader, logFile);
        WordleGame wordleGame = new WordleGame(wordleDictionary, logFile);
        System.out.println(wordleGame.getAnswers().getWord());
        while (wordleGame.isGameOn()) {
            System.out.print("Введите слово: ");
            String guess = scanner.nextLine();
            try {
                guess = wordleGame.correctInPut(guess);
                if (wordleGame.isWinningGuess(guess)) {
                    System.out.println(wordleGame.winningGuess());
                    break;
                } else {
                    printClues(wordleGame.clue(guess));
                    System.out.printf("%s - ваше слово%n%s%n'+' - правильная буква и позиция%n" +
                                    "'^' - буква есть в слове%n'-' - такой буквы нет%n" + ".".repeat(50) + "%n" +
                                    "Известные буквы: %s%n" + ".".repeat(50) + "%n" + "Оставшиеся попытки: %d%n",
                            guess, wordleGame.status(guess), wordleGame.status(), wordleGame.getAttempts());
                }
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
                System.out.println("Попытка не потрачена.");
            } catch (WordNotFoundException exception) {
                System.out.println(exception.getMessage());
                System.out.println("Попытка не потрачена.");
            } catch (NoClueException exception) {
                System.out.println(exception.getMessage());
            }
        }
        System.out.printf("%nЗагаданное слово - %s%n", wordleGame.getAnswers().getWord());
        System.out.println("Игра окончена!");

    }

    public static void printClues(List<String> list) {
        System.out.println("Возможные слова (" + list.size() + "):");
        System.out.println(".".repeat(50));

        int columns = 5;
        int rows = (int) Math.ceil(list.size() / (double) columns);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int index = row + col * rows;
                if (index < list.size()) {
                    System.out.printf("%-10s", list.get(index));
                }
            }
            System.out.println();
        }
        System.out.println(".".repeat(50));
    }

}

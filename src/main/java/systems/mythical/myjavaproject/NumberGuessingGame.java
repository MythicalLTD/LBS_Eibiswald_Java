package systems.mythical.myjavaproject;

import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {
    private static final int MAX_ATTEMPTS = 10;
    private final Scanner scanner;
    private final Random random;

    public NumberGuessingGame() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    public void start() {
        String attemptWord = " Versuche";
        int targetNumber = random.nextInt(100) + 1;
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            System.out.println("Noch " + (MAX_ATTEMPTS - i) + attemptWord);
            System.out.print("Rate eine Zahl (1-100): ");
            int guess = scanner.nextInt();

            if (guess < targetNumber) {
                System.out.println("Zahl zu klein!");
            } else if (guess > targetNumber) {
                System.out.println("Zahl zu groß!");
            } else {
                System.out.println("Gratulation - nach " + (i + 1) + " Versuchen erraten!");
                break;
            }

            if (MAX_ATTEMPTS - i == 2) {
                attemptWord = " Versuch";
            }
            if (MAX_ATTEMPTS - i == 1) {
                System.out.println("Keine Versuche mehr. Die Zahl war " + targetNumber);
            }
        }
    }

    public static void main(String[] args) {
        new NumberGuessingGame().start();
    }
} 
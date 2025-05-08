package systems.mythical.myjavaproject;

import java.util.Scanner;

public class Calculator {
    private final Scanner scanner;

    public Calculator() {
        this.scanner = new Scanner(System.in);
    }

    public void calculator() {
        System.out.println("Einfacher Taschenrechner");

        // Erste Zahl eingeben
        System.out.print("Bitte geben Sie die erste Zahl ein: ");
        double number1 = scanner.nextDouble();

        // Zweite Zahl eingeben
        System.out.print("Bitte geben Sie die zweite Zahl ein: ");
        double number2 = scanner.nextDouble();

        // Rechenoperation eingeben
        System.out.println("Bitte wählen Sie die Rechenoperation:");
        System.out.println("+ für Addition");
        System.out.println("- für Subtraktion");
        System.out.println("* für Multiplikation");
        System.out.println("/ für Division");
        System.out.print("Ihre Wahl: ");

        scanner.nextLine(); // Buffer leeren
        String operation = scanner.nextLine();

        double result = 0;
        boolean validOperation = true;

        // Mehrfachverzweigung für die Rechenoperationen
        switch (operation) {
            case "+" -> result = number1 + number2;
            case "-" -> result = number1 - number2;
            case "*" -> result = number1 * number2;
            case "/" -> {
                if (number2 != 0) {
                    result = number1 / number2;
                } else {
                    System.out.println("Fehler: Division durch Null ist nicht erlaubt!");
                    validOperation = false;
                }
            }
            default -> {
                System.out.println("Ungültige Rechenoperation!");
                validOperation = false;
            }
        }

        if (validOperation) {
            System.out.println("Ergebnis: " + result);
        }
    }
}
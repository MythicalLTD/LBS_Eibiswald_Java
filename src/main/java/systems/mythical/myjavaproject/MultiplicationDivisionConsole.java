package systems.mythical.myjavaproject;

import java.util.Scanner;

public class MultiplicationDivisionConsole {
    private final Scanner scanner;

    public MultiplicationDivisionConsole() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        try (scanner) {
            boolean continueProgram = true;
            
            while (continueProgram) {
                System.out.println("\n=== Multiplikation und Division ===");
                System.out.print("Bitte geben Sie den Multiplikator ein: ");
                
                try {
                    int multiplier = Integer.parseInt(scanner.nextLine());
                    
                    // Multiplication part
                    System.out.println("\nMultiplikation:");
                    int currentValue = multiplier;
                    for (int i = 2; i <= 10; i++) {
                        currentValue = currentValue * i;
                        System.out.printf("%d * %d = %d%n", currentValue/i, i, currentValue);
                    }
                    
                    // Division part
                    System.out.println("\nDivision:");
                    for (int i = 2; i <= 10; i++) {
                        currentValue = currentValue / i;
                        System.out.printf("%d / %d = %d%n", currentValue*i, i, currentValue);
                    }
                    
                    System.out.print("\nMöchten Sie eine weitere Berechnung durchführen? (j/n): ");
                    String answer = scanner.nextLine().toLowerCase();
                    continueProgram = answer.equals("j");
                    
                } catch (NumberFormatException e) {
                    System.out.println("Fehler: Bitte geben Sie eine gültige Zahl ein!");
                }
            }
            
            System.out.println("\nProgramm wird beendet. Auf Wiedersehen!");
        }
    }

    public static void main(String[] args) {
        new MultiplicationDivisionConsole().start();
    }
} 
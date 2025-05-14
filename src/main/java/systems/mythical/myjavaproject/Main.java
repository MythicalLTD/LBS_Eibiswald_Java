package systems.mythical.myjavaproject;

import java.util.Scanner;

import systems.mythical.myjavaproject.gui.GuiMenu;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Modus Auswahl ===");
            System.out.println("1. Terminal Modus");
            System.out.println("2. GUI Modus");
            System.out.print("Bitte wählen Sie einen Modus (1-2): ");
            
            int mode = scanner.nextInt();
            
            switch (mode) {
                case 1 -> runTerminalMode(scanner);
                case 2 -> runGuiMode();
                default -> System.out.println("Ungültige Auswahl! Programm wird beendet.");
            }
        }
    }
    
    private static void runTerminalMode(Scanner scanner) {
        while (true) {
            System.out.println("\n=== Hauptmenü ===");
            System.out.println("1. Geschwindigkeitsüberschreitung berechnen");
            System.out.println("2. Taschenrechner");
            System.out.println("3. ggT berechnen");
            System.out.println("4. Multiplikation und Division");
            System.out.println("5. Gues ");
            System.out.println("6. Beenden");
            System.out.print("Bitte wählen Sie eine Option (1-6): ");
            
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1 -> {
                    Euro euro = new Euro();
                    euro.euro();
                }
                case 2 -> {
                    Calculator calculator = new Calculator();
                    calculator.calculator();
                }
                case 3 -> {
                    ggT ggt = new ggT();
                    ggt.calculateGGT();
                }
                case 4 -> {
                    MultiplicationDivisionConsole multiplicationDivisionConsole = new MultiplicationDivisionConsole();
                    multiplicationDivisionConsole.start();
                }
                case 5 -> {
                    NumberGuessingGame nmb = new NumberGuessingGame();
                    nmb.start();
                }
                case 6 -> {
                    System.out.println("Programm wird beendet.");
                    return;
                }
                default -> System.out.println("Ungültige Auswahl! Bitte wählen Sie 1-6.");
            }
        }
    }
    
    private static void runGuiMode() {
        new GuiMenu();
    }
}
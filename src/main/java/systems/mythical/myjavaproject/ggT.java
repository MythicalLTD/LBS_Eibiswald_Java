package systems.mythical.myjavaproject;

import java.util.Scanner;


public class ggT {
    private final Scanner scanner;

    public ggT() {
        this.scanner = new Scanner(System.in);
    }

    public void calculateGGT() {
        System.out.println("\nGrößter gemeinsamer Teiler (ggT) berechnen");
        
        // Nim der 1te zahl ein
        System.out.print("Bitte geben Sie die erste Zahl ein: ");
        int number1 = scanner.nextInt();
        
        // Nim der 2te zahl ein
        System.out.print("Bitte geben Sie die zweite Zahl ein: ");
        int number2 = scanner.nextInt();
        
        // Sicher sein das beide ziffern positiv sind
        number1 = Math.abs(number1);
        number2 = Math.abs(number2);
        
        // Berechne der ggt mit division method
        int a = number1;
        int b = number2;
        int remainder;
        
        System.out.println("\nBerechnungsschritte:");
        System.out.println("a = " + a + ", b = " + b);
        
        while (b != 0) {
            remainder = a % b;
            System.out.println(a + " % " + b + " = " + remainder);
            a = b;
            b = remainder;
            System.out.println("a = " + a + ", b = " + b);
        }
        
        System.out.println("\nDer größte gemeinsame Teiler von " + number1 + " und " + number2 + " ist: " + a);
    }
}

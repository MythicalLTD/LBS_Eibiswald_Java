package systems.mythical.myjavaproject;

import java.util.Scanner;

public class Euro {
    private final Scanner scanner;

    public Euro() {
        this.scanner = new Scanner(System.in);
    }

    public void euro() {
        System.out.println("\nGeschwindigkeitsüberschreitung berechnen");
        System.out.print("Bitte geben Sie die Geschwindigkeitsüberschreitung in km/h ein: ");

        int speedExcess = scanner.nextInt();
        int fine;

        if (speedExcess <= 10) {
            fine = 0;
        } else if (speedExcess <= 30) {
            fine = 50;
        } else if (speedExcess <= 45) {
            fine = 100;
        } else if (speedExcess <= 60) {
            fine = 150;
        } else {
            fine = 200;
        }

        System.out.println("Die Strafe beträgt: " + fine + " Euro");
    }
}
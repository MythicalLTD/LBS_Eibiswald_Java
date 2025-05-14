package systems.mythical.myjavaproject;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PersonManager {
    private static final int INPUT = 1;
    private static final int OUTPUT = 2;
    private static final int EXIT = 3;
    
    private static final int MIN_POINTS = 0;
    private static final int MAX_POINTS = 100;
    private static final int MAX_PERSONS = 20;
    
    private final ArrayList<Person> persons;
    private final Scanner scanner;
    
    public PersonManager() {
        this.persons = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        boolean running = true;
        
        while (running) {
            displayMenu();
            int choice = getValidChoice();
            
            try {
                running = processChoice(choice);
            } catch (Exception e) {
                System.err.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
    
    private void displayMenu() {
        System.out.println("\nWählen Sie eine Option:");
        System.out.println(INPUT + ") Person eingeben");
        System.out.println(OUTPUT + ") Personen ausgeben");
        System.out.println(EXIT + ") Programm beenden");
    }
    
    private int getValidChoice() {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
                
                if (choice >= INPUT && choice <= EXIT) {
                    return choice;
                }
                System.out.println("Bitte geben Sie eine Zahl zwischen " + INPUT + " und " + EXIT + " ein.");
            } catch (InputMismatchException e) {
                System.out.println("Fehler: Bitte geben Sie nur ganze Zahlen ein.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
    
    private boolean processChoice(int choice) {
        switch (choice) {
            case INPUT:
                if (persons.size() >= MAX_PERSONS) {
                    System.out.println("Maximale Anzahl an Personen (" + MAX_PERSONS + ") erreicht!");
                    break;
                }
                createPerson();
                break;
            case OUTPUT:
                displayAllPersons();
                break;
            case EXIT:
                System.out.println("Programm wird beendet...");
                return false;
            default:
                System.out.println("Ungültige Auswahl!");
        }
        return true;
    }
    
    private void createPerson() {
        System.out.println("Bitte geben Sie den Vornamen ein:");
        String firstName = scanner.nextLine().trim();
        
        System.out.println("Bitte geben Sie den Nachnamen ein:");
        String lastName = scanner.nextLine().trim();
        
        System.out.println("Bitte geben Sie die Straße ein:");
        String street = scanner.nextLine().trim();
        
        System.out.println("Bitte geben Sie die Hausnummer ein:");
        String houseNumber = scanner.nextLine().trim();
        
        System.out.println("Bitte geben Sie die PLZ ein:");
        String postalCode = scanner.nextLine().trim();
        
        System.out.println("Bitte geben Sie den Ort ein:");
        String city = scanner.nextLine().trim();
        
        int points = getValidPoints();
        
        try {
            persons.add(new Person(firstName, lastName, street, houseNumber, postalCode, city, points));
            System.out.println("Person wurde erfolgreich erstellt.");
        } catch (IllegalArgumentException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    
    private void displayAllPersons() {
        if (persons.isEmpty()) {
            System.out.println("Keine Personen vorhanden.");
            return;
        }
        
        System.out.println("\nAlle Personen:");
        for (Person person : persons) {
            System.out.println("\n" + person);
        }
        
        showStatistics();
    }
    
    private void showStatistics() {
        if (persons.isEmpty()) {
            return;
        }
        
        int min = persons.get(0).getPunktezahl();
        int max = min;
        int sum = min;
        
        for (int i = 1; i < persons.size(); i++) {
            int points = persons.get(i).getPunktezahl();
            min = Math.min(min, points);
            max = Math.max(max, points);
            sum += points;
        }
        
        double average = (double) sum / persons.size();
        
        System.out.println("\nStatistik:");
        System.out.println("Minimum: " + min);
        System.out.println("Maximum: " + max);
        System.out.printf("Durchschnitt: %.2f%n", average);
    }
    
    private int getValidPoints() {
        while (true) {
            try {
                System.out.println("Bitte geben Sie die Punktezahl ein (" + MIN_POINTS + "-" + MAX_POINTS + "):");
                int points = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
                
                if (isValidPoints(points)) {
                    return points;
                }
            } catch (InputMismatchException e) {
                System.out.println("Fehler: Bitte geben Sie nur ganze Zahlen ein.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
    
    private boolean isValidPoints(int points) {
        if (points < MIN_POINTS || points > MAX_POINTS) {
            System.out.println("Fehler: Die Punktezahl muss zwischen " + MIN_POINTS + " und " + MAX_POINTS + " liegen.");
            return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        PersonManager manager = new PersonManager();
        manager.start();
    }
} 
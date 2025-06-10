package systems.mythical.myjavaproject;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Hauptklasse für die Zeugnisanwendung
 */
public class ZeugnisAnwendung {
    
    /**
     * Startet die Zeugnisanwendung
     */
    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\n=== ZEUGNISANWENDUNG ===");
            System.out.println("1. Beispielzeugnis erstellen");
            System.out.println("2. Eigenes Zeugnis erstellen");
            System.out.print("Bitte wählen Sie eine Option (1-2): ");
            
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1 -> createSampleCertificate();
                case 2 -> createInteractiveCertificate(scanner);
                default -> System.out.println("Ungültige Auswahl!");
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    
    /**
     * Erstellt ein Beispielzeugnis mit vordefinierten Daten
     */
    private void createSampleCertificate() {
        System.out.println("\n=== Beispielzeugnis wird erstellt ===");
        
        // Zeugnis für Gerhard Jarz (wie im gezeigten Beispiel)
        Zeugnis zeugnis = new Zeugnis(
            "Gerhard Jarz",
            LocalDate.of(1990, 1, 1),
            LocalDate.of(2025, 6, 5)
        );
        
        // Schulfächer hinzufügen (basierend auf dem gezeigten Beispiel)
        zeugnis.addSubject(new Schulfach("Englisch", 1, "ENG"));
        zeugnis.addSubject(new Schulfach("Deutsch", 3, "DEU"));
        zeugnis.addSubject(new Schulfach("Informatik", 4, "INF"));
        zeugnis.addSubject(new Schulfach("Angewandte Mathematik", 2, "ANG"));
        zeugnis.addSubject(new Schulfach("Geschichte", 2, "GES"));
        zeugnis.addSubject(new Schulfach("Geographie", 3, "GEO"));
        
        System.out.println("\nSchulfächer vor der Sortierung:");
        for (Schulfach fach : zeugnis.getSchulfaecher()) {
            System.out.println("- " + fach.getBezeichnung());
        }
        
        // Fächer sortieren
        System.out.println("\nSortiere Schulfächer alphabetisch...");
        zeugnis.sortSubjects();
        
        System.out.println("\nSchulfächer nach der Sortierung:");
        for (Schulfach fach : zeugnis.getSchulfaecher()) {
            System.out.println("- " + fach.getBezeichnung());
        }
        
        // Zeugnis ausgeben
        zeugnis.printCertificate();
        
        System.out.printf("\nZusammenfassung:%n");
        System.out.printf("Anzahl Fächer: %d%n", zeugnis.getAnzahlFaecher());
        System.out.printf("Notendurchschnitt: %.2f%n", zeugnis.getNotendurchschnitt());
    }
    
    /**
     * Erstellt ein Zeugnis mit interaktiver Eingabe
     * @param scanner Scanner für Benutzereingaben
     */
    private void createInteractiveCertificate(Scanner scanner) {
        System.out.println("\n=== Eigenes Zeugnis erstellen ===");
        
        try {
            // Zeugnis mit Benutzereingaben erstellen
            Zeugnis zeugnis = new Zeugnis(scanner);
            
            // Schulfächer interaktiv eingeben
            zeugnis.addSubjectsInteractive(scanner);
            
            if (zeugnis.getAnzahlFaecher() > 0) {
                System.out.println("\nMöchten Sie die Fächer sortieren? (j/n): ");
                String sortChoice = scanner.nextLine();
                
                if (sortChoice.equalsIgnoreCase("j") || sortChoice.equalsIgnoreCase("ja")) {
                    System.out.println("Sortiere Schulfächer alphabetisch...");
                    zeugnis.sortSubjects();
                }
                
                // Zeugnis ausgeben
                zeugnis.printCertificate();
                
                System.out.printf("\nZusammenfassung:%n");
                System.out.printf("Anzahl Fächer: %d%n", zeugnis.getAnzahlFaecher());
                System.out.printf("Notendurchschnitt: %.2f%n", zeugnis.getNotendurchschnitt());
            } else {
                System.out.println("Keine Fächer eingegeben. Zeugnis wird nicht erstellt.");
            }
            
        } catch (Exception e) {
            System.out.println("Fehler beim Erstellen des Zeugnisses: " + e.getMessage());
            System.out.println("Bitte überprüfen Sie das Datumsformat (dd.MM.yyyy)");
        }
    }
    
    /**
     * Demonstriert den Bubble Sort Algorithmus
     */
    public void demonstrateBubbleSort() {
        System.out.println("\n=== Bubble Sort Demonstration ===");
        
        // Erstelle ein Zeugnis mit unsortierten Fächern
        Zeugnis zeugnis = new Zeugnis(
            "Test Schüler",
            LocalDate.of(2000, 1, 1),
            LocalDate.now()
        );
        
        // Füge Fächer in unsortierer Reihenfolge hinzu
        zeugnis.addSubject(new Schulfach("Zeitmanagement", 2));
        zeugnis.addSubject(new Schulfach("Algebra", 1));
        zeugnis.addSubject(new Schulfach("Mathematik", 3));
        zeugnis.addSubject(new Schulfach("Biologie", 2));
        zeugnis.addSubject(new Schulfach("Deutsch", 4));
        
        System.out.println("Vor der Sortierung:");
        for (Schulfach fach : zeugnis.getSchulfaecher()) {
            System.out.println("- " + fach.getBezeichnung());
        }
        
        zeugnis.sortSubjects();
        
        System.out.println("\nNach der Sortierung:");
        for (Schulfach fach : zeugnis.getSchulfaecher()) {
            System.out.println("- " + fach.getBezeichnung());
        }
    }
} 
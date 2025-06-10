package systems.mythical.myjavaproject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Klasse Zeugnis repräsentiert ein Schulzeugnis mit Personendaten und Schulfächern
 */
public class Zeugnis {
    private String schuelerName;
    private LocalDate geburtsdatum;
    private LocalDate zeugnisdatum;
    private ArrayList<Schulfach> schulfaecher;
    private double notendurchschnitt;
    private static final int MAX_FAECHER = 10;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    /**
     * Konstruktor für Zeugnis
     * @param schuelerName Name des Schülers
     * @param geburtsdatum Geburtsdatum des Schülers
     * @param zeugnisdatum Datum des Zeugnisses
     */
    public Zeugnis(String schuelerName, LocalDate geburtsdatum, LocalDate zeugnisdatum) {
        this.schuelerName = schuelerName;
        this.geburtsdatum = geburtsdatum;
        this.zeugnisdatum = zeugnisdatum;
        this.schulfaecher = new ArrayList<>();
        this.notendurchschnitt = 0.0;
    }
    
    /**
     * Konstruktor mit Eingabe über Scanner
     * @param scanner Scanner für Benutzereingaben
     */
    public Zeugnis(Scanner scanner) {
        this.schulfaecher = new ArrayList<>();
        this.notendurchschnitt = 0.0;
        
        System.out.print("Schülername: ");
        scanner.nextLine(); // Buffer leeren
        this.schuelerName = scanner.nextLine();
        
        System.out.print("Geburtsdatum (dd.MM.yyyy): ");
        String geburtsdatumStr = scanner.nextLine();
        this.geburtsdatum = LocalDate.parse(geburtsdatumStr, DATE_FORMAT);
        
        System.out.print("Zeugnisdatum (dd.MM.yyyy): ");
        String zeugnisdatumStr = scanner.nextLine();
        this.zeugnisdatum = LocalDate.parse(zeugnisdatumStr, DATE_FORMAT);
    }
    
    /**
     * Fügt ein Schulfach zum Zeugnis hinzu
     * @param schulfach Das hinzuzufügende Schulfach
     * @return true wenn erfolgreich hinzugefügt, false wenn Maximum erreicht
     */
    public boolean addSubject(Schulfach schulfach) {
        if (schulfaecher.size() < MAX_FAECHER) {
            schulfaecher.add(schulfach);
            calculateAverage();
            return true;
        } else {
            System.out.println("Maximale Anzahl von " + MAX_FAECHER + " Fächern erreicht!");
            return false;
        }
    }
    
    /**
     * Berechnet den Notendurchschnitt
     */
    private void calculateAverage() {
        if (schulfaecher.isEmpty()) {
            notendurchschnitt = 0.0;
            return;
        }
        
        int summe = 0;
        for (Schulfach fach : schulfaecher) {
            summe += fach.getNote();
        }
        notendurchschnitt = (double) summe / schulfaecher.size();
    }
    
    /**
     * Sortiert die Schulfächer aufsteigend nach Bezeichnung mit Bubble Sort
     */
    public void sortSubjects() {
        int n = schulfaecher.size();
        
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < n - i; j++) {
                if (schulfaecher.get(j).getBezeichnung().compareToIgnoreCase(
                        schulfaecher.get(j + 1).getBezeichnung()) > 0) {
                    Collections.swap(schulfaecher, j, j + 1);
                }
            }
        }
    }
    
    /**
     * Gibt das formatierte Zeugnis aus
     */
    public void printCertificate() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                         ZEUGNIS");
        System.out.println("=".repeat(60));
        System.out.println("Name: " + schuelerName);
        System.out.println("Geburtsdatum: " + geburtsdatum.format(DATE_FORMAT));
        System.out.println();
        System.out.println("-".repeat(60));
        
        if (schulfaecher.isEmpty()) {
            System.out.println("Keine Schulfächer eingetragen.");
        } else {
            for (Schulfach fach : schulfaecher) {
                String kurzbezeichnung = fach.getKurzbezeichnung();
                if (kurzbezeichnung != null && !kurzbezeichnung.isEmpty()) {
                    System.out.printf("%-3s | %-30s - %d%n", 
                        kurzbezeichnung, fach.getBezeichnung(), fach.getNote());
                } else {
                    System.out.printf("%-30s - %d%n", 
                        fach.getBezeichnung(), fach.getNote());
                }
            }
        }
        
        System.out.println("-".repeat(60));
        System.out.printf("%s %.2f%n", 
            String.format("%-40s", "Notendurchschnitt:"), notendurchschnitt);
        System.out.println();
        System.out.printf("%s %s%n", 
            zeugnisdatum.format(DATE_FORMAT), "| Zeugnisdatum");
        System.out.println("=".repeat(60));
    }
    
    /**
     * Interaktive Eingabe von Schulfächern
     * @param scanner Scanner für Benutzereingaben
     */
    public void addSubjectsInteractive(Scanner scanner) {
        System.out.println("\n=== Schulfächer eingeben ===");
        System.out.println("Maximale Anzahl: " + MAX_FAECHER + " Fächer");
        
        while (schulfaecher.size() < MAX_FAECHER) {
            System.out.printf("\nFach %d von %d eingeben:%n", 
                schulfaecher.size() + 1, MAX_FAECHER);
            
            System.out.print("Fachbezeichnung (oder 'fertig' zum Beenden): ");
            String bezeichnung = scanner.nextLine();
            
            if (bezeichnung.equalsIgnoreCase("fertig")) {
                break;
            }
            
            System.out.print("Note (1-5): ");
            int note;
            try {
                note = Integer.parseInt(scanner.nextLine());
                if (note < 1 || note > 5) {
                    System.out.println("Ungültige Note! Bitte 1-5 eingeben.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe! Bitte eine Zahl eingeben.");
                continue;
            }
            
            System.out.print("Kurzbezeichnung (optional, Enter für leer): ");
            String kurzbezeichnung = scanner.nextLine();
            
            Schulfach fach = new Schulfach(bezeichnung, note, kurzbezeichnung);
            addSubject(fach);
            
            System.out.println("Fach erfolgreich hinzugefügt!");
        }
        
        if (schulfaecher.size() == MAX_FAECHER) {
            System.out.println("Maximale Anzahl von Fächern erreicht!");
        }
    }
    
    // Getter und Setter
    public String getSchuelerName() {
        return schuelerName;
    }
    
    public void setSchuelerName(String schuelerName) {
        this.schuelerName = schuelerName;
    }
    
    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }
    
    public void setGeburtsdatum(LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }
    
    public LocalDate getZeugnisdatum() {
        return zeugnisdatum;
    }
    
    public void setZeugnisdatum(LocalDate zeugnisdatum) {
        this.zeugnisdatum = zeugnisdatum;
    }
    
    public ArrayList<Schulfach> getSchulfaecher() {
        return new ArrayList<>(schulfaecher); // Defensive Kopie
    }
    
    public double getNotendurchschnitt() {
        return notendurchschnitt;
    }
    
    public int getAnzahlFaecher() {
        return schulfaecher.size();
    }
} 
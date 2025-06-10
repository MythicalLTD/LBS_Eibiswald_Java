package systems.mythical.myjavaproject;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse Warteschlange - verwaltet die Warteschlange vor der Autowaschanlage
 * Maximal 10 Autos können in der Schlange stehen
 */
public class Warteschlange {
    private static final int MAX_CAPACITY = 10;
    private List<Auto> warteschlange;
    
    /**
     * Konstruktor - erstellt eine leere Warteschlange
     */
    public Warteschlange() {
        this.warteschlange = new ArrayList<>();
    }
    
    /**
     * Fügt ein Auto am Ende der Warteschlange hinzu
     * @param auto Das hinzuzufügende Auto
     * @return true wenn erfolgreich hinzugefügt, false wenn die Schlange voll ist
     */
    public boolean autoHinzufuegen(Auto auto) {
        if (warteschlange.size() >= MAX_CAPACITY) {
            System.out.println("Warteschlange ist voll! Maximale Kapazität: " + MAX_CAPACITY);
            return false;
        }
        
        // Prüfen ob Auto bereits in der Schlange ist
        if (warteschlange.contains(auto)) {
            System.out.println("Auto mit Kennzeichen " + auto.getKennzeichen() + " ist bereits in der Warteschlange!");
            return false;
        }
        
        warteschlange.add(auto);
        System.out.println("Auto " + auto.getKennzeichen() + " wurde zur Warteschlange hinzugefügt.");
        return true;
    }
    
    /**
     * Entfernt das erste Auto aus der Warteschlange (für den Waschvorgang)
     * @return Das erste Auto oder null wenn die Schlange leer ist
     */
    public Auto naechstesAutoWaschen() {
        if (warteschlange.isEmpty()) {
            System.out.println("Keine Autos in der Warteschlange!");
            return null;
        }
        
        Auto auto = warteschlange.remove(0);
        System.out.println("Auto " + auto.getKennzeichen() + " wird gewaschen...");
        return auto;
    }
    
    /**
     * Entfernt ein spezifisches Auto aus der Warteschlange (Auto verlässt die Schlange)
     * @param kennzeichen Das Kennzeichen des zu entfernenden Autos
     * @return true wenn erfolgreich entfernt, false wenn Auto nicht gefunden
     */
    public boolean autoEntfernen(String kennzeichen) {
        for (int i = 0; i < warteschlange.size(); i++) {
            if (warteschlange.get(i).getKennzeichen().equalsIgnoreCase(kennzeichen)) {
                Auto entferntesAuto = warteschlange.remove(i);
                System.out.println("Auto " + entferntesAuto.getKennzeichen() + " hat die Warteschlange verlassen.");
                return true;
            }
        }
        System.out.println("Auto mit Kennzeichen " + kennzeichen + " wurde nicht in der Warteschlange gefunden!");
        return false;
    }
    
    /**
     * Zeigt die aktuelle Warteschlange an
     */
    public void warteschlangeAnzeigen() {
        if (warteschlange.isEmpty()) {
            System.out.println("Die Warteschlange ist leer.");
            return;
        }
        
        System.out.println("\n=== AKTUELLE WARTESCHLANGE ===");
        System.out.println("Position | Auto-Info");
        System.out.println("-".repeat(50));
        
        for (int i = 0; i < warteschlange.size(); i++) {
            System.out.printf("%-8d | %s%n", (i + 1), warteschlange.get(i));
        }
        
        System.out.printf("\nAnzahl Autos: %d/%d%n", warteschlange.size(), MAX_CAPACITY);
        System.out.printf("Freie Plätze: %d%n", MAX_CAPACITY - warteschlange.size());
    }
    
    /**
     * Überprüft ob die Warteschlange leer ist
     * @return true wenn leer, false sonst
     */
    public boolean istLeer() {
        return warteschlange.isEmpty();
    }
    
    /**
     * Überprüft ob die Warteschlange voll ist
     * @return true wenn voll, false sonst
     */
    public boolean istVoll() {
        return warteschlange.size() >= MAX_CAPACITY;
    }
    
    /**
     * Gibt die aktuelle Anzahl der Autos zurück
     * @return Anzahl der Autos in der Warteschlange
     */
    public int getAnzahlAutos() {
        return warteschlange.size();
    }
    
    /**
     * Gibt die maximale Kapazität zurück
     * @return Maximale Kapazität der Warteschlange
     */
    public int getMaxKapazitaet() {
        return MAX_CAPACITY;
    }
    
    /**
     * Gibt eine Kopie der Warteschlange zurück (für Thread-Zugriff)
     * @return Liste der Autos in der Warteschlange
     */
    public List<Auto> getWarteschlange() {
        return new ArrayList<>(warteschlange);
    }
    
    /**
     * Sucht ein Auto anhand des Kennzeichens
     * @param kennzeichen Das zu suchende Kennzeichen
     * @return Das gefundene Auto oder null
     */
    public Auto autoSuchen(String kennzeichen) {
        for (Auto auto : warteschlange) {
            if (auto.getKennzeichen().equalsIgnoreCase(kennzeichen)) {
                return auto;
            }
        }
        return null;
    }
    
    /**
     * Gibt Statistiken über die Warteschlange aus
     */
    public void statistikAnzeigen() {
        if (warteschlange.isEmpty()) {
            System.out.println("Keine Statistiken verfügbar - Warteschlange ist leer.");
            return;
        }
        
        System.out.println("\n=== WARTESCHLANGEN-STATISTIK ===");
        
        long gesamtWartezeit = 0;
        long maxWartezeit = 0;
        String laengstWartendesAuto = "";
        
        for (Auto auto : warteschlange) {
            long wartezeit = auto.getWartezeitInSekunden();
            gesamtWartezeit += wartezeit;
            
            if (wartezeit > maxWartezeit) {
                maxWartezeit = wartezeit;
                laengstWartendesAuto = auto.getKennzeichen();
            }
        }
        
        double durchschnittlicheWartezeit = (double) gesamtWartezeit / warteschlange.size();
        
        System.out.printf("Anzahl Autos: %d%n", warteschlange.size());
        System.out.printf("Durchschnittliche Wartezeit: %.1f Sekunden%n", durchschnittlicheWartezeit);
        System.out.printf("Maximale Wartezeit: %d Sekunden (%s)%n", maxWartezeit, laengstWartendesAuto);
        System.out.printf("Gesamte Wartezeit aller Autos: %d Sekunden%n", gesamtWartezeit);
    }
} 
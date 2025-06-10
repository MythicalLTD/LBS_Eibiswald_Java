package systems.mythical.myjavaproject;

/**
 * Klasse Auto - repräsentiert ein Auto in der Warteschlange
 */
public class Auto {
    private String kennzeichen;
    private String fahrzeugtyp;
    private String farbe;
    private long ankunftszeit;
    
    /**
     * Konstruktor für Auto
     * @param kennzeichen Das Kennzeichen des Autos
     * @param fahrzeugtyp Der Typ des Fahrzeugs (z.B. PKW, SUV, etc.)
     * @param farbe Die Farbe des Autos
     */
    public Auto(String kennzeichen, String fahrzeugtyp, String farbe) {
        this.kennzeichen = kennzeichen;
        this.fahrzeugtyp = fahrzeugtyp;
        this.farbe = farbe;
        this.ankunftszeit = System.currentTimeMillis();
    }
    
    /**
     * Vereinfachter Konstruktor - nur mit Kennzeichen
     * @param kennzeichen Das Kennzeichen des Autos
     */
    public Auto(String kennzeichen) {
        this(kennzeichen, "PKW", "unbekannt");
    }
    
    // Getter-Methoden
    public String getKennzeichen() {
        return kennzeichen;
    }
    
    public String getFahrzeugtyp() {
        return fahrzeugtyp;
    }
    
    public String getFarbe() {
        return farbe;
    }
    
    public long getAnkunftszeit() {
        return ankunftszeit;
    }
    
    // Setter-Methoden
    public void setFahrzeugtyp(String fahrzeugtyp) {
        this.fahrzeugtyp = fahrzeugtyp;
    }
    
    public void setFarbe(String farbe) {
        this.farbe = farbe;
    }
    
    /**
     * Berechnet die Wartezeit in Sekunden
     * @return Wartezeit in Sekunden
     */
    public long getWartezeitInSekunden() {
        return (System.currentTimeMillis() - ankunftszeit) / 1000;
    }
    
    /**
     * Gibt eine formatierte Darstellung des Autos zurück
     * @return String-Darstellung des Autos
     */
    @Override
    public String toString() {
        return String.format("%s (%s, %s) - Wartezeit: %d Sek.", 
                           kennzeichen, fahrzeugtyp, farbe, getWartezeitInSekunden());
    }
    
    /**
     * Überprüft Gleichheit basierend auf dem Kennzeichen
     * @param obj Das zu vergleichende Objekt
     * @return true wenn die Kennzeichen gleich sind
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Auto auto = (Auto) obj;
        return kennzeichen.equals(auto.kennzeichen);
    }
    
    @Override
    public int hashCode() {
        return kennzeichen.hashCode();
    }
} 
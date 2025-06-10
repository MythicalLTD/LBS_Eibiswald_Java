package systems.mythical.myjavaproject;

/**
 * Klasse Schulfach repräsentiert ein Schulfach mit Bezeichnung und Note
 */
public class Schulfach {
    private String bezeichnung;
    private int note;
    private String kurzbezeichnung;
    
    /**
     * Konstruktor für Schulfach
     * @param bezeichnung Die vollständige Bezeichnung des Fachs
     * @param note Die Note (1-5, wobei 1 die beste Note ist)
     * @param kurzbezeichnung Optionale Kurzbezeichnung des Fachs
     */
    public Schulfach(String bezeichnung, int note, String kurzbezeichnung) {
        this.bezeichnung = bezeichnung;
        this.note = note;
        this.kurzbezeichnung = kurzbezeichnung;
    }
    
    /**
     * Konstruktor für Schulfach ohne Kurzbezeichnung
     * @param bezeichnung Die vollständige Bezeichnung des Fachs
     * @param note Die Note (1-5, wobei 1 die beste Note ist)
     */
    public Schulfach(String bezeichnung, int note) {
        this(bezeichnung, note, "");
    }
    
    // Getter und Setter
    public String getBezeichnung() {
        return bezeichnung;
    }
    
    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }
    
    public int getNote() {
        return note;
    }
    
    public void setNote(int note) {
        if (note >= 1 && note <= 5) {
            this.note = note;
        } else {
            throw new IllegalArgumentException("Note muss zwischen 1 und 5 liegen!");
        }
    }
    
    public String getKurzbezeichnung() {
        return kurzbezeichnung;
    }
    
    public void setKurzbezeichnung(String kurzbezeichnung) {
        this.kurzbezeichnung = kurzbezeichnung;
    }
    
    /**
     * String-Repräsentation des Schulfachs
     * @return Formatierte Ausgabe des Schulfachs
     */
    @Override
    public String toString() {
        if (kurzbezeichnung != null && !kurzbezeichnung.isEmpty()) {
            return String.format("%-25s | %-3s | %d", bezeichnung, kurzbezeichnung, note);
        } else {
            return String.format("%-25s | %d", bezeichnung, note);
        }
    }
} 
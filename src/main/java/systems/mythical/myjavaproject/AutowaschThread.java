package systems.mythical.myjavaproject;

/**
 * Thread-Klasse für die automatische Abarbeitung der Autowaschanlage
 * Wäscht alle 15 Sekunden ein Auto aus der Warteschlange
 */
public class AutowaschThread implements Runnable {
    private Warteschlange warteschlange;
    private boolean isRunning;
    private int gewascheneFahrzeuge;
    
    /**
     * Konstruktor für den AutowaschThread
     * @param warteschlange Die zu überwachende Warteschlange
     */
    public AutowaschThread(Warteschlange warteschlange) {
        this.warteschlange = warteschlange;
        this.isRunning = false;
        this.gewascheneFahrzeuge = 0;
    }
    
    /**
     * Run-Methode des Threads - wird automatisch ausgeführt
     */
    @Override
    public void run() {
        isRunning = true;
        System.out.println("\n🚿 Autowaschanlage gestartet! Wäscht alle 15 Sekunden ein Auto...");
        
        try {
            while (isRunning) {
                Thread.sleep(15000); // 15 Sekunden warten
                
                if (!warteschlange.istLeer()) {
                    Auto auto = warteschlange.naechstesAutoWaschen();
                    if (auto != null) {
                        gewascheneFahrzeuge++;
                        System.out.printf("\n🚗 Auto gewaschen: %s (Fahrzeug #%d)%n", 
                                        auto.getKennzeichen(), gewascheneFahrzeuge);
                        
                        // Zusätzliche Info über verbleibendes Autos
                        if (warteschlange.getAnzahlAutos() > 0) {
                            System.out.printf("⏳ Wartende Autos: %d%n", warteschlange.getAnzahlAutos());
                        } else {
                            System.out.println("✅ Warteschlange ist jetzt leer!");
                        }
                    }
                } else {
                    System.out.println("⏳ Warteschlange ist leer - warte auf Autos...");
                }
            }
        } catch (InterruptedException e) {
            System.out.printf("\n🛑 Autowaschanlage gestoppt! Insgesamt gewaschen: %d Fahrzeuge%n", 
                            gewascheneFahrzeuge);
        } catch (Exception e) {
            System.out.println("❌ Fehler in der Autowaschanlage: " + e.getMessage());
            e.printStackTrace();
        } finally {
            isRunning = false;
        }
    }
    
    /**
     * Stoppt den Thread sanft
     */
    public void stop() {
        isRunning = false;
    }
    
    /**
     * Überprüft ob der Thread läuft
     * @return true wenn der Thread aktiv ist
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Gibt die Anzahl der gewaschenen Fahrzeuge zurück
     * @return Anzahl der gewaschenen Fahrzeuge
     */
    public int getGewascheneFahrzeuge() {
        return gewascheneFahrzeuge;
    }
    
    /**
     * Setzt den Zähler für gewaschene Fahrzeuge zurück
     */
    public void resetZaehler() {
        gewascheneFahrzeuge = 0;
    }
} 
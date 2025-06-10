package systems.mythical.myjavaproject;

import java.util.Scanner;

/**
 * Hauptklasse für die Autowaschanlage-Anwendung
 * Bietet sowohl menügesteuerte als auch threadbasierte Funktionalität
 */
public class AutowaschanlageAnwendung {
    private Warteschlange warteschlange;
    private Scanner scanner;
    private Thread waschThread;
    private AutowaschThread autowaschRunnable;
    
    /**
     * Konstruktor - initialisiert die Anwendung
     */
    public AutowaschanlageAnwendung() {
        this.warteschlange = new Warteschlange();
        this.scanner = new Scanner(System.in);
        this.waschThread = null;
        this.autowaschRunnable = null;
    }
    
    /**
     * Startet die Anwendung
     */
    public void start() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚿   WILLKOMMEN ZUR AUTOWASCHANLAGE-VERWALTUNG   🚗");
        System.out.println("=".repeat(60));
        
        // Auswahl zwischen manueller und automatischer Steuerung
        auswahlBetriebsmodus();
    }
    
    /**
     * Lässt den Benutzer zwischen manuellem und automatischem Modus wählen
     */
    private void auswahlBetriebsmodus() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("BETRIEBSMODUS AUSWÄHLEN");
            System.out.println("=".repeat(40));
            System.out.println("1️⃣  Manueller Modus (Menügesteuert)");
            System.out.println("2️⃣  Automatischer Modus (Thread-gesteuert)");
            System.out.println("0️⃣  Beenden");
            System.out.println("=".repeat(40));
            System.out.print("Bitte wählen Sie eine Option (0-2): ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Buffer leeren
                
                switch (choice) {
                    case 1 -> startManuellerModus();
                    case 2 -> startAutomatischerModus();
                    case 0 -> {
                        System.out.println("\n👋 Auf Wiedersehen!");
                        return;
                    }
                    default -> System.out.println("❌ Ungültige Auswahl! Bitte 0-2 eingeben.");
                }
            } catch (Exception e) {
                System.out.println("❌ Ungültige Eingabe! Bitte eine Zahl eingeben.");
                scanner.nextLine(); // Buffer leeren
            }
        }
    }
    
    /**
     * Startet den manuellen Modus mit Menüsteuerung
     */
    private void startManuellerModus() {
        System.out.println("\n🔧 Manueller Modus gestartet!");
        
        while (true) {
            anzeigenHauptmenu();
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Buffer leeren
                
                switch (choice) {
                    case 1 -> autoHinzufuegen();
                    case 2 -> autoWaschen();
                    case 3 -> autoEntfernen();
                    case 4 -> warteschlange.warteschlangeAnzeigen();
                    case 5 -> warteschlange.statistikAnzeigen();
                    case 6 -> testdatenErstellen();
                    case 7 -> warteschlangeLeeren();
                    case 0 -> {
                        System.out.println("🔙 Zurück zum Betriebsmodus-Menü...");
                        return;
                    }
                    default -> System.out.println("❌ Ungültige Auswahl! Bitte 0-7 eingeben.");
                }
                
                if (choice != 0) {
                    System.out.println("\n⏸️  Drücken Sie Enter um fortzufahren...");
                    scanner.nextLine();
                }
                
            } catch (Exception e) {
                System.out.println("❌ Ungültige Eingabe! Bitte eine Zahl eingeben.");
                scanner.nextLine(); // Buffer leeren
            }
        }
    }
    
    /**
     * Startet den automatischen Modus mit Thread
     */
    private void startAutomatischerModus() {
        System.out.println("\n🤖 Automatischer Modus gestartet!");
        
        while (true) {
            anzeigenAutomatikMenu();
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Buffer leeren
                
                switch (choice) {
                    case 1 -> automatikStarten();
                    case 2 -> automatikStoppen();
                    case 3 -> autoHinzufuegen();
                    case 4 -> autoEntfernen();
                    case 5 -> warteschlange.warteschlangeAnzeigen();
                    case 6 -> automatikStatus();
                    case 7 -> testdatenErstellen();
                    case 0 -> {
                        automatikStoppen(); // Sicherheitshalber stoppen
                        System.out.println("🔙 Zurück zum Betriebsmodus-Menü...");
                        return;
                    }
                    default -> System.out.println("❌ Ungültige Auswahl! Bitte 0-7 eingeben.");
                }
                
                if (choice != 0) {
                    System.out.println("\n⏸️  Drücken Sie Enter um fortzufahren...");
                    scanner.nextLine();
                }
                
            } catch (Exception e) {
                System.out.println("❌ Ungültige Eingabe! Bitte eine Zahl eingeben.");
                scanner.nextLine(); // Buffer leeren
            }
        }
    }
    
    /**
     * Zeigt das Hauptmenü für den manuellen Modus an
     */
    private void anzeigenHauptmenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🚿 AUTOWASCHANLAGE - MANUELLER MODUS");
        System.out.println("=".repeat(50));
        System.out.println("1️⃣  Auto zur Warteschlange hinzufügen");
        System.out.println("2️⃣  Nächstes Auto waschen");
        System.out.println("3️⃣  Auto aus Warteschlange entfernen");
        System.out.println("4️⃣  Warteschlange anzeigen");
        System.out.println("5️⃣  Statistiken anzeigen");
        System.out.println("6️⃣  Testdaten erstellen");
        System.out.println("7️⃣  Warteschlange leeren");
        System.out.println("0️⃣  Zurück zum Hauptmenü");
        System.out.println("=".repeat(50));
        System.out.print("Bitte wählen Sie eine Option (0-7): ");
    }
    
    /**
     * Zeigt das Menü für den automatischen Modus an
     */
    private void anzeigenAutomatikMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🤖 AUTOWASCHANLAGE - AUTOMATISCHER MODUS");
        System.out.println("=".repeat(50));
        System.out.println("1️⃣  Automatik starten");
        System.out.println("2️⃣  Automatik stoppen");
        System.out.println("3️⃣  Auto zur Warteschlange hinzufügen");
        System.out.println("4️⃣  Auto aus Warteschlange entfernen");
        System.out.println("5️⃣  Warteschlange anzeigen");
        System.out.println("6️⃣  Automatik-Status");
        System.out.println("7️⃣  Testdaten erstellen");
        System.out.println("0️⃣  Zurück zum Hauptmenü");
        System.out.println("=".repeat(50));
        System.out.print("Bitte wählen Sie eine Option (0-7): ");
    }
    
    /**
     * Fügt ein Auto zur Warteschlange hinzu
     */
    private void autoHinzufuegen() {
        System.out.println("\n➕ AUTO HINZUFÜGEN");
        System.out.println("-".repeat(30));
        
        System.out.print("Kennzeichen eingeben: ");
        String kennzeichen = scanner.nextLine().trim().toUpperCase();
        
        if (kennzeichen.isEmpty()) {
            System.out.println("❌ Kennzeichen darf nicht leer sein!");
            return;
        }
        
        System.out.print("Fahrzeugtyp (PKW/SUV/LKW) [PKW]: ");
        String fahrzeugtyp = scanner.nextLine().trim();
        if (fahrzeugtyp.isEmpty()) fahrzeugtyp = "PKW";
        
        System.out.print("Farbe [unbekannt]: ");
        String farbe = scanner.nextLine().trim();
        if (farbe.isEmpty()) farbe = "unbekannt";
        
        Auto neuesAuto = new Auto(kennzeichen, fahrzeugtyp, farbe);
        warteschlange.autoHinzufuegen(neuesAuto);
    }
    
    /**
     * Wäscht das nächste Auto manuell
     */
    private void autoWaschen() {
        System.out.println("\n🚿 AUTO WASCHEN");
        System.out.println("-".repeat(30));
        
        Auto auto = warteschlange.naechstesAutoWaschen();
        if (auto != null) {
            System.out.println("✅ Auto " + auto.getKennzeichen() + " wurde erfolgreich gewaschen!");
        }
    }
    
    /**
     * Entfernt ein Auto aus der Warteschlange
     */
    private void autoEntfernen() {
        System.out.println("\n➖ AUTO ENTFERNEN");
        System.out.println("-".repeat(30));
        
        if (warteschlange.istLeer()) {
            System.out.println("❌ Warteschlange ist leer!");
            return;
        }
        
        System.out.print("Kennzeichen des zu entfernenden Autos: ");
        String kennzeichen = scanner.nextLine().trim();
        warteschlange.autoEntfernen(kennzeichen);
    }
    
    /**
     * Startet die automatische Waschanlage
     */
    private void automatikStarten() {
        if (waschThread != null && waschThread.isAlive()) {
            System.out.println("⚠️  Automatik läuft bereits!");
            return;
        }
        
        autowaschRunnable = new AutowaschThread(warteschlange);
        waschThread = new Thread(autowaschRunnable);
        waschThread.start();
        
        System.out.println("✅ Automatische Waschanlage gestartet!");
        System.out.println("💡 Hinweis: Die Anlage wäscht alle 15 Sekunden ein Auto.");
    }
    
    /**
     * Stoppt die automatische Waschanlage
     */
    private void automatikStoppen() {
        if (waschThread == null || !waschThread.isAlive()) {
            System.out.println("⚠️  Automatik läuft nicht!");
            return;
        }
        
        waschThread.interrupt();
        try {
            waschThread.join(1000); // 1 Sekunde warten
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("🛑 Automatische Waschanlage gestoppt!");
    }
    
    /**
     * Zeigt den Status der automatischen Waschanlage an
     */
    private void automatikStatus() {
        System.out.println("\n📊 AUTOMATIK-STATUS");
        System.out.println("-".repeat(30));
        
        if (waschThread == null || !waschThread.isAlive()) {
            System.out.println("Status: ⏹️  Gestoppt");
        } else {
            System.out.println("Status: ▶️  Läuft");
            if (autowaschRunnable != null) {
                System.out.println("Gewaschene Fahrzeuge: " + autowaschRunnable.getGewascheneFahrzeuge());
            }
        }
        
        System.out.println("Wartende Autos: " + warteschlange.getAnzahlAutos());
        System.out.println("Freie Plätze: " + (warteschlange.getMaxKapazitaet() - warteschlange.getAnzahlAutos()));
    }
    
    /**
     * Erstellt Testdaten für Demonstrationszwecke
     */
    private void testdatenErstellen() {
        System.out.println("\n🧪 TESTDATEN ERSTELLEN");
        System.out.println("-".repeat(30));
        
        String[] kennzeichen = {"W-TEST1", "W-TEST2", "W-ABC123", "W-DEF456", "W-GHI789"};
        String[] fahrzeugtypen = {"PKW", "SUV", "LKW", "PKW", "SUV"};
        String[] farben = {"Rot", "Blau", "Grün", "Schwarz", "Weiß"};
        
        int hinzugefuegt = 0;
        for (int i = 0; i < kennzeichen.length && !warteschlange.istVoll(); i++) {
            Auto testAuto = new Auto(kennzeichen[i], fahrzeugtypen[i], farben[i]);
            if (warteschlange.autoHinzufuegen(testAuto)) {
                hinzugefuegt++;
            }
        }
        
        System.out.println("✅ " + hinzugefuegt + " Testautos hinzugefügt!");
    }
    
    /**
     * Leert die komplette Warteschlange
     */
    private void warteschlangeLeeren() {
        System.out.println("\n🗑️  WARTESCHLANGE LEEREN");
        System.out.println("-".repeat(30));
        
        if (warteschlange.istLeer()) {
            System.out.println("⚠️  Warteschlange ist bereits leer!");
            return;
        }
        
        System.out.print("Sind Sie sicher? (j/n): ");
        String bestaetigung = scanner.nextLine().trim().toLowerCase();
        
        if (bestaetigung.equals("j") || bestaetigung.equals("ja")) {
            int anzahl = warteschlange.getAnzahlAutos();
            warteschlange = new Warteschlange(); // Neue leere Warteschlange
            System.out.println("✅ Warteschlange geleert! " + anzahl + " Autos entfernt.");
        } else {
            System.out.println("❌ Vorgang abgebrochen.");
        }
    }
    
    /**
     * Beendet die Anwendung sauber
     */
    public void beenden() {
        if (waschThread != null && waschThread.isAlive()) {
            automatikStoppen();
        }
        scanner.close();
        System.out.println("👋 Anwendung beendet. Auf Wiedersehen!");
    }
} 
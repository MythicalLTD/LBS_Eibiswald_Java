package systems.mythical.myjavaproject;

import java.util.Scanner;

/**
 * Hauptklasse für die TicTacToe-Anwendung
 * Verwaltet die Benutzerinteraktion und das Spielmenü
 */
public class TicTacToeAnwendung {
    private TicTacToe spiel;
    private TicTacToeAI ai;
    private Scanner scanner;
    private boolean aiMode;
    
    /**
     * Konstruktor - Initialisiert die Anwendung
     */
    public TicTacToeAnwendung() {
        this.spiel = new TicTacToe();
        this.ai = new TicTacToeAI();
        this.scanner = new Scanner(System.in);
        this.aiMode = false;
    }
    
    /**
     * Startet die TicTacToe-Anwendung
     */
    public void start() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🎮   WILLKOMMEN ZU TIC TAC TOE   🎮");
        System.out.println("=".repeat(50));
        
        TicTacToe.showRules();
        
        boolean spielen = true;
        while (spielen) {
            spielen = hauptmenu();
        }
        
        beenden();
    }
    
    /**
     * Zeigt das Hauptmenü an und verarbeitet die Auswahl
     * @return true um fortzufahren, false um zu beenden
     */
    private boolean hauptmenu() {
        System.out.println("\n" + "=".repeat(35));
        System.out.println("         HAUPTMENÜ");
        System.out.println("=".repeat(35));
        System.out.println("1️⃣  Neues Spiel starten");
        System.out.println("2️⃣  Spielregeln anzeigen");
        System.out.println("3️⃣  KI-Einstellungen");
        System.out.println("0️⃣  Beenden");
        System.out.println("=".repeat(35));
        System.out.print("Bitte wählen Sie eine Option (0-3): ");
        
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer leeren
            
            switch (choice) {
                case 1 -> {
                    spielModusWaehlen();
                    return true;
                }
                case 2 -> {
                    TicTacToe.showRules();
                    pausieren();
                    return true;
                }
                case 3 -> {
                    aiEinstellungen();
                    return true;
                }
                case 0 -> {
                    return false;
                }
                default -> {
                    System.out.println("❌ Ungültige Auswahl! Bitte 0-3 eingeben.");
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Ungültige Eingabe! Bitte eine Zahl eingeben.");
            scanner.nextLine(); // Buffer leeren
            return true;
        }
    }
    
    /**
     * Lässt den Benutzer den Spielmodus wählen
     */
    private void spielModusWaehlen() {
        System.out.println("\n" + "=".repeat(35));
        System.out.println("        SPIELMODUS");
        System.out.println("=".repeat(35));
        System.out.println("1️⃣  Mensch vs Mensch");
        System.out.println("2️⃣  Mensch vs KI");
        System.out.println("3️⃣  KI vs KI (Demo)");
        System.out.println("0️⃣  Zurück zum Hauptmenü");
        System.out.println("=".repeat(35));
        System.out.print("Bitte wählen Sie einen Modus (0-3): ");
        
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer leeren
            
            switch (choice) {
                case 1 -> {
                    aiMode = false;
                    spielStarten();
                }
                case 2 -> {
                    aiMode = true;
                    spielStarten();
                }
                case 3 -> {
                    aiDemo();
                }
                case 0 -> {
                    // Zurück zum Hauptmenü
                }
                default -> {
                    System.out.println("❌ Ungültige Auswahl! Bitte 0-3 eingeben.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Ungültige Eingabe! Bitte eine Zahl eingeben.");
            scanner.nextLine(); // Buffer leeren
        }
    }
    
    /**
     * Zeigt KI-Einstellungen an und ermöglicht Änderungen
     */
    private void aiEinstellungen() {
        System.out.println("\n" + "=".repeat(35));
        System.out.println("      KI-EINSTELLUNGEN");
        System.out.println("=".repeat(35));
        System.out.println("Aktuelle KI: " + ai.getDescription());
        System.out.println();
        TicTacToeAI.showDifficulties();
        System.out.println("4️⃣  Zurück zum Hauptmenü");
        System.out.println("=".repeat(35));
        System.out.print("Wählen Sie eine Schwierigkeit (1-4): ");
        
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer leeren
            
            switch (choice) {
                case 1 -> {
                    ai.setDifficulty(TicTacToeAI.Difficulty.EASY);
                    System.out.println("✅ KI-Schwierigkeit auf 'Leicht' gesetzt!");
                }
                case 2 -> {
                    ai.setDifficulty(TicTacToeAI.Difficulty.MEDIUM);
                    System.out.println("✅ KI-Schwierigkeit auf 'Mittel' gesetzt!");
                }
                case 3 -> {
                    ai.setDifficulty(TicTacToeAI.Difficulty.HARD);
                    System.out.println("✅ KI-Schwierigkeit auf 'Schwer' gesetzt!");
                }
                case 4 -> {
                    // Zurück zum Hauptmenü
                }
                default -> {
                    System.out.println("❌ Ungültige Auswahl! Bitte 1-4 eingeben.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Ungültige Eingabe! Bitte eine Zahl eingeben.");
            scanner.nextLine(); // Buffer leeren
        }
    }
    
    /**
     * Startet ein neues Spiel und verwaltet den Spielablauf
     */
    private void spielStarten() {
        spiel.newGame();
        
        if (aiMode) {
            System.out.println("\n🎯 Spiel gegen KI gestartet! Sie sind Spieler 1 (X).");
            System.out.println(ai.getDescription());
        } else {
            System.out.println("\n🎯 Spiel gestartet! Spieler 1 (X) beginnt.");
        }
        
        // Hauptspielschleife
        while (!spiel.isGameEnded()) {
            // Aktuelles Spielbrett anzeigen
            spiel.displayBoard();
            
            // Spielerinformationen anzeigen
            System.out.println("🎲 Spieler " + spiel.getCurrentPlayerNumber() + 
                             " (" + spiel.getCurrentPlayer() + ") ist am Zug");
            
            // Verfügbare Felder anzeigen
            spiel.showAvailablePositions();
            
            // Spielzug einlesen oder KI-Zug ausführen
            if (aiMode && spiel.getCurrentPlayer() == 'O') {
                // KI-Zug
                try {
                    Thread.sleep(1000); // Kurze Pause für bessere UX
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                spiel.makeAIMove(ai);
            } else {
                // Menschlicher Spieler
                int position = eingabePosition();
                
                if (position == -1) {
                    // Spieler möchte zurück zum Hauptmenü
                    System.out.println("🔙 Spiel abgebrochen. Zurück zum Hauptmenü...");
                    return;
                }
                
                // Spielzug ausführen
                spiel.makeMove(position);
            }
            
            if (spiel.isGameEnded()) {
                // Finales Spielbrett anzeigen
                spiel.displayBoard();
                // Ergebnis anzeigen
                spiel.displayResult();
                
                // Nachspiel-Optionen
                nachspielOptionen();
            }
        }
    }
    
    /**
     * Führt eine KI vs KI Demo aus
     */
    private void aiDemo() {
        System.out.println("\n🤖 KI vs KI DEMO");
        System.out.println("=".repeat(20));
        System.out.println(ai.getDescription());
        
        spiel.newGame();
        
        while (!spiel.isGameEnded()) {
            spiel.displayBoard();
            System.out.println("🎲 Spieler " + spiel.getCurrentPlayerNumber() + 
                             " (" + spiel.getCurrentPlayer() + ") ist am Zug");
            
            try {
                Thread.sleep(2000); // 2 Sekunden warten
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            spiel.makeAIMove(ai);
            
            if (spiel.isGameEnded()) {
                spiel.displayBoard();
                spiel.displayResult();
                break;
            }
        }
        
        pausieren();
    }
    
    /**
     * Liest eine Position vom Benutzer ein
     * @return Position 1-9 oder -1 für Abbruch
     */
    private int eingabePosition() {
        while (true) {
            System.out.print("Feld auswählen (1-9) oder 0 für Hauptmenü: ");
            
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // Buffer leeren
                
                if (input == 0) {
                    return -1; // Abbruch
                }
                
                if (input >= 1 && input <= 9) {
                    if (spiel.isPositionAvailable(input)) {
                        return input;
                    } else {
                        System.out.println("❌ Feld " + input + " ist bereits belegt! Wählen Sie ein anderes Feld.");
                    }
                } else {
                    System.out.println("❌ Ungültige Eingabe! Bitte eine Zahl zwischen 1 und 9 eingeben.");
                }
                
            } catch (Exception e) {
                System.out.println("❌ Ungültige Eingabe! Bitte eine Zahl eingeben.");
                scanner.nextLine(); // Buffer leeren
            }
        }
    }
    
    /**
     * Zeigt Optionen nach dem Spiel an
     */
    private void nachspielOptionen() {
        System.out.println("\n" + "=".repeat(30));
        System.out.println("      WAS MÖCHTEN SIE TUN?");
        System.out.println("=".repeat(30));
        System.out.println("1️⃣  Neues Spiel");
        System.out.println("2️⃣  Zurück zum Hauptmenü");
        System.out.println("=".repeat(30));
        System.out.print("Ihre Wahl (1-2): ");
        
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer leeren
            
            switch (choice) {
                case 1 -> {
                    System.out.println("\n🔄 Starte neues Spiel...");
                    spielStarten();
                }
                case 2 -> {
                    System.out.println("\n🔙 Zurück zum Hauptmenü...");
                }
                default -> {
                    System.out.println("❌ Ungültige Auswahl! Zurück zum Hauptmenü...");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Ungültige Eingabe! Zurück zum Hauptmenü...");
            scanner.nextLine(); // Buffer leeren
        }
    }
    
    /**
     * Pausiert die Ausführung bis der Benutzer Enter drückt
     */
    private void pausieren() {
        System.out.println("\n⏸️  Drücken Sie Enter um fortzufahren...");
        scanner.nextLine();
    }
    
    /**
     * Beendet die Anwendung sauber
     */
    private void beenden() {
        scanner.close();
        System.out.println("\n" + "=".repeat(40));
        System.out.println("🎮 Danke fürs Spielen!");
        System.out.println("👋 Auf Wiedersehen!");
        System.out.println("=".repeat(40));
    }
    
    /**
     * Führt eine Demo des Spiels aus (für Testzwecke)
     */
    public void demo() {
        System.out.println("\n🎬 TIC TAC TOE DEMO");
        System.out.println("=".repeat(25));
        
        spiel.newGame();
        
        // Demonstriere einige Züge
        int[] demoZuege = {1, 5, 2, 6, 3}; // Spieler 1 gewinnt in der ersten Reihe
        
        for (int zug : demoZuege) {
            spiel.displayBoard();
            System.out.println("🎲 Spieler " + spiel.getCurrentPlayerNumber() + 
                             " (" + spiel.getCurrentPlayer() + ") wählt Feld " + zug);
            
            spiel.makeMove(zug);
            
            try {
                Thread.sleep(1500); // 1,5 Sekunden warten
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            if (spiel.isGameEnded()) {
                break;
            }
        }
        
        spiel.displayBoard();
        spiel.displayResult();
    }
    
    /**
     * Main-Methode - Einstiegspunkt der Anwendung
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {
        TicTacToeAnwendung app = new TicTacToeAnwendung();
        
        // Überprüfe ob Demo-Modus gewünscht ist
        if (args.length > 0 && args[0].equals("--demo")) {
            app.demo();
        } else {
            app.start();
        }
    }
} 
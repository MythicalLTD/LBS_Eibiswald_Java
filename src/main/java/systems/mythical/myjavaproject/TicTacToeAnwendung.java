package systems.mythical.myjavaproject;

import java.util.Scanner;

/**
 * Hauptklasse für die TicTacToe-Anwendung
 * Verwaltet die Benutzerinteraktion und das Spielmenü
 */
public class TicTacToeAnwendung {
    private TicTacToe spiel;
    private Scanner scanner;
    
    /**
     * Konstruktor - Initialisiert die Anwendung
     */
    public TicTacToeAnwendung() {
        this.spiel = new TicTacToe();
        this.scanner = new Scanner(System.in);
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
        System.out.println("0️⃣  Beenden");
        System.out.println("=".repeat(35));
        System.out.print("Bitte wählen Sie eine Option (0-2): ");
        
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer leeren
            
            switch (choice) {
                case 1 -> {
                    spielStarten();
                    return true;
                }
                case 2 -> {
                    TicTacToe.showRules();
                    pausieren();
                    return true;
                }
                case 0 -> {
                    return false;
                }
                default -> {
                    System.out.println("❌ Ungültige Auswahl! Bitte 0-2 eingeben.");
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
     * Startet ein neues Spiel und verwaltet den Spielablauf
     */
    private void spielStarten() {
        spiel.newGame();
        
        System.out.println("\n🎯 Spiel gestartet! Spieler 1 (X) beginnt.");
        
        // Hauptspielschleife
        while (!spiel.isGameEnded()) {
            // Aktuelles Spielbrett anzeigen
            spiel.displayBoard();
            
            // Spielerinformationen anzeigen
            System.out.println("🎲 Spieler " + spiel.getCurrentPlayerNumber() + 
                             " (" + spiel.getCurrentPlayer() + ") ist am Zug");
            
            // Verfügbare Felder anzeigen
            spiel.showAvailablePositions();
            
            // Spielzug einlesen
            int position = eingabePosition();
            
            if (position == -1) {
                // Spieler möchte zurück zum Hauptmenü
                System.out.println("🔙 Spiel abgebrochen. Zurück zum Hauptmenü...");
                return;
            }
            
            // Spielzug ausführen
            boolean erfolgreich = spiel.makeMove(position);
            
            if (erfolgreich && spiel.isGameEnded()) {
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
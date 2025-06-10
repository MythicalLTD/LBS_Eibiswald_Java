package systems.mythical.myjavaproject;

/**
 * Klasse TicTacToe - Implementiert das klassische TicTacToe-Spiel
 * Spielfeld: 3x3 Grid mit Positionen 1-9
 * Spieler 1: X, Spieler 2: O
 */
public class TicTacToe {
    private static final int BOARD_SIZE = 3;
    private static final char EMPTY = ' ';
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
    
    private char[][] board;
    private char currentPlayer;
    private boolean gameEnded;
    private char winner;
    
    /**
     * Konstruktor - Initialisiert ein neues TicTacToe-Spiel
     */
    public TicTacToe() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        currentPlayer = PLAYER_X; // Spieler 1 (X) beginnt
        gameEnded = false;
        winner = EMPTY;
        initializeBoard();
    }
    
    /**
     * Initialisiert das Spielbrett mit leeren Feldern
     */
    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = EMPTY;
            }
        }
    }
    
    /**
     * Zeigt das aktuelle Spielbrett an
     */
    public void displayBoard() {
        System.out.println("\n" + "=".repeat(15));
        System.out.println("   TIC TAC TOE");
        System.out.println("=".repeat(15));
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.print(" ");
            for (int col = 0; col < BOARD_SIZE; col++) {
                char displayChar = board[row][col];
                
                // Wenn Feld leer ist, zeige die entsprechende Nummer (1-9)
                if (displayChar == EMPTY) {
                    int position = row * BOARD_SIZE + col + 1;
                    displayChar = (char) ('0' + position);
                }
                
                System.out.print(displayChar);
                
                if (col < BOARD_SIZE - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
            
            if (row < BOARD_SIZE - 1) {
                System.out.println("-----------");
            }
        }
        System.out.println();
    }
    
    /**
     * Führt einen Spielzug aus
     * @param position Position 1-9
     * @return true wenn Zug erfolgreich, false wenn ungültig
     */
    public boolean makeMove(int position) {
        if (gameEnded) {
            System.out.println("❌ Das Spiel ist bereits beendet!");
            return false;
        }
        
        if (position < 1 || position > 9) {
            System.out.println("❌ Ungültige Position! Bitte eine Zahl zwischen 1 und 9 eingeben.");
            return false;
        }
        
        // Konvertiere Position (1-9) zu Array-Indizes (0-2, 0-2)
        int row = (position - 1) / BOARD_SIZE;
        int col = (position - 1) % BOARD_SIZE;
        
        if (board[row][col] != EMPTY) {
            System.out.println("❌ Feld ist bereits belegt! Wählen Sie ein anderes Feld.");
            return false;
        }
        
        // Setze Spielermarke
        board[row][col] = currentPlayer;
        
        // Überprüfe Spielende
        checkGameEnd();
        
        // Wechsle Spieler (nur wenn Spiel noch läuft)
        if (!gameEnded) {
            currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        }
        
        return true;
    }
    
    /**
     * Überprüft ob das Spiel beendet ist (Gewinn oder Unentschieden)
     */
    private void checkGameEnd() {
        // Überprüfe alle Gewinnkombinationen
        if (checkWin()) {
            gameEnded = true;
            winner = currentPlayer;
            return;
        }
        
        // Überprüfe Unentschieden
        if (isBoardFull()) {
            gameEnded = true;
            winner = EMPTY; // Kein Gewinner = Unentschieden
        }
    }
    
    /**
     * Überprüft alle möglichen Gewinnkombinationen
     * @return true wenn der aktuelle Spieler gewonnen hat
     */
    private boolean checkWin() {
        // Überprüfe Reihen
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][0] == currentPlayer && 
                board[row][1] == currentPlayer && 
                board[row][2] == currentPlayer) {
                return true;
            }
        }
        
        // Überprüfe Spalten
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board[0][col] == currentPlayer && 
                board[1][col] == currentPlayer && 
                board[2][col] == currentPlayer) {
                return true;
            }
        }
        
        // Überprüfe Diagonalen
        // Hauptdiagonale (oben-links nach unten-rechts)
        if (board[0][0] == currentPlayer && 
            board[1][1] == currentPlayer && 
            board[2][2] == currentPlayer) {
            return true;
        }
        
        // Nebendiagonale (oben-rechts nach unten-links)
        if (board[0][2] == currentPlayer && 
            board[1][1] == currentPlayer && 
            board[2][0] == currentPlayer) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Überprüft ob das Spielbrett voll ist
     * @return true wenn alle Felder belegt sind
     */
    private boolean isBoardFull() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Überprüft ob ein bestimmtes Feld verfügbar ist
     * @param position Position 1-9
     * @return true wenn Feld verfügbar
     */
    public boolean isPositionAvailable(int position) {
        if (position < 1 || position > 9) {
            return false;
        }
        
        int row = (position - 1) / BOARD_SIZE;
        int col = (position - 1) % BOARD_SIZE;
        
        return board[row][col] == EMPTY;
    }
    
    /**
     * Zeigt verfügbare Positionen an
     */
    public void showAvailablePositions() {
        System.out.print("Verfügbare Felder: ");
        boolean first = true;
        
        for (int i = 1; i <= 9; i++) {
            if (isPositionAvailable(i)) {
                if (!first) {
                    System.out.print(", ");
                }
                System.out.print(i);
                first = false;
            }
        }
        System.out.println();
    }
    
    /**
     * Zeigt das Spielergebnis an
     */
    public void displayResult() {
        if (!gameEnded) {
            return;
        }
        
        System.out.println("\n" + "=".repeat(25));
        System.out.println("        SPIELENDE!");
        System.out.println("=".repeat(25));
        
        if (winner == EMPTY) {
            System.out.println("🤝 UNENTSCHIEDEN!");
            System.out.println("   Beide Spieler haben gut gespielt!");
        } else {
            int playerNumber = (winner == PLAYER_X) ? 1 : 2;
            System.out.println("🎉 SPIELER " + playerNumber + " (" + winner + ") GEWINNT!");
            System.out.println("   Herzlichen Glückwunsch!");
        }
        
        System.out.println("=".repeat(25));
    }
    
    /**
     * Startet ein neues Spiel
     */
    public void newGame() {
        initializeBoard();
        currentPlayer = PLAYER_X;
        gameEnded = false;
        winner = EMPTY;
        System.out.println("🆕 Neues Spiel gestartet!");
    }
    
    // Getter-Methoden
    public char getCurrentPlayer() {
        return currentPlayer;
    }
    
    public boolean isGameEnded() {
        return gameEnded;
    }
    
    public char getWinner() {
        return winner;
    }
    
    public int getCurrentPlayerNumber() {
        return (currentPlayer == PLAYER_X) ? 1 : 2;
    }
    
    /**
     * Zeigt Spielregeln an
     */
    public static void showRules() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("           SPIELREGELN");
        System.out.println("=".repeat(40));
        System.out.println("📋 Ziel: Drei Zeichen in einer Reihe");
        System.out.println("   (horizontal, vertikal oder diagonal)");
        System.out.println();
        System.out.println("🎮 Steuerung:");
        System.out.println("   • Wählen Sie ein Feld (1-9)");
        System.out.println("   • Spieler 1 = X, Spieler 2 = O");
        System.out.println("   • Spieler wechseln sich ab");
        System.out.println();
        System.out.println("📍 Feldnummerierung:");
        System.out.println("   1 | 2 | 3");
        System.out.println("   ---------");
        System.out.println("   4 | 5 | 6");
        System.out.println("   ---------");
        System.out.println("   7 | 8 | 9");
        System.out.println("=".repeat(40));
    }
} 
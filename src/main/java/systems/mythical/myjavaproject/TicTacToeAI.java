package systems.mythical.myjavaproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * KI-Klasse für TicTacToe mit verschiedenen Schwierigkeitsgraden
 * Implementiert Minimax-Algorithmus für intelligente Spielzüge
 */
public class TicTacToeAI {
    private static final char AI_PLAYER = 'O';
    private static final char HUMAN_PLAYER = 'X';
    private static final char EMPTY = ' ';
    
    private Difficulty difficulty;
    private Random random;
    
    /**
     * Schwierigkeitsgrade für die KI
     */
    public enum Difficulty {
        EASY("Leicht", "Zufällige Züge"),
        MEDIUM("Mittel", "Grundlegende Strategie"),
        HARD("Schwer", "Minimax-Algorithmus");
        
        private final String name;
        private final String description;
        
        Difficulty(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
    }
    
    /**
     * Konstruktor - Initialisiert die KI mit Standard-Schwierigkeit
     */
    public TicTacToeAI() {
        this.difficulty = Difficulty.MEDIUM;
        this.random = new Random();
    }
    
    /**
     * Konstruktor mit spezifischer Schwierigkeit
     * @param difficulty Schwierigkeitsgrad
     */
    public TicTacToeAI(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.random = new Random();
    }
    
    /**
     * Setzt die Schwierigkeit der KI
     * @param difficulty Neue Schwierigkeit
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    /**
     * Gibt die aktuelle Schwierigkeit zurück
     * @return Aktuelle Schwierigkeit
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }
    
    /**
     * Wählt den nächsten Zug für die KI basierend auf der Schwierigkeit
     * @param board Aktuelles Spielbrett
     * @return Position (1-9) für den nächsten Zug
     */
    public int chooseMove(char[][] board) {
        switch (difficulty) {
            case EASY:
                return chooseRandomMove(board);
            case MEDIUM:
                return chooseMediumMove(board);
            case HARD:
                return chooseBestMove(board);
            default:
                return chooseRandomMove(board);
        }
    }
    
    /**
     * Wählt einen zufälligen verfügbaren Zug (Leicht)
     * @param board Aktuelles Spielbrett
     * @return Zufällige Position
     */
    private int chooseRandomMove(char[][] board) {
        List<Integer> availableMoves = getAvailableMoves(board);
        if (availableMoves.isEmpty()) {
            return -1;
        }
        return availableMoves.get(random.nextInt(availableMoves.size()));
    }
    
    /**
     * Wählt einen Zug mit grundlegender Strategie (Mittel)
     * @param board Aktuelles Spielbrett
     * @return Strategische Position
     */
    private int chooseMediumMove(char[][] board) {
        // Versuche zu gewinnen
        int winningMove = findWinningMove(board, AI_PLAYER);
        if (winningMove != -1) {
            return winningMove;
        }
        
        // Blockiere gegnerischen Gewinnzug
        int blockingMove = findWinningMove(board, HUMAN_PLAYER);
        if (blockingMove != -1) {
            return blockingMove;
        }
        
        // Versuche das Zentrum zu besetzen
        if (board[1][1] == EMPTY) {
            return 5;
        }
        
        // Versuche eine Ecke zu besetzen
        int[] corners = {1, 3, 7, 9};
        List<Integer> availableCorners = new ArrayList<>();
        for (int corner : corners) {
            if (isPositionAvailable(board, corner)) {
                availableCorners.add(corner);
            }
        }
        if (!availableCorners.isEmpty()) {
            return availableCorners.get(random.nextInt(availableCorners.size()));
        }
        
        // Fallback: Zufälliger Zug
        return chooseRandomMove(board);
    }
    
    /**
     * Wählt den besten Zug mit Minimax-Algorithmus (Schwer)
     * @param board Aktuelles Spielbrett
     * @return Beste Position
     */
    private int chooseBestMove(char[][] board) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;
        
        List<Integer> availableMoves = getAvailableMoves(board);
        
        for (int move : availableMoves) {
            // Mache den Zug
            int row = (move - 1) / 3;
            int col = (move - 1) % 3;
            board[row][col] = AI_PLAYER;
            
            // Bewerte den Zug
            int score = minimax(board, 0, false);
            
            // Mache den Zug rückgängig
            board[row][col] = EMPTY;
            
            // Aktualisiere besten Zug
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        return bestMove;
    }
    
    /**
     * Minimax-Algorithmus für optimale Spielzüge
     * @param board Aktuelles Spielbrett
     * @param depth Aktuelle Tiefe
     * @param isMaximizing true für KI-Zug, false für Spieler-Zug
     * @return Bewertung der Position
     */
    private int minimax(char[][] board, int depth, boolean isMaximizing) {
        // Terminal-Zustände überprüfen
        if (hasWon(board, AI_PLAYER)) {
            return 10 - depth;
        }
        if (hasWon(board, HUMAN_PLAYER)) {
            return depth - 10;
        }
        if (isBoardFull(board)) {
            return 0;
        }
        
        List<Integer> availableMoves = getAvailableMoves(board);
        
        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int move : availableMoves) {
                int row = (move - 1) / 3;
                int col = (move - 1) % 3;
                board[row][col] = AI_PLAYER;
                
                int score = minimax(board, depth + 1, false);
                board[row][col] = EMPTY;
                
                bestScore = Math.max(bestScore, score);
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int move : availableMoves) {
                int row = (move - 1) / 3;
                int col = (move - 1) % 3;
                board[row][col] = HUMAN_PLAYER;
                
                int score = minimax(board, depth + 1, true);
                board[row][col] = EMPTY;
                
                bestScore = Math.min(bestScore, score);
            }
            return bestScore;
        }
    }
    
    /**
     * Findet einen Gewinnzug für einen Spieler
     * @param board Aktuelles Spielbrett
     * @param player Spieler (X oder O)
     * @return Position des Gewinnzugs oder -1
     */
    private int findWinningMove(char[][] board, char player) {
        List<Integer> availableMoves = getAvailableMoves(board);
        
        for (int move : availableMoves) {
            int row = (move - 1) / 3;
            int col = (move - 1) % 3;
            
            // Teste den Zug
            board[row][col] = player;
            if (hasWon(board, player)) {
                board[row][col] = EMPTY;
                return move;
            }
            board[row][col] = EMPTY;
        }
        
        return -1;
    }
    
    /**
     * Überprüft ob ein Spieler gewonnen hat
     * @param board Spielbrett
     * @param player Spieler
     * @return true wenn gewonnen
     */
    private boolean hasWon(char[][] board, char player) {
        // Reihen
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == player && board[row][1] == player && board[row][2] == player) {
                return true;
            }
        }
        
        // Spalten
        for (int col = 0; col < 3; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
                return true;
            }
        }
        
        // Diagonalen
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Überprüft ob das Spielbrett voll ist
     * @param board Spielbrett
     * @return true wenn voll
     */
    private boolean isBoardFull(char[][] board) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Gibt alle verfügbaren Züge zurück
     * @param board Spielbrett
     * @return Liste der verfügbaren Positionen
     */
    private List<Integer> getAvailableMoves(char[][] board) {
        List<Integer> moves = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            if (isPositionAvailable(board, i)) {
                moves.add(i);
            }
        }
        return moves;
    }
    
    /**
     * Überprüft ob eine Position verfügbar ist
     * @param board Spielbrett
     * @param position Position (1-9)
     * @return true wenn verfügbar
     */
    private boolean isPositionAvailable(char[][] board, int position) {
        if (position < 1 || position > 9) {
            return false;
        }
        
        int row = (position - 1) / 3;
        int col = (position - 1) % 3;
        
        return board[row][col] == EMPTY;
    }
    
    /**
     * Zeigt alle verfügbaren Schwierigkeitsgrade an
     */
    public static void showDifficulties() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("        KI-SCHWIERIGKEITSGRADE");
        System.out.println("=".repeat(40));
        
        for (Difficulty diff : Difficulty.values()) {
            System.out.println("🎯 " + diff.getName() + ": " + diff.getDescription());
        }
        
        System.out.println("=".repeat(40));
    }
    
    /**
     * Gibt eine Beschreibung der aktuellen KI zurück
     * @return KI-Beschreibung
     */
    public String getDescription() {
        return "🤖 KI (" + difficulty.getName() + "): " + difficulty.getDescription();
    }
} 
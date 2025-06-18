package systems.mythical.myjavaproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Optimized Client für das Netzwerk-TicTacToe-Spiel
 * Verbindet sich mit dem Server und ermöglicht Spielerinteraktion mit erweiterten Features
 */
public class TicTacToeClient {
    private static final int DEFAULT_PORT = 8080;
    private static final int RECONNECT_DELAY = 3000; // 3 seconds
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final int MOVE_TIMEOUT = 60000; // 60 seconds
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;
    private int playerNumber;
    private char playerSymbol;
    private String playerName;
    private String opponentName;
    private AtomicBoolean gameRunning;
    private AtomicBoolean connected;
    private char[][] board;
    private ExecutorService messageProcessor;
    private ScheduledExecutorService heartbeatExecutor;
    private LocalDateTime connectionTime;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private int reconnectAttempts;
    private String serverAddress;
    private int serverPort;
    
    public TicTacToeClient() {
        this.scanner = new Scanner(System.in);
        this.gameRunning = new AtomicBoolean(false);
        this.connected = new AtomicBoolean(false);
        this.board = new char[3][3];
        this.messageProcessor = Executors.newSingleThreadExecutor();
        this.heartbeatExecutor = Executors.newScheduledThreadPool(1);
        this.reconnectAttempts = 0;
        this.playerName = "Unbekannter Spieler";
        this.opponentName = "Gegner";
    }
    
    /**
     * Verbindet sich mit dem Server
     */
    public boolean connect(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.serverPort = port;
        
        while (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            try {
                log("🔗 Verbinde zum Server: " + serverAddress + ":" + port);
                
                socket = new Socket(serverAddress, port);
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
                socket.setSoTimeout(30000);
                
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                connectionTime = LocalDateTime.now();
                connected.set(true);
                reconnectAttempts = 0;
                
                log("✅ Verbindung erfolgreich hergestellt!");
                return true;
                
            } catch (IOException e) {
                reconnectAttempts++;
                logError("Verbindungsfehler (Versuch " + reconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS + "): " + e.getMessage());
                
                if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                    log("🔄 Warte " + (RECONNECT_DELAY / 1000) + " Sekunden vor erneutem Versuch...");
                    try {
                        Thread.sleep(RECONNECT_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
        }
        
        logError("❌ Maximale Anzahl von Verbindungsversuchen erreicht!");
        return false;
    }
    
    /**
     * Startet das Client-Spiel
     */
    public void startGame() {
        try {
            // Spielername eingeben
            getPlayerName();
            
            // Sende Spielername an Server
            out.println("NAME:" + playerName);
            
            // Warte auf Willkommensnachricht vom Server
            String welcomeMessage = in.readLine();
            if (welcomeMessage != null && welcomeMessage.startsWith("WELCOME:")) {
                String[] parts = welcomeMessage.split(":");
                playerNumber = Integer.parseInt(parts[1]);
                playerSymbol = parts[2].charAt(0);
                
                log("🎮 Willkommen! Sie sind Spieler " + playerNumber + " (" + playerSymbol + ")");
            }
            
            // Warte auf Spielstart
            String gameStartMessage = in.readLine();
            if ("GAME_START".equals(gameStartMessage)) {
                log("🎯 Spiel startet!");
                gameRunning.set(true);
                
                // Starte Heartbeat-Response
                startHeartbeatResponse();
                
                // Starte Message Processing
                startMessageProcessing();
                
                // Warte auf Spielende
                while (gameRunning.get() && connected.get()) {
                    Thread.sleep(100);
                }
            }
            
        } catch (IOException e) {
            logError("Spiel-Fehler: " + e.getMessage());
            handleDisconnection();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            cleanup();
        }
    }
    
    /**
     * Lässt den Spieler seinen Namen eingeben
     */
    private void getPlayerName() {
        System.out.print("👤 Geben Sie Ihren Spielernamen ein (Enter für Standard): ");
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            playerName = input;
        }
        log("Spielername: " + playerName);
    }
    
    /**
     * Startet die Heartbeat-Response
     */
    private void startHeartbeatResponse() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            if (connected.get() && out != null) {
                try {
                    out.println("HEARTBEAT_RESPONSE");
                } catch (Exception e) {
                    logError("Heartbeat-Response Fehler: " + e.getMessage());
                }
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Startet die Message Processing
     */
    private void startMessageProcessing() {
        messageProcessor.submit(() -> {
            try {
                while (gameRunning.get() && connected.get()) {
                    String message = in.readLine();
                    if (message == null) {
                        log("❌ Verbindung zum Server verloren");
                        handleDisconnection();
                        break;
                    }
                    
                    handleServerMessage(message);
                }
            } catch (IOException e) {
                logError("Kommunikationsfehler: " + e.getMessage());
                handleDisconnection();
            }
        });
    }
    
    /**
     * Behandelt Verbindungsabbrüche
     */
    private void handleDisconnection() {
        connected.set(false);
        gameRunning.set(false);
        
        log("🔌 Verbindung getrennt");
        System.out.println("\n❌ Verbindung zum Server verloren!");
        System.out.println("Möchten Sie sich erneut verbinden? (j/n): ");
        
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("j") || response.equals("ja") || response.equals("y") || response.equals("yes")) {
            log("🔄 Versuche erneute Verbindung...");
            if (connect(serverAddress, serverPort)) {
                startGame();
            }
        }
    }
    
    /**
     * Verarbeitet Nachrichten vom Server
     */
    private void handleServerMessage(String message) {
        try {
            if (message.startsWith("BOARD:")) {
                updateBoard(message.substring(6));
                displayBoard();
            } else if ("YOUR_TURN".equals(message)) {
                System.out.println("\n🎲 Sie sind am Zug!");
                makeMove();
            } else if (message.startsWith("OPPONENT_TURN:")) {
                String opponent = message.substring(14);
                System.out.println("\n⏳ " + opponent + " ist am Zug...");
            } else if ("INVALID_MOVE".equals(message)) {
                System.out.println("❌ Ungültiger Zug! Versuchen Sie es erneut.");
                makeMove();
            } else if (message.startsWith("MOVE_MADE:")) {
                String[] parts = message.split(":");
                int position = Integer.parseInt(parts[1]);
                char player = parts[2].charAt(0);
                String playerName = parts.length > 3 ? parts[3] : "Unbekannt";
                System.out.println("✅ " + playerName + " setzt auf Position " + position);
            } else if (message.startsWith("GAME_END:")) {
                handleGameEnd(message.substring(9));
            } else if ("HEARTBEAT".equals(message)) {
                // Heartbeat wird automatisch beantwortet
            } else {
                log("📨 Unbekannte Nachricht: " + message);
            }
        } catch (Exception e) {
            logError("Fehler beim Verarbeiten der Nachricht: " + e.getMessage());
        }
    }
    
    /**
     * Aktualisiert das lokale Spielbrett
     */
    private void updateBoard(String boardString) {
        String[] parts = boardString.split(":");
        String boardData = parts[0];
        
        for (int i = 0; i < 9; i++) {
            int row = i / 3;
            int col = i % 3;
            board[row][col] = boardData.charAt(i);
        }
        
        // Update player names if provided
        if (parts.length > 3) {
            if (playerNumber == 1) {
                opponentName = parts[3];
            } else {
                opponentName = parts[2];
            }
        }
    }
    
    /**
     * Zeigt das aktuelle Spielbrett an
     */
    private void displayBoard() {
        System.out.println("\n" + "=".repeat(25));
        System.out.println("   TIC TAC TOE - NETZWERK");
        System.out.println("=".repeat(25));
        System.out.println("👤 " + playerName + " (" + playerSymbol + ") vs " + opponentName);
        System.out.println("🔗 Verbunden seit: " + connectionTime.format(timeFormatter));
        System.out.println("-".repeat(25));
        
        for (int row = 0; row < 3; row++) {
            System.out.print(" ");
            for (int col = 0; col < 3; col++) {
                char displayChar = board[row][col];
                
                // Wenn Feld leer ist, zeige die entsprechende Nummer (1-9)
                if (displayChar == ' ') {
                    int position = row * 3 + col + 1;
                    displayChar = (char) ('0' + position);
                }
                
                // Farbige Darstellung
                if (displayChar == playerSymbol) {
                    System.out.print("[" + displayChar + "]");
                } else if (displayChar == (playerSymbol == 'X' ? 'O' : 'X')) {
                    System.out.print("(" + displayChar + ")");
                } else {
                    System.out.print(" " + displayChar + " ");
                }
                
                if (col < 2) {
                    System.out.print("|");
                }
            }
            System.out.println();
            
            if (row < 2) {
                System.out.println("---+---+---");
            }
        }
        System.out.println("-".repeat(25));
    }
    
    /**
     * Lässt den Spieler einen Zug machen
     */
    private void makeMove() {
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < MOVE_TIMEOUT) {
            System.out.print("🎯 Position (1-9) oder 'q' zum Beenden: ");
            
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("q")) {
                    out.println("QUIT");
                    gameRunning.set(false);
                    return;
                }
                
                try {
                    int position = Integer.parseInt(input);
                    if (position >= 1 && position <= 9) {
                        out.println("MOVE:" + position);
                        return;
                    } else {
                        System.out.println("❌ Position muss zwischen 1 und 9 liegen!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Bitte geben Sie eine gültige Zahl ein!");
                }
            }
        }
        
        System.out.println("⏰ Zeitüberschreitung! Automatischer Zug wird gesendet.");
        // Automatischer Zug auf erstes freies Feld
        for (int i = 1; i <= 9; i++) {
            if (isPositionAvailable(i)) {
                out.println("MOVE:" + i);
                return;
            }
        }
    }
    
    /**
     * Überprüft ob eine Position verfügbar ist
     */
    private boolean isPositionAvailable(int position) {
        if (position < 1 || position > 9) return false;
        
        int row = (position - 1) / 3;
        int col = (position - 1) % 3;
        
        return board[row][col] == ' ';
    }
    
    /**
     * Behandelt das Spielende
     */
    private void handleGameEnd(String result) {
        displayBoard();
        
        String[] parts = result.split(":");
        String gameResult = parts[0];
        String winner = parts.length > 1 ? parts[1] : "Unbekannt";
        
        System.out.println("\n" + "=".repeat(30));
        System.out.println("        SPIELENDE");
        System.out.println("=".repeat(30));
        
        switch (gameResult) {
            case "DRAW" -> {
                System.out.println("🤝 Unentschieden!");
                System.out.println("Beide Spieler haben gleich gut gespielt.");
            }
            case "PLAYER1_WINS" -> {
                if (playerNumber == 1) {
                    System.out.println("🏆 Sie haben gewonnen!");
                    System.out.println("Glückwunsch, " + playerName + "!");
                } else {
                    System.out.println("😔 Sie haben verloren!");
                    System.out.println(winner + " war stärker.");
                }
            }
            case "PLAYER2_WINS" -> {
                if (playerNumber == 2) {
                    System.out.println("🏆 Sie haben gewonnen!");
                    System.out.println("Glückwunsch, " + playerName + "!");
                } else {
                    System.out.println("😔 Sie haben verloren!");
                    System.out.println(winner + " war stärker.");
                }
            }
        }
        
        System.out.println("=".repeat(30));
        gameRunning.set(false);
    }
    
    /**
     * Loggt eine Nachricht mit Zeitstempel
     */
    private void log(String message) {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        System.out.println("[" + timestamp + "] " + message);
    }
    
    /**
     * Loggt einen Fehler mit Zeitstempel
     */
    private void logError(String message) {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        System.err.println("[" + timestamp + "] ❌ " + message);
    }
    
    /**
     * Bereinigt alle Ressourcen
     */
    private void cleanup() {
        gameRunning.set(false);
        connected.set(false);
        
        try {
            if (heartbeatExecutor != null) {
                heartbeatExecutor.shutdown();
                if (!heartbeatExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    heartbeatExecutor.shutdownNow();
                }
            }
            
            if (messageProcessor != null) {
                messageProcessor.shutdown();
                if (!messageProcessor.awaitTermination(5, TimeUnit.SECONDS)) {
                    messageProcessor.shutdownNow();
                }
            }
            
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            if (scanner != null) scanner.close();
            
            log("🧹 Client-Ressourcen bereinigt");
        } catch (Exception e) {
            logError("Fehler beim Aufräumen: " + e.getMessage());
        }
    }
    
    /**
     * Hauptmethode zum Starten des Clients
     */
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = DEFAULT_PORT;
        
        if (args.length > 0) {
            serverAddress = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("❌ Ungültiger Port. Verwende Standard-Port " + DEFAULT_PORT);
            }
        }
        
        TicTacToeClient client = new TicTacToeClient();
        if (client.connect(serverAddress, port)) {
            client.startGame();
        }
    }
} 
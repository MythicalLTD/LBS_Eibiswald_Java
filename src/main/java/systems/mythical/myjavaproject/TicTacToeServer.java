package systems.mythical.myjavaproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Optimized Server für das Netzwerk-TicTacToe-Spiel
 * Verwaltet die Verbindung zwischen zwei Spielern mit erweiterten Features
 */
public class TicTacToeServer {
    private static final int DEFAULT_PORT = 8080;
    private static final int HEARTBEAT_INTERVAL = 5000; // 5 seconds
    private static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    private static final int MAX_RECONNECT_ATTEMPTS = 3;
    
    private ServerSocket serverSocket;
    private Socket player1Socket;
    private Socket player2Socket;
    private PrintWriter player1Out;
    private PrintWriter player2Out;
    private BufferedReader player1In;
    private BufferedReader player2In;
    private TicTacToe game;
    private AtomicBoolean gameRunning;
    private AtomicBoolean serverRunning;
    private ExecutorService executorService;
    private ScheduledExecutorService heartbeatExecutor;
    private AtomicInteger player1Heartbeat;
    private AtomicInteger player2Heartbeat;
    private String player1Name;
    private String player2Name;
    private LocalDateTime gameStartTime;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public TicTacToeServer() {
        this.game = new TicTacToe();
        this.gameRunning = new AtomicBoolean(false);
        this.serverRunning = new AtomicBoolean(false);
        this.executorService = Executors.newCachedThreadPool();
        this.heartbeatExecutor = Executors.newScheduledThreadPool(2);
        this.player1Heartbeat = new AtomicInteger(0);
        this.player2Heartbeat = new AtomicInteger(0);
        this.player1Name = "Spieler 1";
        this.player2Name = "Spieler 2";
    }
    
    /**
     * Startet den Server und wartet auf zwei Spieler
     */
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(0); // No timeout for accept
            serverRunning.set(true);
            
            log("🎮 TicTacToe Server gestartet auf Port " + port);
            log("📍 Server IP: " + getServerIP());
            log("⏳ Warte auf Spieler 1...");
            
            // Warte auf ersten Spieler
            player1Socket = serverSocket.accept();
            setupPlayerConnection(player1Socket, 1);
            log("✅ Spieler 1 verbunden: " + player1Socket.getInetAddress() + " (" + player1Name + ")");
            
            log("⏳ Warte auf Spieler 2...");
            
            // Warte auf zweiten Spieler
            player2Socket = serverSocket.accept();
            setupPlayerConnection(player2Socket, 2);
            log("✅ Spieler 2 verbunden: " + player2Socket.getInetAddress() + " (" + player2Name + ")");
            
            // Starte Heartbeat-Monitoring
            startHeartbeatMonitoring();
            
            // Beide Spieler benachrichtigen, dass das Spiel startet
            broadcastMessage("GAME_START");
            gameStartTime = LocalDateTime.now();
            log("🎯 Spiel gestartet um " + gameStartTime.format(timeFormatter) + "! Beide Spieler sind verbunden.");
            
            // Starte das Spiel
            gameRunning.set(true);
            runGame();
            
        } catch (IOException e) {
            logError("Server-Fehler: " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    /**
     * Richtet eine Spielerverbindung ein
     */
    private void setupPlayerConnection(Socket socket, int playerNumber) throws IOException {
        socket.setTcpNoDelay(true); // Disable Nagle's algorithm for real-time gaming
        socket.setKeepAlive(true);
        socket.setSoTimeout(CONNECTION_TIMEOUT);
        
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        if (playerNumber == 1) {
            player1Out = out;
            player1In = in;
        } else {
            player2Out = out;
            player2In = in;
        }
        
        // Sende Willkommensnachricht
        String welcomeMsg = "WELCOME:" + playerNumber + ":" + (playerNumber == 1 ? "X" : "O");
        out.println(welcomeMsg);
        
        // Warte auf Spielername
        String nameMsg = in.readLine();
        if (nameMsg != null && nameMsg.startsWith("NAME:")) {
            String playerName = nameMsg.substring(5);
            if (playerNumber == 1) {
                player1Name = playerName.isEmpty() ? "Spieler 1" : playerName;
            } else {
                player2Name = playerName.isEmpty() ? "Spieler 2" : playerName;
            }
        }
    }
    
    /**
     * Startet das Heartbeat-Monitoring
     */
    private void startHeartbeatMonitoring() {
        // Heartbeat für Spieler 1
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            if (gameRunning.get() && player1Out != null) {
                try {
                    player1Out.println("HEARTBEAT");
                    player1Heartbeat.incrementAndGet();
                } catch (Exception e) {
                    logError("Heartbeat-Fehler Spieler 1: " + e.getMessage());
                }
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
        
        // Heartbeat für Spieler 2
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            if (gameRunning.get() && player2Out != null) {
                try {
                    player2Out.println("HEARTBEAT");
                    player2Heartbeat.incrementAndGet();
                } catch (Exception e) {
                    logError("Heartbeat-Fehler Spieler 2: " + e.getMessage());
                }
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
        
        // Connection monitoring
        heartbeatExecutor.scheduleAtFixedRate(this::checkConnections, 10000, 10000, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Überprüft die Verbindungen der Spieler
     */
    private void checkConnections() {
        if (!gameRunning.get()) return;
        
        // Prüfe Spieler 1 Verbindung
        if (player1Heartbeat.get() > 3) {
            log("⚠️ Spieler 1 (" + player1Name + ") reagiert nicht - Verbindung instabil");
            player1Heartbeat.set(0);
        }
        
        // Prüfe Spieler 2 Verbindung
        if (player2Heartbeat.get() > 3) {
            log("⚠️ Spieler 2 (" + player2Name + ") reagiert nicht - Verbindung instabil");
            player2Heartbeat.set(0);
        }
    }
    
    /**
     * Hauptspielschleife für das Netzwerkspiel
     */
    private void runGame() {
        try {
            while (gameRunning.get() && !game.isGameEnded()) {
                // Aktuelles Spielbrett an beide Spieler senden
                sendBoardToPlayers();
                
                // Bestimme welcher Spieler am Zug ist
                Socket currentPlayerSocket = (game.getCurrentPlayer() == 'X') ? player1Socket : player2Socket;
                PrintWriter currentPlayerOut = (game.getCurrentPlayer() == 'X') ? player1Out : player2Out;
                BufferedReader currentPlayerIn = (game.getCurrentPlayer() == 'X') ? player1In : player2In;
                PrintWriter otherPlayerOut = (game.getCurrentPlayer() == 'X') ? player2Out : player1Out;
                String currentPlayerName = (game.getCurrentPlayer() == 'X') ? player1Name : player2Name;
                
                // Benachrichtige Spieler über den aktuellen Zug
                currentPlayerOut.println("YOUR_TURN");
                otherPlayerOut.println("OPPONENT_TURN:" + currentPlayerName);
                
                log("🎲 " + currentPlayerName + " ist am Zug");
                
                // Warte auf Zug des aktuellen Spielers mit Timeout
                String move = waitForMove(currentPlayerIn, currentPlayerName);
                if (move == null) {
                    log("❌ " + currentPlayerName + " hat die Verbindung getrennt");
                    break;
                }
                
                if (move.startsWith("MOVE:")) {
                    try {
                        int position = Integer.parseInt(move.substring(5));
                        if (game.makeMove(position)) {
                            // Erfolgreicher Zug - beide Spieler benachrichtigen
                            String moveMsg = "MOVE_MADE:" + position + ":" + game.getCurrentPlayer() + ":" + currentPlayerName;
                            player1Out.println(moveMsg);
                            player2Out.println(moveMsg);
                            log("✅ " + currentPlayerName + " setzt auf Position " + position);
                        } else {
                            // Ungültiger Zug - nur den aktuellen Spieler benachrichtigen
                            currentPlayerOut.println("INVALID_MOVE");
                            log("❌ Ungültiger Zug von " + currentPlayerName + " auf Position " + position);
                        }
                    } catch (NumberFormatException e) {
                        currentPlayerOut.println("INVALID_MOVE");
                        log("❌ Ungültiges Format von " + currentPlayerName);
                    }
                } else if (move.equals("QUIT")) {
                    log("❌ " + currentPlayerName + " hat das Spiel beendet");
                    break;
                } else if (move.equals("HEARTBEAT_RESPONSE")) {
                    // Reset heartbeat counter
                    if (game.getCurrentPlayer() == 'X') {
                        player1Heartbeat.set(0);
                    } else {
                        player2Heartbeat.set(0);
                    }
                }
            }
            
            // Spielende behandeln
            handleGameEnd();
            
        } catch (IOException e) {
            logError("Spiel-Fehler: " + e.getMessage());
        }
    }
    
    /**
     * Wartet auf einen Spielzug mit Timeout
     */
    private String waitForMove(BufferedReader in, String playerName) throws IOException {
        long startTime = System.currentTimeMillis();
        long timeout = 60000; // 60 seconds timeout
        
        while (System.currentTimeMillis() - startTime < timeout) {
            if (in.ready()) {
                return in.readLine();
            }
            try {
                Thread.sleep(100); // Small delay to prevent busy waiting
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        
        log("⏰ Timeout: " + playerName + " hat nicht rechtzeitig geantwortet");
        return null;
    }
    
    /**
     * Sendet das aktuelle Spielbrett an beide Spieler
     */
    private void sendBoardToPlayers() {
        char[][] board = game.getBoard();
        StringBuilder boardString = new StringBuilder("BOARD:");
        
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                boardString.append(board[row][col]);
            }
        }
        
        // Füge Spielinformationen hinzu
        boardString.append(":").append(game.getCurrentPlayer())
                   .append(":").append(player1Name)
                   .append(":").append(player2Name);
        
        broadcastMessage(boardString.toString());
    }
    
    /**
     * Sendet eine Nachricht an beide Spieler
     */
    private void broadcastMessage(String message) {
        if (player1Out != null) {
            player1Out.println(message);
        }
        if (player2Out != null) {
            player2Out.println(message);
        }
    }
    
    /**
     * Behandelt das Spielende
     */
    private void handleGameEnd() {
        sendBoardToPlayers();
        
        String result;
        String winnerName;
        if (game.getWinner() == ' ') {
            result = "DRAW";
            winnerName = "Unentschieden";
        } else if (game.getWinner() == 'X') {
            result = "PLAYER1_WINS";
            winnerName = player1Name;
        } else {
            result = "PLAYER2_WINS";
            winnerName = player2Name;
        }
        
        String endMsg = "GAME_END:" + result + ":" + winnerName;
        broadcastMessage(endMsg);
        
        LocalDateTime endTime = LocalDateTime.now();
        long duration = java.time.Duration.between(gameStartTime, endTime).getSeconds();
        
        log("🏁 Spiel beendet: " + winnerName + " gewinnt! (Dauer: " + duration + " Sekunden)");
    }
    
    /**
     * Ermittelt die Server-IP-Adresse
     */
    private String getServerIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "unbekannt";
        }
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
        serverRunning.set(false);
        
        try {
            if (heartbeatExecutor != null) {
                heartbeatExecutor.shutdown();
                if (!heartbeatExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    heartbeatExecutor.shutdownNow();
                }
            }
            
            if (executorService != null) {
                executorService.shutdown();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            }
            
            if (player1Out != null) player1Out.close();
            if (player2Out != null) player2Out.close();
            if (player1In != null) player1In.close();
            if (player2In != null) player2In.close();
            if (player1Socket != null) player1Socket.close();
            if (player2Socket != null) player2Socket.close();
            if (serverSocket != null) serverSocket.close();
            
            log("🧹 Server-Ressourcen bereinigt");
        } catch (Exception e) {
            logError("Fehler beim Aufräumen: " + e.getMessage());
        }
    }
    
    /**
     * Hauptmethode zum Starten des Servers
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("❌ Ungültiger Port. Verwende Standard-Port " + DEFAULT_PORT);
            }
        }
        
        TicTacToeServer server = new TicTacToeServer();
        server.start(port);
    }
} 
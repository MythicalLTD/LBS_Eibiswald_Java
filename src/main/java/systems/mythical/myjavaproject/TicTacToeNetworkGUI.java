package systems.mythical.myjavaproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

/**
 * GUI-Client für das Netzwerk-TicTacToe-Spiel
 * Moderne Swing-basierte Benutzeroberfläche
 */
public class TicTacToeNetworkGUI extends JFrame {
    private static final int DEFAULT_PORT = 8080;
    private static final int RECONNECT_DELAY = 3000;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final int MOVE_TIMEOUT = 60000;
    
    // Network components
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
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
    
    // GUI components
    private JPanel mainPanel;
    private JPanel connectionPanel;
    private JPanel gamePanel;
    private JPanel boardPanel;
    private JPanel infoPanel;
    private JPanel chatPanel;
    private JButton[][] boardButtons;
    private JLabel statusLabel;
    private JLabel playerLabel;
    private JLabel opponentLabel;
    private JLabel connectionLabel;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendChatButton;
    private JButton disconnectButton;
    private JButton reconnectButton;
    private JProgressBar connectionProgress;
    private JDialog connectionDialog;
    
    public TicTacToeNetworkGUI() {
        this.gameRunning = new AtomicBoolean(false);
        this.connected = new AtomicBoolean(false);
        this.board = new char[3][3];
        this.messageProcessor = Executors.newSingleThreadExecutor();
        this.heartbeatExecutor = Executors.newScheduledThreadPool(1);
        this.reconnectAttempts = 0;
        this.playerName = "Spieler";
        this.opponentName = "Gegner";
        
        initializeGUI();
        showConnectionDialog();
    }
    
    /**
     * Initialisiert die GUI
     */
    private void initializeGUI() {
        setTitle("🌐 TicTacToe Network Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Hauptpanel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 250));
        
        // Verbindungspanel
        createConnectionPanel();
        
        // Spielpanel
        createGamePanel();
        
        // Info-Panel
        createInfoPanel();
        
        // Chat-Panel
        createChatPanel();
        
        // Layout zusammenbauen
        mainPanel.add(connectionPanel, BorderLayout.NORTH);
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(infoPanel, BorderLayout.NORTH);
        rightPanel.add(chatPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        add(mainPanel);
        
        // Window Listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
    }
    
    /**
     * Erstellt das Verbindungspanel
     */
    private void createConnectionPanel() {
        connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.setBackground(new Color(220, 230, 250));
        connectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        connectionLabel = new JLabel("🔌 Nicht verbunden");
        connectionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        connectionLabel.setForeground(Color.RED);
        
        connectionProgress = new JProgressBar();
        connectionProgress.setIndeterminate(true);
        connectionProgress.setVisible(false);
        
        disconnectButton = new JButton("🔌 Trennen");
        disconnectButton.setEnabled(false);
        disconnectButton.addActionListener(e -> disconnect());
        
        reconnectButton = new JButton("🔄 Verbinden");
        reconnectButton.setEnabled(false);
        reconnectButton.addActionListener(e -> reconnect());
        
        connectionPanel.add(connectionLabel);
        connectionPanel.add(connectionProgress);
        connectionPanel.add(disconnectButton);
        connectionPanel.add(reconnectButton);
    }
    
    /**
     * Erstellt das Spielpanel
     */
    private void createGamePanel() {
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(new Color(250, 250, 250));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titel
        JLabel titleLabel = new JLabel("🎮 TicTacToe Network", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 100));
        gamePanel.add(titleLabel, BorderLayout.NORTH);
        
        // Spielbrett
        createBoardPanel();
        gamePanel.add(boardPanel, BorderLayout.CENTER);
        
        // Status
        statusLabel = new JLabel("Warten auf Verbindung...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(new Color(100, 100, 100));
        gamePanel.add(statusLabel, BorderLayout.SOUTH);
    }
    
    /**
     * Erstellt das Spielbrett
     */
    private void createBoardPanel() {
        boardPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        boardPanel.setBackground(new Color(200, 200, 220));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        boardButtons = new JButton[3][3];
        
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final int position = row * 3 + col + 1;
                JButton button = new JButton(String.valueOf(position));
                button.setFont(new Font("Arial", Font.BOLD, 24));
                button.setPreferredSize(new Dimension(80, 80));
                button.setBackground(new Color(255, 255, 255));
                button.setBorder(BorderFactory.createRaisedBevelBorder());
                button.setEnabled(false);
                
                button.addActionListener(e -> makeMove(position));
                
                boardButtons[row][col] = button;
                boardPanel.add(button);
            }
        }
    }
    
    /**
     * Erstellt das Info-Panel
     */
    private void createInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(245, 245, 255));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 150)), 
            "📊 Spielinformationen", 
            TitledBorder.CENTER, 
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        infoPanel.setPreferredSize(new Dimension(250, 200));
        
        playerLabel = new JLabel("👤 Sie: Nicht verbunden");
        playerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        playerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        opponentLabel = new JLabel("🎯 Gegner: Nicht verbunden");
        opponentLabel.setFont(new Font("Arial", Font.BOLD, 12));
        opponentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel connectionInfoLabel = new JLabel("🔗 Verbindung: Nicht aktiv");
        connectionInfoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        connectionInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(playerLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(opponentLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(connectionInfoLabel);
        infoPanel.add(Box.createVerticalStrut(10));
    }
    
    /**
     * Erstellt das Chat-Panel
     */
    private void createChatPanel() {
        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(new Color(245, 245, 255));
        chatPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 150)), 
            "💬 Chat", 
            TitledBorder.CENTER, 
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        chatPanel.setPreferredSize(new Dimension(250, 300));
        
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 11));
        chatArea.setBackground(new Color(255, 255, 255));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        chatInput = new JTextField();
        chatInput.setEnabled(false);
        chatInput.addActionListener(e -> sendChatMessage());
        
        sendChatButton = new JButton("📤");
        sendChatButton.setEnabled(false);
        sendChatButton.addActionListener(e -> sendChatMessage());
        
        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendChatButton, BorderLayout.EAST);
        
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Zeigt den Verbindungsdialog
     */
    private void showConnectionDialog() {
        connectionDialog = new JDialog(this, "🔗 Verbindung herstellen", true);
        connectionDialog.setLayout(new BorderLayout());
        connectionDialog.setSize(400, 300);
        connectionDialog.setLocationRelativeTo(this);
        connectionDialog.setResizable(false);
        
        JPanel dialogPanel = new JPanel(new GridBagLayout());
        dialogPanel.setBackground(new Color(240, 240, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Titel
        JLabel titleLabel = new JLabel("🌐 TicTacToe Network Verbindung");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 50, 100));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        dialogPanel.add(titleLabel, gbc);
        
        // Spielername
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        dialogPanel.add(new JLabel("👤 Spielername:"), gbc);
        
        JTextField nameField = new JTextField("Spieler", 20);
        gbc.gridx = 1; gbc.gridy = 1;
        dialogPanel.add(nameField, gbc);
        
        // Server-Adresse
        gbc.gridx = 0; gbc.gridy = 2;
        dialogPanel.add(new JLabel("📍 Server-IP:"), gbc);
        
        JTextField serverField = new JTextField("localhost", 20);
        gbc.gridx = 1; gbc.gridy = 2;
        dialogPanel.add(serverField, gbc);
        
        // Port
        gbc.gridx = 0; gbc.gridy = 3;
        dialogPanel.add(new JLabel("🔌 Port:"), gbc);
        
        JTextField portField = new JTextField(String.valueOf(DEFAULT_PORT), 20);
        gbc.gridx = 1; gbc.gridy = 3;
        dialogPanel.add(portField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton connectButton = new JButton("🔗 Verbinden");
        JButton cancelButton = new JButton("❌ Abbrechen");
        
        connectButton.addActionListener(e -> {
            playerName = nameField.getText().trim();
            if (playerName.isEmpty()) playerName = "Spieler";
            
            serverAddress = serverField.getText().trim();
            if (serverAddress.isEmpty()) serverAddress = "localhost";
            
            try {
                serverPort = Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException ex) {
                serverPort = DEFAULT_PORT;
            }
            
            connectionDialog.dispose();
            connect(serverAddress, serverPort);
        });
        
        cancelButton.addActionListener(e -> {
            System.exit(0);
        });
        
        buttonPanel.add(connectButton);
        buttonPanel.add(cancelButton);
        
        connectionDialog.add(dialogPanel, BorderLayout.CENTER);
        connectionDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        connectionDialog.setVisible(true);
    }
    
    /**
     * Verbindet sich mit dem Server
     */
    private boolean connect(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.serverPort = port;
        
        connectionProgress.setVisible(true);
        statusLabel.setText("Verbinde zum Server...");
        
        while (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            try {
                socket = new Socket(serverAddress, port);
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
                socket.setSoTimeout(30000);
                
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                connectionTime = LocalDateTime.now();
                connected.set(true);
                reconnectAttempts = 0;
                
                updateConnectionStatus("✅ Verbunden", Color.GREEN);
                statusLabel.setText("Verbunden! Warte auf Spielstart...");
                
                // Sende Spielername
                out.println("NAME:" + playerName);
                
                // Starte Message Processing
                startMessageProcessing();
                
                return true;
                
            } catch (IOException e) {
                reconnectAttempts++;
                updateConnectionStatus("❌ Verbindungsfehler", Color.RED);
                statusLabel.setText("Verbindungsfehler (Versuch " + reconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS + ")");
                
                if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                    try {
                        Thread.sleep(RECONNECT_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
        }
        
        updateConnectionStatus("❌ Verbindung fehlgeschlagen", Color.RED);
        statusLabel.setText("Maximale Anzahl von Verbindungsversuchen erreicht!");
        connectionProgress.setVisible(false);
        return false;
    }
    
    /**
     * Startet die Message Processing
     */
    private void startMessageProcessing() {
        messageProcessor.submit(() -> {
            try {
                while (connected.get()) {
                    String message = in.readLine();
                    if (message == null) {
                        SwingUtilities.invokeLater(() -> {
                            updateConnectionStatus("❌ Verbindung verloren", Color.RED);
                            statusLabel.setText("Verbindung zum Server verloren!");
                        });
                        break;
                    }
                    
                    handleServerMessage(message);
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    updateConnectionStatus("❌ Kommunikationsfehler", Color.RED);
                    statusLabel.setText("Kommunikationsfehler: " + e.getMessage());
                });
            }
        });
    }
    
    /**
     * Verarbeitet Nachrichten vom Server
     */
    private void handleServerMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (message.startsWith("WELCOME:")) {
                    String[] parts = message.split(":");
                    playerNumber = Integer.parseInt(parts[1]);
                    playerSymbol = parts[2].charAt(0);
                    
                    playerLabel.setText("👤 Sie: " + playerName + " (" + playerSymbol + ")");
                    addChatMessage("System", "Willkommen! Sie sind Spieler " + playerNumber);
                } else if ("GAME_START".equals(message)) {
                    gameRunning.set(true);
                    statusLabel.setText("Spiel gestartet!");
                    addChatMessage("System", "Spiel gestartet!");
                    startHeartbeatResponse();
                } else if (message.startsWith("BOARD:")) {
                    updateBoard(message.substring(6));
                    updateBoardDisplay();
                } else if ("YOUR_TURN".equals(message)) {
                    statusLabel.setText("🎲 Sie sind am Zug!");
                    enableBoardButtons(true);
                } else if (message.startsWith("OPPONENT_TURN:")) {
                    String opponent = message.substring(14);
                    statusLabel.setText("⏳ " + opponent + " ist am Zug...");
                    enableBoardButtons(false);
                } else if ("INVALID_MOVE".equals(message)) {
                    statusLabel.setText("❌ Ungültiger Zug! Versuchen Sie es erneut.");
                    enableBoardButtons(true);
                } else if (message.startsWith("MOVE_MADE:")) {
                    String[] parts = message.split(":");
                    int position = Integer.parseInt(parts[1]);
                    char player = parts[2].charAt(0);
                    String playerName = parts.length > 3 ? parts[3] : "Unbekannt";
                    statusLabel.setText("✅ " + playerName + " setzt auf Position " + position);
                    addChatMessage("System", playerName + " setzt auf Position " + position);
                } else if (message.startsWith("GAME_END:")) {
                    handleGameEnd(message.substring(9));
                } else if ("HEARTBEAT".equals(message)) {
                    // Heartbeat wird automatisch beantwortet
                }
            } catch (Exception e) {
                addChatMessage("System", "Fehler: " + e.getMessage());
            }
        });
    }
    
    /**
     * Aktualisiert das Spielbrett
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
            opponentLabel.setText("🎯 Gegner: " + opponentName);
        }
    }
    
    /**
     * Aktualisiert die Brett-Anzeige
     */
    private void updateBoardDisplay() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JButton button = boardButtons[row][col];
                char cell = board[row][col];
                
                if (cell == ' ') {
                    int position = row * 3 + col + 1;
                    button.setText(String.valueOf(position));
                    button.setBackground(new Color(255, 255, 255));
                    button.setForeground(new Color(100, 100, 100));
                } else {
                    button.setText(String.valueOf(cell));
                    if (cell == playerSymbol) {
                        button.setBackground(new Color(144, 238, 144)); // Light green
                        button.setForeground(new Color(0, 100, 0));
                    } else {
                        button.setBackground(new Color(255, 182, 193)); // Light pink
                        button.setForeground(new Color(139, 0, 0));
                    }
                }
            }
        }
    }
    
    /**
     * Aktiviert/Deaktiviert die Brett-Buttons
     */
    private void enableBoardButtons(boolean enabled) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                boardButtons[row][col].setEnabled(enabled && board[row][col] == ' ');
            }
        }
    }
    
    /**
     * Macht einen Spielzug
     */
    private void makeMove(int position) {
        if (out != null) {
            out.println("MOVE:" + position);
            statusLabel.setText("Zug gesendet...");
        }
    }
    
    /**
     * Behandelt das Spielende
     */
    private void handleGameEnd(String result) {
        String[] parts = result.split(":");
        String gameResult = parts[0];
        String winner = parts.length > 1 ? parts[1] : "Unbekannt";
        
        enableBoardButtons(false);
        
        switch (gameResult) {
            case "DRAW" -> {
                statusLabel.setText("🤝 Unentschieden!");
                addChatMessage("System", "Spiel endet unentschieden!");
            }
            case "PLAYER1_WINS" -> {
                if (playerNumber == 1) {
                    statusLabel.setText("🏆 Sie haben gewonnen!");
                    addChatMessage("System", "Glückwunsch! Sie haben gewonnen!");
                } else {
                    statusLabel.setText("😔 Sie haben verloren!");
                    addChatMessage("System", winner + " hat gewonnen!");
                }
            }
            case "PLAYER2_WINS" -> {
                if (playerNumber == 2) {
                    statusLabel.setText("🏆 Sie haben gewonnen!");
                    addChatMessage("System", "Glückwunsch! Sie haben gewonnen!");
                } else {
                    statusLabel.setText("😔 Sie haben verloren!");
                    addChatMessage("System", winner + " hat gewonnen!");
                }
            }
        }
        
        gameRunning.set(false);
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
                    // Ignore heartbeat errors
                }
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Aktualisiert den Verbindungsstatus
     */
    private void updateConnectionStatus(String text, Color color) {
        connectionLabel.setText(text);
        connectionLabel.setForeground(color);
        connectionProgress.setVisible(false);
        
        disconnectButton.setEnabled(connected.get());
        reconnectButton.setEnabled(!connected.get());
        chatInput.setEnabled(connected.get());
        sendChatButton.setEnabled(connected.get());
    }
    
    /**
     * Fügt eine Chat-Nachricht hinzu
     */
    private void addChatMessage(String sender, String message) {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        chatArea.append("[" + timestamp + "] " + sender + ": " + message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    /**
     * Sendet eine Chat-Nachricht
     */
    private void sendChatMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty() && out != null) {
            addChatMessage(playerName, message);
            out.println("CHAT:" + message);
            chatInput.setText("");
        }
    }
    
    /**
     * Trennt die Verbindung
     */
    private void disconnect() {
        if (out != null) {
            out.println("QUIT");
        }
        cleanup();
        updateConnectionStatus("🔌 Getrennt", Color.ORANGE);
        statusLabel.setText("Verbindung getrennt");
    }
    
    /**
     * Verbindet sich erneut
     */
    private void reconnect() {
        cleanup();
        connect(serverAddress, serverPort);
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
            
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    /**
     * Hauptmethode zum Starten der GUI
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default look and feel
        }
        
        SwingUtilities.invokeLater(() -> {
            TicTacToeNetworkGUI gui = new TicTacToeNetworkGUI();
            gui.setVisible(true);
        });
    }
} 
package systems.mythical.myjavaproject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

/**
 * Enhanced Netzwerk-Anwendung für TicTacToe
 * Ermöglicht das Spielen über LAN zwischen zwei Spielern mit erweiterten Features
 */
public class TicTacToeNetworkAnwendung {
    private Scanner scanner;
    private static final int DEFAULT_PORT = 8080;
    private static final int DISCOVERY_PORT = 8081;
    private static final String DISCOVERY_MESSAGE = "TIC_TAC_TOE_DISCOVERY";
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private ExecutorService discoveryExecutor;
    private List<String> discoveredServers;
    
    public TicTacToeNetworkAnwendung() {
        this.scanner = new Scanner(System.in);
        this.discoveryExecutor = Executors.newCachedThreadPool();
        this.discoveredServers = new ArrayList<>();
    }
    
    /**
     * Startet die Netzwerk-TicTacToe-Anwendung
     */
    public void start() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🌐   ENHANCED NETZWERK TIC TAC TOE   🌐");
        System.out.println("=".repeat(60));
        
        showNetworkInfo();
        
        boolean spielen = true;
        while (spielen) {
            spielen = hauptmenu();
        }
        
        beenden();
    }
    
    /**
     * Zeigt Informationen über das Netzwerkspiel
     */
    private void showNetworkInfo() {
        System.out.println("\n📋 ENHANCED NETZWERK-SPIEL FEATURES:");
        System.out.println("• 🚀 Automatische Server-Erkennung");
        System.out.println("• 🔄 Automatische Wiederverbindung");
        System.out.println("• 💓 Heartbeat-Monitoring");
        System.out.println("• ⏰ Timeout-Schutz");
        System.out.println("• 👤 Benutzerdefinierte Spielernamen");
        System.out.println("• 📊 Erweiterte Statistiken");
        System.out.println("• 🎨 Verbesserte Benutzeroberfläche");
        System.out.println("• 🔧 Robuste Fehlerbehandlung");
        System.out.println("• 📡 Real-time Kommunikation");
        System.out.println();
    }
    
    /**
     * Zeigt das Hauptmenü an und verarbeitet die Auswahl
     * @return true um fortzufahren, false um zu beenden
     */
    private boolean hauptmenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("    ENHANCED NETZWERK-MENÜ");
        System.out.println("=".repeat(40));
        System.out.println("1️⃣  Server starten (Host)");
        System.out.println("2️⃣  Client verbinden (Join)");
        System.out.println("3️⃣  Server automatisch suchen");
        System.out.println("4️⃣  Netzwerk-Status prüfen");
        System.out.println("5️⃣  Netzwerk-Informationen");
        System.out.println("6️⃣  Erweiterte Einstellungen");
        System.out.println("7️⃣  GUI-Client starten");
        System.out.println("0️⃣  Beenden");
        System.out.println("=".repeat(40));
        System.out.print("Bitte wählen Sie eine Option (0-7): ");
        
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Buffer leeren
            
            switch (choice) {
                case 1 -> {
                    startServer();
                    return true;
                }
                case 2 -> {
                    startClient();
                    return true;
                }
                case 3 -> {
                    discoverServers();
                    return true;
                }
                case 4 -> {
                    checkNetworkStatus();
                    return true;
                }
                case 5 -> {
                    showNetworkInfo();
                    pausieren();
                    return true;
                }
                case 6 -> {
                    advancedSettings();
                    return true;
                }
                case 7 -> {
                    startGuiClient();
                    return true;
                }
                case 0 -> {
                    return false;
                }
                default -> {
                    System.out.println("❌ Ungültige Auswahl! Bitte 0-7 eingeben.");
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
     * Startet den Server
     */
    private void startServer() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      ENHANCED SERVER STARTEN");
        System.out.println("=".repeat(40));
        
        int port = getPortFromUser();
        String serverName = getServerNameFromUser();
        
        System.out.println("\n🎮 Enhanced Server wird gestartet...");
        System.out.println("📍 IP-Adresse: " + getLocalIPAddress());
        System.out.println("🔌 Port: " + port);
        System.out.println("🏷️  Server-Name: " + serverName);
        System.out.println("⏳ Warte auf Spieler...");
        System.out.println("\n💡 Teilen Sie diese Informationen mit dem anderen Spieler!");
        System.out.println("📱 Der andere Spieler kann 'Server automatisch suchen' verwenden.");
        
        // Starte Discovery-Service
        startDiscoveryService(serverName, port);
        
        try {
            TicTacToeServer server = new TicTacToeServer();
            server.start(port);
        } catch (Exception e) {
            System.err.println("❌ Server-Fehler: " + e.getMessage());
        }
    }
    
    /**
     * Startet den Client
     */
    private void startClient() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      ENHANCED CLIENT VERBINDEN");
        System.out.println("=".repeat(40));
        
        String serverAddress = getServerAddressFromUser();
        int port = getPortFromUser();
        
        System.out.println("\n🔗 Verbinde zum Enhanced Server...");
        System.out.println("📍 Server: " + serverAddress);
        System.out.println("🔌 Port: " + port);
        
        try {
            TicTacToeClient client = new TicTacToeClient();
            if (client.connect(serverAddress, port)) {
                client.startGame();
            } else {
                System.out.println("❌ Verbindung fehlgeschlagen!");
                pausieren();
            }
        } catch (Exception e) {
            System.err.println("❌ Client-Fehler: " + e.getMessage());
            pausieren();
        }
    }
    
    /**
     * Sucht automatisch nach verfügbaren Servern
     */
    private void discoverServers() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      SERVER-AUTOMATISCHE SUCHE");
        System.out.println("=".repeat(40));
        
        System.out.println("🔍 Suche nach TicTacToe-Servern im Netzwerk...");
        discoveredServers.clear();
        
        // Starte Discovery in separatem Thread
        Future<?> discoveryFuture = discoveryExecutor.submit(() -> {
            try {
                DatagramSocket discoverySocket = new DatagramSocket();
                discoverySocket.setSoTimeout(5000);
                
                // Sende Discovery-Nachricht
                byte[] sendData = DISCOVERY_MESSAGE.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
                    InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT);
                discoverySocket.send(sendPacket);
                
                // Warte auf Antworten
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 10000) {
                    try {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        discoverySocket.receive(receivePacket);
                        
                        String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        if (response.startsWith("TIC_TAC_TOE_SERVER:")) {
                            String serverInfo = response.substring(19);
                            discoveredServers.add(serverInfo);
                            System.out.println("✅ Server gefunden: " + serverInfo);
                        }
                    } catch (SocketTimeoutException e) {
                        // Timeout ist normal
                    }
                }
                
                discoverySocket.close();
            } catch (Exception e) {
                System.err.println("❌ Discovery-Fehler: " + e.getMessage());
            }
        });
        
        // Warte auf Discovery-Abschluss
        try {
            discoveryFuture.get(12, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Timeout oder Interruption
        }
        
        if (discoveredServers.isEmpty()) {
            System.out.println("❌ Keine Server gefunden!");
            System.out.println("💡 Stellen Sie sicher, dass ein Server läuft und im gleichen Netzwerk ist.");
        } else {
            System.out.println("\n📋 Gefundene Server:");
            for (int i = 0; i < discoveredServers.size(); i++) {
                System.out.println((i + 1) + ". " + discoveredServers.get(i));
            }
            
            System.out.print("\n🎮 Möchten Sie sich mit einem Server verbinden? (j/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("j") || response.equals("ja") || response.equals("y") || response.equals("yes")) {
                connectToDiscoveredServer();
            }
        }
        
        pausieren();
    }
    
    /**
     * Verbindet sich mit einem gefundenen Server
     */
    private void connectToDiscoveredServer() {
        if (discoveredServers.isEmpty()) return;
        
        System.out.print("Wählen Sie einen Server (1-" + discoveredServers.size() + "): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= discoveredServers.size()) {
                String serverInfo = discoveredServers.get(choice - 1);
                String[] parts = serverInfo.split(":");
                String serverAddress = parts[0];
                int port = Integer.parseInt(parts[1]);
                
                System.out.println("🔗 Verbinde zu " + serverInfo + "...");
                
                TicTacToeClient client = new TicTacToeClient();
                if (client.connect(serverAddress, port)) {
                    client.startGame();
                } else {
                    System.out.println("❌ Verbindung fehlgeschlagen!");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Ungültige Auswahl!");
        }
    }
    
    /**
     * Prüft den Netzwerk-Status
     */
    private void checkNetworkStatus() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      NETZWERK-STATUS PRÜFUNG");
        System.out.println("=".repeat(40));
        
        System.out.println("🔍 Prüfe Netzwerk-Konfiguration...");
        
        // Lokale IP-Adresse
        String localIP = getLocalIPAddress();
        System.out.println("📍 Lokale IP: " + localIP);
        
        // Netzwerk-Interfaces
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            System.out.println("🌐 Verfügbare Netzwerk-Interfaces:");
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    System.out.println("  • " + networkInterface.getDisplayName());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Fehler beim Prüfen der Netzwerk-Interfaces: " + e.getMessage());
        }
        
        // Port-Verfügbarkeit
        System.out.println("🔌 Port-Verfügbarkeit:");
        checkPortAvailability(DEFAULT_PORT, "Standard-Port");
        checkPortAvailability(DISCOVERY_PORT, "Discovery-Port");
        
        System.out.println("✅ Netzwerk-Status-Prüfung abgeschlossen!");
        pausieren();
    }
    
    /**
     * Prüft die Verfügbarkeit eines Ports
     */
    private void checkPortAvailability(int port, String portName) {
        try (ServerSocket testSocket = new ServerSocket(port)) {
            System.out.println("  ✅ " + portName + " (" + port + ") ist verfügbar");
        } catch (IOException e) {
            System.out.println("  ❌ " + portName + " (" + port + ") ist belegt");
        }
    }
    
    /**
     * Erweiterte Einstellungen
     */
    private void advancedSettings() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      ERWEITERTE EINSTELLUNGEN");
        System.out.println("=".repeat(40));
        System.out.println("1️⃣  Timeout-Einstellungen");
        System.out.println("2️⃣  Heartbeat-Intervall");
        System.out.println("3️⃣  Verbindungsversuche");
        System.out.println("4️⃣  Debug-Modus");
        System.out.println("0️⃣  Zurück");
        System.out.println("=".repeat(40));
        System.out.print("Wählen Sie eine Option (0-4): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1 -> {
                    System.out.println("⏰ Aktuelle Timeout-Einstellungen:");
                    System.out.println("  • Verbindungs-Timeout: 30 Sekunden");
                    System.out.println("  • Spielzug-Timeout: 60 Sekunden");
                    System.out.println("  • Heartbeat-Intervall: 5 Sekunden");
                }
                case 2 -> {
                    System.out.println("💓 Heartbeat-Monitoring ist aktiviert");
                    System.out.println("  • Automatische Verbindungsüberwachung");
                    System.out.println("  • Warnungen bei instabilen Verbindungen");
                }
                case 3 -> {
                    System.out.println("🔄 Automatische Wiederverbindung:");
                    System.out.println("  • Maximale Versuche: 5");
                    System.out.println("  • Wartezeit zwischen Versuchen: 3 Sekunden");
                }
                case 4 -> {
                    System.out.println("🐛 Debug-Modus:");
                    System.out.println("  • Detaillierte Logging-Ausgaben");
                    System.out.println("  • Netzwerk-Paket-Überwachung");
                    System.out.println("  • Performance-Metriken");
                }
                case 0 -> {
                    return;
                }
                default -> {
                    System.out.println("❌ Ungültige Auswahl!");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Ungültige Eingabe!");
        }
        
        pausieren();
    }
    
    /**
     * Startet den Discovery-Service
     */
    private void startDiscoveryService(String serverName, int port) {
        discoveryExecutor.submit(() -> {
            try {
                DatagramSocket discoverySocket = new DatagramSocket(DISCOVERY_PORT);
                discoverySocket.setBroadcast(true);
                
                while (true) {
                    try {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        discoverySocket.receive(receivePacket);
                        
                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        if (DISCOVERY_MESSAGE.equals(message)) {
                            String response = "TIC_TAC_TOE_SERVER:" + getLocalIPAddress() + ":" + port + ":" + serverName;
                            byte[] sendData = response.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
                                receivePacket.getAddress(), receivePacket.getPort());
                            discoverySocket.send(sendPacket);
                        }
                    } catch (Exception e) {
                        // Ignore errors in discovery service
                    }
                }
            } catch (Exception e) {
                // Discovery service failed
            }
        });
    }
    
    /**
     * Startet den GUI-Client
     */
    private void startGuiClient() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      GUI-CLIENT STARTEN");
        System.out.println("=".repeat(40));
        
        System.out.println("🎨 Starte GUI-Client...");
        System.out.println("💡 Die GUI wird in einem separaten Fenster geöffnet.");
        System.out.println("📱 Sie können die GUI und die Konsole parallel verwenden.");
        
        try {
            // Starte GUI in separatem Thread
            SwingUtilities.invokeLater(() -> {
                try {
                    TicTacToeNetworkGUI gui = new TicTacToeNetworkGUI();
                    gui.setVisible(true);
                    System.out.println("✅ GUI-Client erfolgreich gestartet!");
                } catch (Exception e) {
                    System.err.println("❌ Fehler beim Starten der GUI: " + e.getMessage());
                }
            });
            
            // Kurze Pause für bessere Benutzererfahrung
            Thread.sleep(1000);
            
        } catch (Exception e) {
            System.err.println("❌ GUI-Client Fehler: " + e.getMessage());
        }
    }
    
    /**
     * Lässt den Benutzer eine Port-Nummer eingeben
     */
    private int getPortFromUser() {
        while (true) {
            System.out.print("🔌 Port-Nummer eingeben (Standard: " + DEFAULT_PORT + "): ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                return DEFAULT_PORT;
            }
            
            try {
                int port = Integer.parseInt(input);
                if (port > 0 && port <= 65535) {
                    return port;
                } else {
                    System.out.println("❌ Port muss zwischen 1 und 65535 liegen!");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Bitte geben Sie eine gültige Port-Nummer ein!");
            }
        }
    }
    
    /**
     * Lässt den Benutzer einen Server-Namen eingeben
     */
    private String getServerNameFromUser() {
        System.out.print("🏷️  Server-Name eingeben (Enter für Standard): ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? "TicTacToe Server" : input;
    }
    
    /**
     * Lässt den Benutzer eine Server-Adresse eingeben
     */
    private String getServerAddressFromUser() {
        System.out.print("📍 Server-IP-Adresse eingeben (Standard: localhost): ");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            return "localhost";
        }
        
        return input;
    }
    
    /**
     * Ermittelt die lokale IP-Adresse
     */
    private String getLocalIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unbekannt";
        }
    }
    
    /**
     * Pausiert die Ausführung
     */
    private void pausieren() {
        System.out.println("\nDrücken Sie Enter um fortzufahren...");
        scanner.nextLine();
    }
    
    /**
     * Beendet die Anwendung
     */
    private void beenden() {
        if (discoveryExecutor != null) {
            discoveryExecutor.shutdown();
        }
        
        System.out.println("\n👋 Enhanced Netzwerk-TicTacToe wird beendet.");
        System.out.println("Danke fürs Spielen! 🎮");
    }
    
    /**
     * Hauptmethode zum Testen
     */
    public static void main(String[] args) {
        TicTacToeNetworkAnwendung app = new TicTacToeNetworkAnwendung();
        app.start();
    }
} 
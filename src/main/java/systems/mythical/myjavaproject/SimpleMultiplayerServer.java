package systems.mythical.myjavaproject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleMultiplayerServer {
    private static final Map<String, GameArena> arenas = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> arenaPlayers = new ConcurrentHashMap<>();
    private static final Set<String> arenasBeingDeleted = ConcurrentHashMap.newKeySet();
    private static HttpServer server;
    
    public static void start() throws Exception {
        int port = 8080;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/", new LobbyHandler());
        server.createContext("/game", new GameHandler());
        server.createContext("/api/join", new JoinHandler());
        server.createContext("/api/move", new MoveHandler());
        server.createContext("/api/status", new StatusHandler());
        server.createContext("/api/create-arena", new CreateArenaHandler());
        server.createContext("/api/delete-arena", new DeleteArenaHandler());
        server.createContext("/api/reset-arena", new ResetArenaHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        
        String localIP = InetAddress.getLocalHost().getHostAddress();
        System.out.println("🌐 Simple Multiplayer TicTacToe Server gestartet!");
        System.out.println("📍 Lokale URL: http://localhost:" + port);
        System.out.println("🌍 Netzwerk URL: http://" + localIP + ":" + port);
        System.out.println("💡 Teile die Netzwerk URL mit Freunden zum Spielen!");
        System.out.println("⏹️  Drücke Ctrl+C zum Beenden");
        
        // Create default arenas (protected ones that get reset)
        createArena("Arena-1", "Kampfarena 1");
        createArena("Arena-2", "Kampfarena 2"); 
        createArena("Arena-3", "Kampfarena 3");
        
        // Create additional arenas (these will be deleted after games)
        for (int i = 4; i <= 10; i++) {
            createArena("Arena-" + i, "Kampfarena " + i);
        }
    }
    
    public static void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
    
    private static void createArena(String id, String name) {
        arenas.put(id, new GameArena(id, name));
        arenaPlayers.put(id, ConcurrentHashMap.newKeySet());
    }
    
    static class LobbyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                System.out.println("📥 Lobby request: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
                String response = getLobbyHTML();
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                exchange.getResponseHeaders().add("Cache-Control", "no-cache");
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
                System.out.println("✅ Lobby response sent successfully");
            } catch (Exception e) {
                System.err.println("❌ Error in LobbyHandler: " + e.getMessage());
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
            }
        }
        
        private String getLobbyHTML() {
            StringBuilder arenaList = new StringBuilder();
            for (GameArena arena : arenas.values()) {
                int playerCount = arena.getPlayerCount();
                boolean isBeingDeleted = arenasBeingDeleted.contains(arena.id);
                boolean isGameFinished = arena.getWinner() != ' ' || arena.isDraw();
                
                String statusColor, statusText, buttonClass;
                
                if (isBeingDeleted) {
                    statusColor = "text-orange-600";
                    statusText = "🧹 Wird bereinigt...";
                    buttonClass = "bg-gray-400 text-gray-700 font-bold py-3 px-6 rounded-xl cursor-not-allowed";
                } else if (isGameFinished) {
                    statusColor = "text-purple-600";
                    statusText = "🎯 Spiel beendet";
                    buttonClass = "bg-gray-400 text-gray-700 font-bold py-3 px-6 rounded-xl cursor-not-allowed";
                } else if (playerCount == 0) {
                    statusColor = "text-green-600";
                    statusText = "🟢 Leer";
                    buttonClass = "bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white font-bold py-3 px-6 rounded-xl shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200";
                } else if (playerCount == 1) {
                    statusColor = "text-yellow-600";
                    statusText = "🟡 1 Spieler wartet";
                    buttonClass = "bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white font-bold py-3 px-6 rounded-xl shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200";
                } else {
                    statusColor = "text-red-600";
                    statusText = "🔴 Voll";
                    buttonClass = "bg-gray-400 text-gray-700 font-bold py-3 px-6 rounded-xl cursor-not-allowed";
                }
                
                arenaList.append(String.format("""
                    <div class="bg-white rounded-2xl shadow-xl p-8 hover:shadow-2xl transition-all duration-300 transform hover:scale-105 border border-gray-100">
                        <div class="text-center">
                            <div class="text-4xl mb-4">🎮</div>
                            <h3 class="text-2xl font-bold text-gray-800 mb-3">%s</h3>
                            <p class="text-lg %s mb-6 font-semibold">%s</p>
                            <div class="bg-gray-50 rounded-lg p-4 mb-6">
                                <div class="flex justify-between items-center">
                                    <span class="text-gray-600">Spieler:</span>
                                    <span class="font-bold text-lg">%d/2</span>
                                </div>
                                <div class="w-full bg-gray-200 rounded-full h-2 mt-2">
                                    <div class="bg-gradient-to-r from-blue-500 to-purple-600 h-2 rounded-full transition-all duration-300" style="width: %d%%"></div>
                                </div>
                            </div>
                            <button onclick="joinArena('%s')" %s
                                    class="%s">
                                %s
                            </button>
                            <div class="mt-4 flex space-x-2">
                                <button onclick="resetArena('%s', '%s')" 
                                        class="flex-1 bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-2 px-3 rounded-lg text-sm transition-colors">
                                    🔄 Reset
                                </button>
                                <button onclick="deleteArena('%s', '%s')" 
                                        class="flex-1 bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-3 rounded-lg text-sm transition-colors">
                                    🗑️ Löschen
                                </button>
                            </div>
                        </div>
                    </div>
                                         """, arena.name, statusColor, statusText, playerCount, (playerCount * 50), arena.id, 
                     (playerCount >= 2 || isBeingDeleted || isGameFinished) ? "disabled" : "", buttonClass, 
                     isBeingDeleted ? "🧹 Bereinigt..." : isGameFinished ? "🎯 Beendet" : playerCount >= 2 ? "❌ Voll" : "🚀 Jetzt beitreten",
                     arena.id, arena.name, arena.id, arena.name));
            }
            
            return """
                <!DOCTYPE html>
                <html lang="de">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>🎮 TicTacToe Multiplayer Lobby</title>
                    <script src="https://cdn.tailwindcss.com"></script>
                    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
                </head>
                <body class="bg-gradient-to-br from-purple-600 to-blue-600 min-h-screen">
                    <div class="container mx-auto px-4 py-8">
                        <div class="text-center mb-8">
                            <h1 class="text-6xl font-bold text-white mb-4 animate-pulse">🎮 TicTacToe Multiplayer</h1>
                            <p class="text-white text-xl mb-4">Wähle eine Arena und spiele mit Freunden!</p>
                            <div class="bg-white/20 rounded-xl p-4 backdrop-blur-sm border border-white/30">
                                <p class="text-white font-semibold">👋 Willkommen <span id="playerNameDisplay" class="text-yellow-300">Spieler</span>!</p>
                                <button onclick="changePlayerName()" class="mt-2 bg-white/20 hover:bg-white/30 text-white px-4 py-2 rounded-lg transition-all">
                                    <i class="fas fa-edit"></i> Namen ändern
                                </button>
                            </div>
                        </div>
                        
                        <div class="max-w-4xl mx-auto">
                            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                %s
                            </div>
                        </div>
                        
                        <div class="text-center mt-12 space-y-6">
                            <div class="flex justify-center flex-wrap gap-4">
                                <button onclick="location.reload()" 
                                        class="bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white font-bold py-3 px-6 rounded-xl shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200">
                                    <i class="fas fa-sync-alt"></i> Aktualisieren
                                </button>
                                <button onclick="showCreateArenaModal()" 
                                        class="bg-gradient-to-r from-purple-500 to-purple-600 hover:from-purple-600 hover:to-purple-700 text-white font-bold py-3 px-6 rounded-xl shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200">
                                    <i class="fas fa-plus"></i> Arena erstellen
                                </button>
                                <button onclick="showPlayerStats()" 
                                        class="bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white font-bold py-3 px-6 rounded-xl shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200">
                                    <i class="fas fa-chart-bar"></i> Statistiken
                                </button>
                                <button onclick="showSettings()" 
                                        class="bg-gradient-to-r from-gray-500 to-gray-600 hover:from-gray-600 hover:to-gray-700 text-white font-bold py-3 px-6 rounded-xl shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200">
                                    <i class="fas fa-cog"></i> Einstellungen
                                </button>
                            </div>
                            <div class="bg-white/10 rounded-xl p-4 backdrop-blur-sm border border-white/20">
                                <p class="text-white text-lg font-semibold mb-2"><i class="fas fa-info-circle"></i> Spielregeln</p>
                                <p class="text-white/80 text-sm">• Drei in einer Reihe gewinnt • Standard-Arenas (1-3) werden automatisch zurückgesetzt • Custom Arenas werden nach Spielende gelöscht</p>
                            </div>
                        </div>
                    </div>
                    
                    <script>
                        console.log('🚀 TicTacToe Lobby JavaScript loaded');
                        console.log('🌐 Current URL:', window.location.href);
                        console.log('📊 Local Storage Player Name:', localStorage.getItem('ticTacToePlayerName'));
                        
                        let currentPlayerName = localStorage.getItem('ticTacToePlayerName') || '';
                        console.log('👤 Current player name:', currentPlayerName);
                        
                        // Initialize player name on page load
                        document.addEventListener('DOMContentLoaded', function() {
                            console.log('📄 DOM Content Loaded');
                            if (!currentPlayerName) {
                                console.log('🔐 No stored player name, prompting...');
                                promptForPlayerName();
                            } else {
                                console.log('✅ Player name found, updating display');
                                updatePlayerNameDisplay();
                            }
                        });
                        
                        function updatePlayerNameDisplay() {
                            document.getElementById('playerNameDisplay').textContent = currentPlayerName || 'Spieler';
                        }
                        
                        async function promptForPlayerName() {
                            try {
                                console.log('💬 Prompting for player name, current:', currentPlayerName);
                                const { value: name } = await Swal.fire({
                                    title: '🎮 Willkommen!',
                                    text: 'Wie möchtest du genannt werden?',
                                    input: 'text',
                                    inputPlaceholder: 'Dein Spielername...',
                                    inputValue: currentPlayerName,
                                    showCancelButton: false,
                                    allowOutsideClick: false,
                                    inputValidator: (value) => {
                                        if (!value || value.trim().length < 2) {
                                            return 'Bitte gib einen Namen mit mindestens 2 Zeichen ein!';
                                        }
                                        if (value.trim().length > 20) {
                                            return 'Name darf maximal 20 Zeichen haben!';
                                        }
                                        return null;
                                    },
                                    background: '#1a1a2e',
                                    color: '#fff',
                                    confirmButtonColor: '#7c3aed'
                                });
                                
                                if (name) {
                                    currentPlayerName = name.trim();
                                    localStorage.setItem('ticTacToePlayerName', currentPlayerName);
                                    updatePlayerNameDisplay();
                                    console.log('✅ Player name set to:', currentPlayerName);
                                    
                                    // Update stats
                                    let stats = JSON.parse(localStorage.getItem('ticTacToeStats') || '{}');
                                    if (!stats[currentPlayerName]) {
                                        stats[currentPlayerName] = { wins: 0, losses: 0, draws: 0, gamesPlayed: 0 };
                                        localStorage.setItem('ticTacToeStats', JSON.stringify(stats));
                                        console.log('📊 Created new stats for player:', currentPlayerName);
                                    }
                                } else {
                                    console.warn('⚠️ No name provided in prompt');
                                }
                            } catch (error) {
                                console.error('❌ Error in promptForPlayerName:', error);
                            }
                        }
                        
                        async function changePlayerName() {
                            await promptForPlayerName();
                        }
                        
                        async function joinArena(arenaId) {
                            try {
                                console.log('🎮 Joining arena:', arenaId);
                                if (!currentPlayerName) {
                                    console.log('🔐 No player name, prompting...');
                                    await promptForPlayerName();
                                }
                                
                                if (currentPlayerName) {
                                    console.log('✅ Player name confirmed:', currentPlayerName);
                                    const result = await Swal.fire({
                                        title: '🚀 Arena beitreten',
                                        text: `Möchtest du der Arena als "${currentPlayerName}" beitreten?`,
                                        icon: 'question',
                                        showCancelButton: true,
                                        confirmButtonText: '✅ Ja, beitreten!',
                                        cancelButtonText: '❌ Abbrechen',
                                        background: '#1a1a2e',
                                        color: '#fff',
                                        confirmButtonColor: '#7c3aed',
                                        cancelButtonColor: '#6b7280'
                                    });
                                    
                                    if (result.isConfirmed) {
                                        const gameUrl = '/game?arena=' + arenaId + '&player=' + encodeURIComponent(currentPlayerName);
                                        console.log('🚀 Redirecting to game:', gameUrl);
                                        window.location.href = gameUrl;
                                    } else {
                                        console.log('❌ User cancelled joining arena');
                                    }
                                } else {
                                    console.warn('⚠️ No player name provided after prompt');
                                }
                            } catch (error) {
                                console.error('❌ Error in joinArena:', error);
                                await Swal.fire({
                                    title: '❌ Fehler',
                                    text: 'Fehler beim Beitreten der Arena: ' + error.message,
                                    icon: 'error',
                                    background: '#1a1a2e',
                                    color: '#fff',
                                    confirmButtonColor: '#ef4444'
                                });
                            }
                        }
                        
                        async function showCreateArenaModal() {
                            const { value: formValues } = await Swal.fire({
                                title: '🏟️ Neue Arena erstellen',
                                html: `
                                    <div class="space-y-4">
                                        <div>
                                            <label class="block text-left text-white mb-2 font-semibold">Arena Name:</label>
                                            <input id="swal-arena-name" class="swal2-input" placeholder="Meine Arena" maxlength="20">
                                        </div>
                                        <div>
                                            <label class="block text-left text-white mb-2 font-semibold">Beschreibung:</label>
                                            <input id="swal-arena-desc" class="swal2-input" placeholder="Epische Kämpfe hier!" maxlength="50">
                                        </div>
                                    </div>
                                `,
                                focusConfirm: false,
                                showCancelButton: true,
                                confirmButtonText: '🚀 Arena erstellen',
                                cancelButtonText: '❌ Abbrechen',
                                background: '#1a1a2e',
                                color: '#fff',
                                confirmButtonColor: '#7c3aed',
                                cancelButtonColor: '#6b7280',
                                preConfirm: () => {
                                    const name = document.getElementById('swal-arena-name').value.trim();
                                    const desc = document.getElementById('swal-arena-desc').value.trim();
                                    
                                    if (!name) {
                                        Swal.showValidationMessage('Bitte Arena Name eingeben!');
                                        return false;
                                    }
                                    if (name.length < 2) {
                                        Swal.showValidationMessage('Name muss mindestens 2 Zeichen haben!');
                                        return false;
                                    }
                                    
                                    return { name, desc: desc || name };
                                }
                            });
                            
                            if (formValues) {
                                await createArena(formValues.name, formValues.desc);
                            }
                        }
                        
                        async function createArena(name, description) {
                            try {
                                const response = await fetch('/api/create-arena', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/json'},
                                    body: JSON.stringify({name: name, description: description})
                                });
                                
                                const data = await response.json();
                                
                                if (data.success) {
                                    await Swal.fire({
                                        title: '✅ Arena erstellt!',
                                        text: `Arena "${name}" wurde erfolgreich erstellt!`,
                                        icon: 'success',
                                        background: '#1a1a2e',
                                        color: '#fff',
                                        confirmButtonColor: '#10b981'
                                    });
                                    location.reload();
                                } else {
                                    await Swal.fire({
                                        title: '❌ Fehler',
                                        text: data.message,
                                        icon: 'error',
                                        background: '#1a1a2e',
                                        color: '#fff',
                                        confirmButtonColor: '#ef4444'
                                    });
                                }
                            } catch (err) {
                                await Swal.fire({
                                    title: '🌐 Netzwerkfehler',
                                    text: 'Verbindung zum Server fehlgeschlagen!',
                                    icon: 'error',
                                    background: '#1a1a2e',
                                    color: '#fff',
                                    confirmButtonColor: '#ef4444'
                                });
                            }
                        }
                        
                        async function deleteArena(arenaId, arenaName) {
                            const result = await Swal.fire({
                                title: '🗑️ Arena löschen',
                                text: `Arena "${arenaName}" wirklich löschen?`,
                                icon: 'warning',
                                showCancelButton: true,
                                confirmButtonText: '🗑️ Ja, löschen!',
                                cancelButtonText: '❌ Abbrechen',
                                background: '#1a1a2e',
                                color: '#fff',
                                confirmButtonColor: '#ef4444',
                                cancelButtonColor: '#6b7280'
                            });
                            
                            if (result.isConfirmed) {
                                try {
                                    const response = await fetch('/api/delete-arena', {
                                        method: 'POST',
                                        headers: {'Content-Type': 'application/json'},
                                        body: JSON.stringify({arenaId: arenaId})
                                    });
                                    
                                    const data = await response.json();
                                    
                                    if (data.success) {
                                        await Swal.fire({
                                            title: '✅ Arena gelöscht!',
                                            text: `Arena "${arenaName}" wurde erfolgreich gelöscht!`,
                                            icon: 'success',
                                            background: '#1a1a2e',
                                            color: '#fff',
                                            confirmButtonColor: '#10b981'
                                        });
                                        location.reload();
                                    } else {
                                        await Swal.fire({
                                            title: '❌ Fehler',
                                            text: data.message,
                                            icon: 'error',
                                            background: '#1a1a2e',
                                            color: '#fff',
                                            confirmButtonColor: '#ef4444'
                                        });
                                    }
                                } catch (err) {
                                    await Swal.fire({
                                        title: '🌐 Netzwerkfehler',
                                        text: 'Verbindung zum Server fehlgeschlagen!',
                                        icon: 'error',
                                        background: '#1a1a2e',
                                        color: '#fff',
                                        confirmButtonColor: '#ef4444'
                                    });
                                }
                            }
                        }
                        
                        async function resetArena(arenaId, arenaName) {
                            const result = await Swal.fire({
                                title: '🔄 Arena zurücksetzen',
                                text: `Arena "${arenaName}" wirklich zurücksetzen?`,
                                icon: 'question',
                                showCancelButton: true,
                                confirmButtonText: '🔄 Ja, zurücksetzen!',
                                cancelButtonText: '❌ Abbrechen',
                                background: '#1a1a2e',
                                color: '#fff',
                                confirmButtonColor: '#f59e0b',
                                cancelButtonColor: '#6b7280'
                            });
                            
                            if (result.isConfirmed) {
                                try {
                                    const response = await fetch('/api/reset-arena', {
                                        method: 'POST',
                                        headers: {'Content-Type': 'application/json'},
                                        body: JSON.stringify({arenaId: arenaId})
                                    });
                                    
                                    const data = await response.json();
                                    
                                    if (data.success) {
                                        await Swal.fire({
                                            title: '✅ Arena zurückgesetzt!',
                                            text: `Arena "${arenaName}" wurde erfolgreich zurückgesetzt!`,
                                            icon: 'success',
                                            background: '#1a1a2e',
                                            color: '#fff',
                                            confirmButtonColor: '#10b981'
                                        });
                                        location.reload();
                                    } else {
                                        await Swal.fire({
                                            title: '❌ Fehler',
                                            text: data.message,
                                            icon: 'error',
                                            background: '#1a1a2e',
                                            color: '#fff',
                                            confirmButtonColor: '#ef4444'
                                        });
                                    }
                                } catch (err) {
                                    await Swal.fire({
                                        title: '🌐 Netzwerkfehler',
                                        text: 'Verbindung zum Server fehlgeschlagen!',
                                        icon: 'error',
                                        background: '#1a1a2e',
                                        color: '#fff',
                                        confirmButtonColor: '#ef4444'
                                    });
                                }
                            }
                        }
                        
                        async function showPlayerStats() {
                            const stats = JSON.parse(localStorage.getItem('ticTacToeStats') || '{}');
                            const playerStats = stats[currentPlayerName] || { wins: 0, losses: 0, draws: 0, gamesPlayed: 0 };
                            
                            const winRate = playerStats.gamesPlayed > 0 ? 
                                Math.round((playerStats.wins / playerStats.gamesPlayed) * 100) : 0;
                            
                            await Swal.fire({
                                title: `📊 Statistiken von ${currentPlayerName}`,
                                html: `
                                    <div class="text-left space-y-3">
                                        <div class="bg-green-100 p-3 rounded-lg">
                                            <p class="text-green-800 font-semibold">🏆 Siege: ${playerStats.wins}</p>
                                        </div>
                                        <div class="bg-red-100 p-3 rounded-lg">
                                            <p class="text-red-800 font-semibold">❌ Niederlagen: ${playerStats.losses}</p>
                                        </div>
                                        <div class="bg-yellow-100 p-3 rounded-lg">
                                            <p class="text-yellow-800 font-semibold">🤝 Unentschieden: ${playerStats.draws}</p>
                                        </div>
                                        <div class="bg-blue-100 p-3 rounded-lg">
                                            <p class="text-blue-800 font-semibold">🎮 Gesamt Spiele: ${playerStats.gamesPlayed}</p>
                                        </div>
                                        <div class="bg-purple-100 p-3 rounded-lg">
                                            <p class="text-purple-800 font-semibold">📈 Siegesrate: ${winRate}%</p>
                                        </div>
                                    </div>
                                `,
                                background: '#1a1a2e',
                                color: '#fff',
                                confirmButtonColor: '#7c3aed'
                            });
                        }
                        
                        async function showSettings() {
                            const { value: result } = await Swal.fire({
                                title: '⚙️ Einstellungen',
                                html: `
                                    <div class="text-left space-y-4">
                                        <div>
                                            <label class="block text-white mb-2 font-semibold">🎨 Theme:</label>
                                            <select id="swal-theme" class="swal2-select">
                                                <option value="default">Standard (Lila/Blau)</option>
                                                <option value="dark">Dunkel</option>
                                                <option value="neon">Neon</option>
                                            </select>
                                        </div>
                                        <div>
                                            <label class="block text-white mb-2 font-semibold">🔊 Sound:</label>
                                            <select id="swal-sound" class="swal2-select">
                                                <option value="on">An</option>
                                                <option value="off">Aus</option>
                                            </select>
                                        </div>
                                        <div>
                                            <label class="block text-white mb-2 font-semibold">📱 Animations:</label>
                                            <select id="swal-animations" class="swal2-select">
                                                <option value="on">An</option>
                                                <option value="off">Aus</option>
                                            </select>
                                        </div>
                                    </div>
                                `,
                                showCancelButton: true,
                                confirmButtonText: '💾 Speichern',
                                cancelButtonText: '❌ Abbrechen',
                                background: '#1a1a2e',
                                color: '#fff',
                                confirmButtonColor: '#7c3aed',
                                cancelButtonColor: '#6b7280',
                                focusConfirm: false,
                                preConfirm: () => {
                                    return {
                                        theme: document.getElementById('swal-theme').value,
                                        sound: document.getElementById('swal-sound').value,
                                        animations: document.getElementById('swal-animations').value
                                    };
                                }
                            });
                            
                            if (result) {
                                localStorage.setItem('ticTacToeSettings', JSON.stringify(result));
                                await Swal.fire({
                                    title: '✅ Einstellungen gespeichert!',
                                    text: 'Deine Einstellungen wurden erfolgreich gespeichert!',
                                    icon: 'success',
                                    background: '#1a1a2e',
                                    color: '#fff',
                                    confirmButtonColor: '#10b981'
                                });
                            }
                        }
                    </script>
                </body>
                </html>
                """.replace("%s", arenaList.toString());
        }
    }
    
    static class GameHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                System.out.println("📥 Game request: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQuery(query);
                
                String arenaId = params.get("arena");
                String playerName = params.get("player");
                
                if (arenaId == null || playerName == null) {
                    System.err.println("❌ Missing parameters - arena: " + arenaId + ", player: " + playerName);
                    exchange.getResponseHeaders().add("Location", "/");
                    exchange.sendResponseHeaders(302, -1);
                    return;
                }
                
                GameArena arena = arenas.get(arenaId);
                if (arena == null) {
                    System.err.println("❌ Arena not found: " + arenaId);
                    exchange.getResponseHeaders().add("Location", "/");
                    exchange.sendResponseHeaders(302, -1);
                    return;
                }
                
                String response = getGameHTML(arena, playerName);
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                exchange.getResponseHeaders().add("Cache-Control", "no-cache");
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
                System.out.println("✅ Game response sent for arena: " + arenaId + ", player: " + playerName);
            } catch (Exception e) {
                System.err.println("❌ Error in GameHandler: " + e.getMessage());
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
            }
        }
        
        private Map<String, String> parseQuery(String query) {
            Map<String, String> params = new HashMap<>();
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2) {
                        try {
                            params.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                        } catch (Exception e) {
                            params.put(pair[0], pair[1]);
                        }
                    }
                }
            }
            return params;
        }
        
        private String getGameHTML(GameArena arena, String playerName) {
            String html = """
                <!DOCTYPE html>
                <html lang="de">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>🎮 TicTacToe - %s</title>
                    <script src="https://cdn.tailwindcss.com"></script>
                    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
                    <style>
                        @keyframes pulse-glow {
                            0%, 100% { box-shadow: 0 0 20px rgba(59, 130, 246, 0.5); }
                            50% { box-shadow: 0 0 40px rgba(59, 130, 246, 0.8), 0 0 60px rgba(59, 130, 246, 0.6); }
                        }
                        @keyframes float {
                            0%, 100% { transform: translateY(0px); }
                            50% { transform: translateY(-10px); }
                        }
                        @keyframes shake {
                            0%, 100% { transform: translateX(0); }
                            25% { transform: translateX(-5px); }
                            75% { transform: translateX(5px); }
                        }
                        @keyframes winning-line {
                            from { width: 0%; }
                            to { width: 100%; }
                        }
                        .pulse-glow { animation: pulse-glow 2s infinite; }
                        .float-animation { animation: float 3s ease-in-out infinite; }
                        .shake-animation { animation: shake 0.5s ease-in-out; }
                        .winning-cell { animation: pulse-glow 1s ease-in-out 3; }
                        .cell-hover { transition: all 0.2s ease; }
                        .cell-hover:hover { transform: scale(1.1) rotate(2deg); }
                        .gradient-border {
                            background: linear-gradient(45deg, #3B82F6, #8B5CF6, #EC4899, #F59E0B);
                            padding: 3px;
                            border-radius: 1rem;
                        }
                        .gradient-border-inner {
                            background: white;
                            border-radius: calc(1rem - 3px);
                        }
                        .stats-card {
                            backdrop-filter: blur(10px);
                            background: rgba(255, 255, 255, 0.95);
                        }
                        .game-sound { display: none; }
                    </style>
                </head>
                <body class="bg-gradient-to-br from-indigo-900 via-purple-900 to-pink-900 min-h-screen">
                    <!-- Background Pattern -->
                    <div class="fixed inset-0 bg-black bg-opacity-20"></div>
                    <div class="fixed inset-0" style="background-image: radial-gradient(circle at 1px 1px, rgba(255,255,255,0.15) 1px, transparent 0); background-size: 20px 20px;"></div>
                    
                    <!-- Sound Effects -->
                    <audio id="moveSound" class="game-sound" preload="auto">
                        <source src="data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmAYCjOJyu7DZiEA" type="audio/wav">
                    </audio>
                    <audio id="winSound" class="game-sound" preload="auto">
                        <source src="data:audio/wav;base64,UklGRroCAABXQVZFZm10IBAAAAABAAEARKwAAIhYAQACABAAZGF0YWYCAAClzZlzllVmtlFZtI5GYpFG..." type="audio/wav">
                    </audio>
                    <audio id="loseSound" class="game-sound" preload="auto">
                        <source src="data:audio/wav;base64,UklGRroCAABXQVZFZm10IBAAAAABAAEARKwAAIhYAQACABAAZGF0YWYCAACoqZGXWlhcqpAYGUZckpNH..." type="audio/wav">
                    </audio>
                    
                    <div class="container mx-auto px-4 py-4 relative z-10">
                        <div class="max-w-6xl mx-auto">
                            <!-- Header with enhanced design -->
                            <div class="text-center mb-6">
                                <h1 class="text-6xl font-black mb-4 float-animation">
                                    <span class="bg-gradient-to-r from-yellow-400 via-red-500 to-pink-500 bg-clip-text text-transparent drop-shadow-lg">
                                        🎮 TIC TAC TOE
                                    </span>
                                </h1>
                                <div class="gradient-border inline-block">
                                    <div class="gradient-border-inner px-6 py-3">
                                        <p class="text-gray-800 font-bold text-lg">
                                            <i class="fas fa-gamepad text-purple-600"></i> Arena: <span class="text-purple-600">%s</span> 
                                            <span class="mx-3">|</span>
                                            <i class="fas fa-user text-blue-600"></i> Spieler: <span class="text-blue-600">%s</span>
                                        </p>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="grid lg:grid-cols-3 gap-6">
                                <!-- Game Stats Panel -->
                                <div class="lg:order-1 stats-card rounded-2xl p-6 shadow-2xl">
                                    <h3 class="text-xl font-bold mb-4 text-center">
                                        <i class="fas fa-chart-bar text-green-600"></i> Live Statistiken
                                    </h3>
                                    <div id="gameStats" class="space-y-3">
                                        <div class="flex justify-between items-center p-3 bg-gradient-to-r from-blue-50 to-blue-100 rounded-xl">
                                            <span><i class="fas fa-trophy text-yellow-500"></i> Siege:</span>
                                            <span id="winsCount" class="font-bold text-green-600">0</span>
                                        </div>
                                        <div class="flex justify-between items-center p-3 bg-gradient-to-r from-red-50 to-red-100 rounded-xl">
                                            <span><i class="fas fa-times text-red-500"></i> Niederlagen:</span>
                                            <span id="lossesCount" class="font-bold text-red-600">0</span>
                                        </div>
                                        <div class="flex justify-between items-center p-3 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl">
                                            <span><i class="fas fa-handshake text-gray-500"></i> Unentschieden:</span>
                                            <span id="drawsCount" class="font-bold text-gray-600">0</span>
                                        </div>
                                        <div class="flex justify-between items-center p-3 bg-gradient-to-r from-purple-50 to-purple-100 rounded-xl">
                                            <span><i class="fas fa-percentage text-purple-500"></i> Gewinnrate:</span>
                                            <span id="winRate" class="font-bold text-purple-600">0%</span>
                                        </div>
                                    </div>
                                    
                                    <!-- Quick Actions -->
                                    <div class="mt-6 space-y-3">
                                        <button onclick="toggleSound()" id="soundToggle"
                                                class="w-full bg-gradient-to-r from-indigo-500 to-purple-600 hover:from-indigo-600 hover:to-purple-700 text-white font-bold py-2 px-4 rounded-xl shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200">
                                            <i class="fas fa-volume-up"></i> Sound: Ein
                                        </button>
                                        <button onclick="showGameHelp()"
                                                class="w-full bg-gradient-to-r from-green-500 to-emerald-600 hover:from-green-600 hover:to-emerald-700 text-white font-bold py-2 px-4 rounded-xl shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200">
                                            <i class="fas fa-question-circle"></i> Hilfe
                                        </button>
                                    </div>
                                </div>
                                
                                <!-- Game Board - Center -->
                                <div class="lg:order-2 text-center">
                                    <div id="status" class="mb-8 p-6 rounded-2xl bg-gradient-to-r from-yellow-100 to-yellow-200 text-yellow-800 font-bold text-lg shadow-lg">
                                        🔄 Verbinde mit Server...
                                    </div>
                                    
                                    <div class="flex justify-center mb-8">
                                        <div id="board" class="game-board">
                                            <!-- Board will be generated by JavaScript -->
                                        </div>
                                    </div>
                                    
                                    <div class="space-y-4">
                                        <div class="flex justify-center space-x-4">
                                            <button onclick="resetGame()" 
                                                    class="bg-gradient-to-r from-yellow-500 to-orange-600 hover:from-yellow-600 hover:to-orange-700 text-white font-bold py-3 px-6 rounded-xl shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200">
                                                <i class="fas fa-redo"></i> Neustart
                                            </button>
                                            <button onclick="window.location.href='/'" 
                                                    class="bg-gradient-to-r from-gray-500 to-gray-600 hover:from-gray-600 hover:to-gray-700 text-white font-bold py-3 px-6 rounded-xl shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200">
                                                <i class="fas fa-home"></i> Lobby
                                            </button>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Player Info Panel -->
                                <div class="lg:order-3 stats-card rounded-2xl p-6 shadow-2xl">
                                    <h3 class="text-xl font-bold mb-4 text-center">
                                        <i class="fas fa-users text-blue-600"></i> Spieler Info
                                    </h3>
                                    <div id="playerInfo" class="space-y-4">
                                        <div class="p-4 bg-gradient-to-r from-blue-50 to-blue-100 rounded-xl">
                                            <div class="text-center">
                                                <div class="text-2xl mb-2">👤</div>
                                                <div class="font-bold text-blue-800" id="currentPlayerName">%s</div>
                                                <div class="text-sm text-blue-600" id="playerSymbol">Warte auf Zuweisung...</div>
                                            </div>
                                        </div>
                                        
                                        <div id="opponentInfo" class="p-4 bg-gradient-to-r from-red-50 to-red-100 rounded-xl">
                                            <div class="text-center">
                                                <div class="text-2xl mb-2">🤖</div>
                                                <div class="font-bold text-red-800">Warte auf Gegner...</div>
                                                <div class="text-sm text-red-600">-</div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- Game Timer -->
                                    <div class="mt-6 p-4 bg-gradient-to-r from-purple-50 to-purple-100 rounded-xl">
                                        <div class="text-center">
                                            <div class="text-sm text-purple-600 mb-1">Spielzeit</div>
                                            <div id="gameTimer" class="text-xl font-bold text-purple-800">00:00</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <script>
                        console.log('🎮 TicTacToe Game JavaScript loaded');
                        const arenaId = '%s';
                        const playerName = '%s';
                        console.log('🏟️ Arena ID:', arenaId);
                        console.log('👤 Player Name:', playerName);
                        
                        let mySymbol = '', myTurn = false, board = Array(9).fill(' '), gameStarted = false;
                        let gameStartTime = null, gameEndTime = null, timerInterval = null;
                        let soundEnabled = localStorage.getItem('gameSound') !== 'false';
                        let winningCombination = null, opponentName = '';
                        
                        const status = document.getElementById('status');
                        const boardDiv = document.getElementById('board');
                        
                        // Update sound toggle button
                        document.getElementById('soundToggle').innerHTML = soundEnabled ? 
                            '<i class="fas fa-volume-up"></i> Sound: Ein' : 
                            '<i class="fas fa-volume-mute"></i> Sound: Aus';
                        
                        // Update stats display on load
                        updateStatsDisplay();
                        
                        // Sound functions
                        function playSound(soundId) {
                            if (soundEnabled) {
                                try {
                                    const sound = document.getElementById(soundId);
                                    sound.currentTime = 0;
                                    sound.play().catch(e => console.log('Sound play failed:', e));
                                } catch (e) {
                                    console.log('Sound error:', e);
                                }
                            }
                        }
                        
                        function toggleSound() {
                            soundEnabled = !soundEnabled;
                            localStorage.setItem('gameSound', soundEnabled);
                            document.getElementById('soundToggle').innerHTML = soundEnabled ? 
                                '<i class="fas fa-volume-up"></i> Sound: Ein' : 
                                '<i class="fas fa-volume-mute"></i> Sound: Aus';
                        }
                        
                        async function showGameHelp() {
                            await Swal.fire({
                                title: '🎮 Spielhilfe',
                                html: `
                                    <div class="text-left space-y-4">
                                        <p><strong>🎯 Ziel:</strong> Bringe drei deiner Symbole in eine Reihe (horizontal, vertikal oder diagonal).</p>
                                        <p><strong>🎮 Steuerung:</strong> Klicke auf ein leeres Feld um deinen Zug zu machen.</p>
                                        <p><strong>⚡ Features:</strong></p>
                                        <ul class="list-disc list-inside space-y-1 ml-4">
                                            <li>Echtzeit-Multiplayer</li>
                                            <li>Live-Statistiken</li>
                                            <li>Sound-Effekte</li>
                                            <li>Animierte Spielbrett</li>
                                            <li>Game Timer</li>
                                        </ul>
                                    </div>
                                `,
                                icon: 'info',
                                background: '#1a1a2e',
                                color: '#fff',
                                confirmButtonColor: '#10b981'
                            });
                        }
                        
                        function updateStatsDisplay() {
                            const stats = JSON.parse(localStorage.getItem('ticTacToeStats') || '{}');
                            const playerStats = stats[playerName] || { wins: 0, losses: 0, draws: 0, gamesPlayed: 0 };
                            
                            document.getElementById('winsCount').textContent = playerStats.wins;
                            document.getElementById('lossesCount').textContent = playerStats.losses;
                            document.getElementById('drawsCount').textContent = playerStats.draws;
                            
                            const winRate = playerStats.gamesPlayed > 0 ? 
                                Math.round((playerStats.wins / playerStats.gamesPlayed) * 100) : 0;
                            document.getElementById('winRate').textContent = winRate + '%';
                        }
                        
                        function startGameTimer() {
                            if (!gameStartTime) {
                                gameStartTime = Date.now();
                                timerInterval = setInterval(() => {
                                    const elapsed = Math.floor((Date.now() - gameStartTime) / 1000);
                                    const minutes = Math.floor(elapsed / 60);
                                    const seconds = elapsed % 60;
                                    document.getElementById('gameTimer').textContent = 
                                        `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
                                }, 1000);
                            }
                        }
                        
                        function stopGameTimer() {
                            if (timerInterval) {
                                clearInterval(timerInterval);
                                timerInterval = null;
                                gameEndTime = Date.now();
                            }
                        }
                        
                        function joinGame() {
                            console.log('🔗 Attempting to join game...');
                            fetch('/api/join', {
                                method: 'POST',
                                headers: {'Content-Type': 'application/json'},
                                body: JSON.stringify({arena: arenaId, player: playerName})
                            })
                            .then(response => {
                                console.log('📥 Join response status:', response.status);
                                return response.json();
                            })
                            .then(data => {
                                console.log('📊 Join response data:', data);
                                if (data.success) {
                                    mySymbol = data.symbol;
                                    gameStarted = data.gameStarted;
                                    console.log('✅ Successfully joined as:', mySymbol, 'Game started:', gameStarted);
                                    
                                    // Update player symbol display
                                    document.getElementById('playerSymbol').textContent = `Du spielst als: ${mySymbol}`;
                                    
                                    if (gameStarted) {
                                        status.textContent = '🎮 Spiel läuft! Du bist ' + mySymbol;
                                        status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-green-100 to-green-200 text-green-800 font-bold text-lg shadow-lg pulse-glow';
                                        startGameTimer();
                                    } else {
                                        status.textContent = '⏳ Warte auf zweiten Spieler...';
                                        status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-yellow-100 to-yellow-200 text-yellow-800 font-bold text-lg shadow-lg';
                                    }
                                    startPolling();
                                } else {
                                    console.error('❌ Failed to join game:', data.message);
                                    status.textContent = '❌ Fehler: ' + data.message;
                                    status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-red-100 to-red-200 text-red-800 font-bold text-lg shadow-lg shake-animation';
                                }
                            })
                            .catch(error => {
                                console.error('❌ Error joining game:', error);
                                status.textContent = '🌐 Verbindungsfehler beim Beitreten!';
                                status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-red-100 to-red-200 text-red-800 font-bold text-lg shadow-lg shake-animation';
                            });
                        }
                        
                        function startPolling() {
                            setInterval(pollGameStatus, 1000); // Reasonable updates (1x per second)
                            drawBoard();
                        }
                        
                        function pollGameStatus() {
                            fetch('/api/status?arena=' + arenaId + '&player=' + encodeURIComponent(playerName))
                                .then(response => {
                                    if (!response.ok) {
                                        throw new Error('HTTP ' + response.status);
                                    }
                                    return response.json();
                                })
                                .then(data => {
                                    if (data.board) {
                                        const newBoard = data.board.split('');
                                        // Check if board changed to add sound and animation
                                        let boardChanged = false;
                                        for (let i = 0; i < 9; i++) {
                                            if (board[i] !== newBoard[i] && newBoard[i] !== ' ') {
                                                boardChanged = true;
                                                break;
                                            }
                                        }
                                        if (boardChanged && gameStarted) {
                                            playSound('moveSound');
                                        }
                                        board = newBoard;
                                        drawBoard();
                                    }
                                    
                                    // Update opponent info
                                    if (data.players && data.players.length === 2) {
                                        const opponent = data.players.find(p => p !== playerName);
                                        if (opponent && opponent !== opponentName) {
                                            opponentName = opponent;
                                            const opponentSymbol = mySymbol === 'X' ? 'O' : 'X';
                                            document.querySelector('#opponentInfo .font-bold').textContent = opponent;
                                            document.querySelector('#opponentInfo .text-sm').textContent = `Spielt als: ${opponentSymbol}`;
                                        }
                                    }
                                    
                                    if (data.gameStarted && !gameStarted) {
                                        gameStarted = true;
                                        status.textContent = '🚀 Spiel gestartet! Du bist ' + mySymbol;
                                        status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-green-100 to-green-200 text-green-800 font-bold text-lg shadow-lg pulse-glow';
                                        startGameTimer();
                                    }
                                    
                                    if (data.currentPlayer) {
                                        myTurn = (data.currentPlayer === mySymbol);
                                        if (gameStarted) {
                                            status.textContent = myTurn ? '⚡ Du bist dran!' : '⏳ Gegner ist dran...';
                                            status.className = myTurn ? 
                                                'mb-8 p-6 rounded-2xl bg-gradient-to-r from-blue-100 to-blue-200 text-blue-800 font-bold text-lg shadow-lg animate-pulse' :
                                                'mb-8 p-6 rounded-2xl bg-gradient-to-r from-gray-100 to-gray-200 text-gray-800 font-bold text-lg shadow-lg';
                                        }
                                    }
                                    
                                    if (data.winner) {
                                        stopGameTimer();
                                        myTurn = false;
                                        
                                        // Update stats first
                                        updatePlayerStats(data.winner);
                                        updateStatsDisplay();
                                        
                                        // Show game result with sound and visual effects
                                        if (data.winner === 'DRAW') {
                                            status.textContent = '🤝 Unentschieden! Zurück zur Lobby in 8 Sekunden...';
                                            status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-gray-100 to-gray-200 text-gray-800 font-bold text-lg shadow-lg';
                                            
                                            // Show draw modal
                                            setTimeout(() => {
                                                Swal.fire({
                                                    title: '🤝 Unentschieden!',
                                                    text: 'Ein spannender Kampf! Niemand gewinnt dieses Mal.',
                                                    icon: 'info',
                                                    background: '#1a1a2e',
                                                    color: '#fff',
                                                    confirmButtonColor: '#6b7280',
                                                    timer: 3000,
                                                    timerProgressBar: true
                                                });
                                            }, 500);
                                        } else if (data.winner === mySymbol) {
                                            status.textContent = '🎉 Du gewinnst! Zurück zur Lobby in 8 Sekunden...';
                                            status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-green-100 to-green-200 text-green-800 font-bold text-lg shadow-lg animate-bounce';
                                            playSound('winSound');
                                            
                                            // Show victory modal with confetti effect
                                            setTimeout(() => {
                                                Swal.fire({
                                                    title: '🎉 Glückwunsch!',
                                                    text: `Du hast gewonnen! 🏆 Fantastisches Spiel!`,
                                                    icon: 'success',
                                                    background: '#1a1a2e',
                                                    color: '#fff',
                                                    confirmButtonColor: '#10b981',
                                                    timer: 3000,
                                                    timerProgressBar: true,
                                                    showConfirmButton: false
                                                });
                                            }, 500);
                                        } else {
                                            status.textContent = '😔 Du verlierst! Zurück zur Lobby in 8 Sekunden...';
                                            status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-red-100 to-red-200 text-red-800 font-bold text-lg shadow-lg shake-animation';
                                            playSound('loseSound');
                                            
                                            // Show defeat modal
                                            setTimeout(() => {
                                                Swal.fire({
                                                    title: '😔 Schade!',
                                                    text: 'Du hast verloren, aber gib nicht auf! 💪',
                                                    icon: 'error',
                                                    background: '#1a1a2e',
                                                    color: '#fff',
                                                    confirmButtonColor: '#ef4444',
                                                    timer: 3000,
                                                    timerProgressBar: true
                                                });
                                            }, 500);
                                        }
                                        
                                        // Highlight winning combination if available
                                        if (data.winningCombination) {
                                            winningCombination = data.winningCombination;
                                            drawBoard();
                                        }
                                        
                                        // Auto return to lobby after 8 seconds (to allow arena cleanup)
                                        let countdown = 8;
                                        const countdownInterval = setInterval(() => {
                                            countdown--;
                                            if (countdown <= 0) {
                                                window.location.href = '/';
                                                clearInterval(countdownInterval);
                                            } else {
                                                const currentText = status.textContent;
                                                const baseText = currentText.split(' Zurück zur Lobby')[0];
                                                status.textContent = baseText + ` Zurück zur Lobby in ${countdown} Sekunden...`;
                                            }
                                        }, 1000);
                                    }
                                })
                                .catch(error => {
                                    console.error('❌ Error polling game status:', error);
                                    // Don't update status on every poll error to avoid spam
                                });
                        }
                        
                        function drawBoard() {
                            boardDiv.innerHTML = '';
                            boardDiv.className = 'grid grid-cols-3 gap-4 w-fit mx-auto p-4 bg-white bg-opacity-10 rounded-2xl backdrop-blur-sm border border-white border-opacity-20 shadow-2xl';
                            
                            for (let i = 0; i < 9; i++) {
                                const cell = document.createElement('button');
                                let cellClass = 'w-28 h-28 text-5xl font-black border-3 rounded-2xl shadow-xl transition-all duration-150 transform ';
                                
                                if (board[i] === 'X') {
                                    cellClass += 'bg-gradient-to-br from-blue-400 via-blue-500 to-blue-700 text-white border-blue-300 shadow-blue-400/50 hover:shadow-blue-400/70';
                                    cell.innerHTML = '<i class="fas fa-times"></i>';
                                } else if (board[i] === 'O') {
                                    cellClass += 'bg-gradient-to-br from-red-400 via-red-500 to-red-700 text-white border-red-300 shadow-red-400/50 hover:shadow-red-400/70';
                                    cell.innerHTML = '<i class="fas fa-circle-notch"></i>';
                                } else {
                                    if (myTurn && gameStarted) {
                                        cellClass += 'bg-gradient-to-br from-white to-gray-100 text-gray-400 border-gray-200 hover:from-purple-50 hover:to-purple-100 hover:border-purple-300 hover:shadow-purple-300/50 hover:scale-105 cursor-pointer';
                                        cell.innerHTML = '<i class="fas fa-plus opacity-30"></i>';
                                    } else {
                                        cellClass += 'bg-gradient-to-br from-gray-50 to-gray-100 text-gray-300 border-gray-200';
                                        cell.innerHTML = '<i class="fas fa-plus opacity-10"></i>';
                                    }
                                }
                                
                                // Add winning cell animation
                                if (winningCombination && winningCombination.includes(i)) {
                                    cellClass += ' winning-cell';
                                }
                                
                                // Add pulsing effect for current player's turn
                                if (board[i] === ' ' && myTurn && gameStarted) {
                                    cellClass += ' pulse-glow';
                                }
                                
                                cell.className = cellClass;
                                cell.disabled = board[i] !== ' ' || !myTurn || !gameStarted;
                                
                                if (!cell.disabled) {
                                    cell.style.cursor = 'pointer';
                                    
                                    // Immediate click feedback
                                    cell.addEventListener('mousedown', () => {
                                        if (board[i] === ' ') {
                                            cell.style.transform = 'scale(0.95)';
                                        }
                                    });
                                    
                                    cell.addEventListener('mouseup', () => {
                                        if (board[i] === ' ') {
                                            cell.style.transform = 'scale(1)';
                                        }
                                    });
                                    
                                    cell.onmouseenter = () => {
                                        if (board[i] === ' ' && myTurn && gameStarted) {
                                            cell.innerHTML = mySymbol === 'X' ? 
                                                '<i class="fas fa-times text-blue-400 opacity-60"></i>' : 
                                                '<i class="fas fa-circle-notch text-red-400 opacity-60"></i>';
                                            cell.style.transform = 'scale(1.05)';
                                        }
                                    };
                                    cell.onmouseleave = () => {
                                        if (board[i] === ' ') {
                                            cell.innerHTML = '<i class="fas fa-plus opacity-20"></i>';
                                            cell.style.transform = 'scale(1)';
                                        }
                                    };
                                } else {
                                    cell.style.cursor = board[i] !== ' ' ? 'default' : 'not-allowed';
                                    cell.style.opacity = board[i] !== ' ' ? '1' : '0.6';
                                }
                                
                                // Multiple event handlers for better responsiveness
                                cell.addEventListener('click', (e) => {
                                    e.preventDefault();
                                    makeMove(i);
                                });
                                
                                // Handle touch devices
                                cell.addEventListener('touchstart', (e) => {
                                    e.preventDefault();
                                    if (board[i] === ' ' && myTurn && gameStarted && !isMovePending) {
                                        makeMove(i);
                                    }
                                });
                                
                                boardDiv.appendChild(cell);
                            }
                        }
                        
                        let isMovePending = false;
                        
                        function makeMove(index) {
                            if (board[index] === ' ' && myTurn && gameStarted && !isMovePending) {
                                console.log('🎯 Making move at position:', index);
                                isMovePending = true;
                                
                                // Immediate visual feedback
                                const cells = document.querySelectorAll('#board button');
                                const clickedCell = cells[index];
                                
                                // Instant visual response
                                clickedCell.disabled = true;
                                clickedCell.style.opacity = '0.7';
                                clickedCell.innerHTML = mySymbol === 'X' ? 
                                    '<i class="fas fa-times text-blue-500"></i>' : 
                                    '<i class="fas fa-circle-notch text-red-500"></i>';
                                clickedCell.style.transform = 'scale(1.1)';
                                
                                // Disable all cells temporarily to prevent double clicks
                                cells.forEach(cell => cell.style.pointerEvents = 'none');
                                
                                status.textContent = '⚡ Zug wird gesendet...';
                                status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-blue-100 to-blue-200 text-blue-800 font-bold text-lg shadow-lg';
                                
                                fetch('/api/move', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/json'},
                                    body: JSON.stringify({arena: arenaId, player: playerName, position: index})
                                })
                                .then(response => response.json())
                                .then(data => {
                                    console.log('📊 Move response:', data);
                                    if (data.success) {
                                        // Move successful
                                        board[index] = mySymbol;
                                        myTurn = false;
                                        playSound('moveSound');
                                        
                                        clickedCell.style.transform = 'scale(1)';
                                        status.textContent = '✅ Zug erfolgreich! Warte auf Gegner...';
                                        status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-green-100 to-green-200 text-green-800 font-bold text-lg shadow-lg';
                                        
                                        // Immediately poll for updates after a short delay
                                        setTimeout(pollGameStatus, 200);
                                    } else {
                                        // Move failed - revert
                                        clickedCell.disabled = false;
                                        clickedCell.style.opacity = '1';
                                        clickedCell.innerHTML = '<i class="fas fa-plus opacity-20"></i>';
                                        clickedCell.style.transform = 'scale(1)';
                                        
                                        status.textContent = '❌ Ungültiger Zug - versuche einen anderen!';
                                        status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-red-100 to-red-200 text-red-800 font-bold text-lg shadow-lg';
                                    }
                                    
                                    // Re-enable all cells
                                    cells.forEach(cell => cell.style.pointerEvents = 'auto');
                                    drawBoard();
                                })
                                .catch(err => {
                                    console.error('❌ Move error:', err);
                                    // Network error - revert
                                    clickedCell.disabled = false;
                                    clickedCell.style.opacity = '1';
                                    clickedCell.innerHTML = '<i class="fas fa-plus opacity-20"></i>';
                                    clickedCell.style.transform = 'scale(1)';
                                    
                                    status.textContent = '🌐 Verbindungsfehler - versuche erneut!';
                                    status.className = 'mb-8 p-6 rounded-2xl bg-gradient-to-r from-red-100 to-red-200 text-red-800 font-bold text-lg shadow-lg';
                                    
                                    // Re-enable all cells
                                    cells.forEach(cell => cell.style.pointerEvents = 'auto');
                                    drawBoard();
                                })
                                .finally(() => {
                                    isMovePending = false;
                                });
                            }
                        }
                        
                        function updatePlayerStats(winner) {
                            let stats = JSON.parse(localStorage.getItem('ticTacToeStats') || '{}');
                            if (!stats[playerName]) {
                                stats[playerName] = { wins: 0, losses: 0, draws: 0, gamesPlayed: 0 };
                            }
                            
                            stats[playerName].gamesPlayed++;
                            
                            if (winner === 'DRAW') {
                                stats[playerName].draws++;
                            } else if (winner === mySymbol) {
                                stats[playerName].wins++;
                            } else {
                                stats[playerName].losses++;
                            }
                            
                            localStorage.setItem('ticTacToeStats', JSON.stringify(stats));
                        }
                        
                        async function resetGame() {
                            const result = await Swal.fire({
                                title: '🔄 Spiel neu starten',
                                text: 'Möchtest du das Spiel wirklich neu starten?',
                                icon: 'question',
                                showCancelButton: true,
                                confirmButtonText: '✅ Ja, neu starten!',
                                cancelButtonText: '❌ Abbrechen',
                                background: '#1a1a2e',
                                color: '#fff',
                                confirmButtonColor: '#10b981',
                                cancelButtonColor: '#6b7280'
                            });
                            
                            if (result.isConfirmed) {
                                // Stop timer and reset all game state
                                stopGameTimer();
                                
                                // Show loading animation
                                Swal.fire({
                                    title: '🔄 Wird neu gestartet...',
                                    text: 'Das Spiel wird neu geladen.',
                                    icon: 'info',
                                    background: '#1a1a2e',
                                    color: '#fff',
                                    showConfirmButton: false,
                                    allowOutsideClick: false,
                                    timer: 1500,
                                    timerProgressBar: true
                                }).then(() => {
                                    location.reload();
                                });
                            }
                        }
                        
                        // Initialize page with welcome animation
                        document.addEventListener('DOMContentLoaded', function() {
                            // Fade in animation for the whole page
                            document.body.style.opacity = '0';
                            document.body.style.transition = 'opacity 0.5s ease-in-out';
                            setTimeout(() => {
                                document.body.style.opacity = '1';
                            }, 100);
                        });
                        
                        // Start the game
                        console.log('🎮 Starting game initialization...');
                        joinGame();
                    </script>
                </body>
                </html>
                """;
            
            // Replace placeholders in order:
            // 1. Title arena name, 2. Display arena name, 3. Display player name, 
            // 4. Current player name, 5. JS arena ID, 6. JS player name
            String result = html;
            result = result.replaceFirst("%s", arena.name);      // Title
            result = result.replaceFirst("%s", arena.name);      // Display arena
            result = result.replaceFirst("%s", playerName);      // Display player  
            result = result.replaceFirst("%s", playerName);      // Current player name
            result = result.replaceFirst("%s", arena.id);        // JS arena ID
            result = result.replaceFirst("%s", playerName);      // JS player name
            return result;
        }
    }
    
    static class JoinHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                System.out.println("📥 Join request: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
                if (!"POST".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }
                
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Join request body: " + body);
                // Parse JSON manually (simple approach)
                String arenaId = extractJson(body, "arena");
                String playerName = extractJson(body, "player");
                
                Map<String, Object> response = new HashMap<>();
                
                GameArena arena = arenas.get(arenaId);
                if (arena != null) {
                    Set<String> players = arenaPlayers.get(arenaId);
                    if (players.size() < 2 && !players.contains(playerName)) {
                        players.add(playerName);
                        arena.addPlayer(playerName);
                        
                        response.put("success", true);
                        response.put("symbol", players.size() == 1 ? "X" : "O");
                        response.put("gameStarted", players.size() == 2);
                        System.out.println("✅ Player " + playerName + " joined arena " + arenaId + " as " + (players.size() == 1 ? "X" : "O"));
                    } else {
                        response.put("success", false);
                        response.put("message", "Arena ist voll oder Spieler bereits vorhanden");
                        System.err.println("❌ Failed to add player " + playerName + " to arena " + arenaId + " (full or duplicate)");
                    }
                } else {
                    response.put("success", false);
                    response.put("message", "Arena nicht gefunden");
                    System.err.println("❌ Arena not found for join: " + arenaId);
                }
                
                String jsonResponse = mapToJson(response);
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                }
                System.out.println("✅ Join response sent: " + jsonResponse);
            } catch (Exception e) {
                System.err.println("❌ Error in JoinHandler: " + e.getMessage());
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
            }
        }
    }
    
    static class MoveHandler implements HttpHandler {
        @Override
        @SuppressWarnings("UseSpecificCatch")
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Move request body: " + body); // Debug log
                
                String arenaId = extractJson(body, "arena");
                String playerName = extractJson(body, "player");
                String positionStr = extractJson(body, "position");
                
                Map<String, Object> response = new HashMap<>();
                
                if (arenaId.isEmpty() || playerName.isEmpty() || positionStr.isEmpty()) {
                    response.put("success", false);
                    response.put("message", "Missing required fields");
                } else {
                    int position = Integer.parseInt(positionStr);
                    GameArena arena = arenas.get(arenaId);
                    if (arena != null) {
                        boolean success = arena.makeMove(position, playerName);
                        response.put("success", success);
                        if (!success) {
                            response.put("message", "Invalid move");
                        }
                    } else {
                        response.put("success", false);
                        response.put("message", "Arena not found");
                    }
                }
                
                String jsonResponse = mapToJson(response);
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                System.err.println("❌ Error in MoveHandler: " + e.getMessage());
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }
    
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQuery(query);
                String arenaId = params.get("arena");
                
                Map<String, Object> response = new HashMap<>();
                
                GameArena arena = arenas.get(arenaId);
                if (arena != null) {
                    Set<String> players = arenaPlayers.get(arenaId);
                    response.put("board", new String(arena.getBoard()));
                    response.put("gameStarted", players.size() == 2);
                    response.put("currentPlayer", String.valueOf(arena.getCurrentPlayer()));
                    response.put("winner", arena.getWinner() == ' ' ? null : String.valueOf(arena.getWinner()));
                    response.put("players", players.toArray(new String[0])); // Add player list
                    if (arena.isDraw()) {
                        response.put("winner", "DRAW");
                    }
                    
                    // Auto-cleanup/reset arenas after game ends
                    boolean isGameEnded = arena.getWinner() != ' ' || arena.isDraw();
                    boolean isDefaultArena = arenaId.equals("Arena-1") || arenaId.equals("Arena-2") || arenaId.equals("Arena-3");
                    
                    if (isGameEnded && !arenasBeingDeleted.contains(arenaId)) {
                        arenasBeingDeleted.add(arenaId);
                        
                        if (isDefaultArena) {
                            // Reset default arenas (Arena-1, Arena-2, Arena-3) after game ends
                            new Thread(() -> {
                                try {
                                    Thread.sleep(10000); // Wait 10 seconds
                                    arena.reset();
                                    Set<String> arenaPlayersList = arenaPlayers.get(arenaId);
                                    if (arenaPlayersList != null) {
                                        arenaPlayersList.clear();
                                    }
                                    arenasBeingDeleted.remove(arenaId);
                                    System.out.println("🔄 Default arena auto-reset after game end: " + arenaId);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    arenasBeingDeleted.remove(arenaId);
                                }
                            }).start();
                        } else {
                            // Delete custom arenas after game ends
                            new Thread(() -> {
                                try {
                                    Thread.sleep(10000); // Wait 10 seconds
                                    arenas.remove(arenaId);
                                    arenaPlayers.remove(arenaId);
                                    arenasBeingDeleted.remove(arenaId);
                                    System.out.println("🧹 Custom arena auto-deleted after game end: " + arenaId);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    arenasBeingDeleted.remove(arenaId);
                                }
                            }).start();
                        }
                    }
                } else {
                    System.err.println("❌ Arena not found for status check: " + arenaId);
                }
                
                String jsonResponse = mapToJson(response);
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                System.err.println("❌ Error in StatusHandler: " + e.getMessage());
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
            }
        }
        
        private Map<String, String> parseQuery(String query) {
            Map<String, String> params = new HashMap<>();
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2) {
                        try {
                            params.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                        } catch (Exception e) {
                            params.put(pair[0], pair[1]);
                        }
                    }
                }
            }
            return params;
        }
    }
    
    // Simple JSON helpers
    private static String extractJson(String json, String key) {
        // Try string value first
        String stringPattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(stringPattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        
        // Try number value
        String numberPattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        p = java.util.regex.Pattern.compile(numberPattern);
        m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }
    
    private static String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else if (value instanceof Boolean) {
                sb.append(value.toString());
            } else if (value == null) {
                sb.append("null");
            } else {
                sb.append("\"").append(value).append("\"");
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
    
    static class GameArena {
        public final String id;
        public final String name;
        private final List<String> players = new ArrayList<>();
        private char[] board = new char[9];
        private char currentPlayer = 'X';
        private boolean gameActive = false;
        private char winner = ' ';
        
        public GameArena(String id, String name) {
            this.id = id;
            this.name = name;
            Arrays.fill(board, ' ');
        }
        
        public int getPlayerCount() {
            return players.size();
        }
        
        public char getCurrentPlayer() {
            return currentPlayer;
        }
        
        public char getWinner() {
            return winner;
        }
        
        public boolean isDraw() {
            if (winner != ' ') return false;
            for (char c : board) if (c == ' ') return false;
            return true;
        }
        
        public boolean addPlayer(String playerName) {
            if (players.size() < 2 && !players.contains(playerName)) {
                players.add(playerName);
                if (players.size() == 2) {
                    gameActive = true;
                }
                return true;
            }
            return false;
        }
        
        public synchronized boolean makeMove(int position, String playerName) {
            if (!gameActive || board[position] != ' ' || players.size() != 2) {
                return false;
            }
            
            // Determine if it's this player's turn
            boolean isFirstPlayer = players.get(0).equals(playerName);
            boolean isSecondPlayer = players.get(1).equals(playerName);
            
            if ((currentPlayer == 'X' && !isFirstPlayer) || 
                (currentPlayer == 'O' && !isSecondPlayer)) {
                return false;
            }
            
            board[position] = currentPlayer;
            
            if (checkWin(board, currentPlayer)) {
                gameActive = false;
                winner = currentPlayer;
            } else if (isDraw()) {
                gameActive = false;
            } else {
                currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
            }
            
            return true;
        }
        
        private boolean checkWin(char[] b, char sym) {
            int[][] wins = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
            for (int[] w : wins) {
                if (b[w[0]] == sym && b[w[1]] == sym && b[w[2]] == sym) return true;
            }
            return false;
        }
        
        public char[] getBoard() {
            return board.clone();
        }
        
        public void reset() {
            Arrays.fill(board, ' ');
            currentPlayer = 'X';
            gameActive = false;
            winner = ' ';
            players.clear();
        }
    }
    
    static class CreateArenaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String name = extractJson(body, "name");
                String description = extractJson(body, "description");
                
                Map<String, Object> response = new HashMap<>();
                
                if (name.isEmpty()) {
                    response.put("success", false);
                    response.put("message", "Arena Name ist erforderlich");
                } else {
                    // Generate unique ID
                    String arenaId = "Arena-" + System.currentTimeMillis();
                    createArena(arenaId, name + " - " + description);
                    response.put("success", true);
                    response.put("arenaId", arenaId);
                    System.out.println("✅ Neue Arena erstellt: " + arenaId + " (" + name + ")");
                }
                
                String jsonResponse = mapToJson(response);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes());
                }
            } catch (Exception e) {
                System.err.println("Error in CreateArenaHandler: " + e.getMessage());
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }
    
    static class DeleteArenaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String arenaId = extractJson(body, "arenaId");
                
                Map<String, Object> response = new HashMap<>();
                
                if (arenaId.equals("Arena-1") || arenaId.equals("Arena-2") || arenaId.equals("Arena-3")) {
                    response.put("success", false);
                    response.put("message", "Standard-Arenas können nicht gelöscht werden");
                } else if (arenas.containsKey(arenaId)) {
                    arenas.remove(arenaId);
                    arenaPlayers.remove(arenaId);
                    response.put("success", true);
                    System.out.println("🗑️ Arena gelöscht: " + arenaId);
                } else {
                    response.put("success", false);
                    response.put("message", "Arena nicht gefunden");
                }
                
                String jsonResponse = mapToJson(response);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes());
                }
            } catch (Exception e) {
                System.err.println("Error in DeleteArenaHandler: " + e.getMessage());
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }
    
    static class ResetArenaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String arenaId = extractJson(body, "arenaId");
                
                Map<String, Object> response = new HashMap<>();
                
                GameArena arena = arenas.get(arenaId);
                if (arena != null) {
                    arena.reset();
                    // Clear players from arena
                    Set<String> players = arenaPlayers.get(arenaId);
                    if (players != null) {
                        players.clear();
                    }
                    response.put("success", true);
                    System.out.println("🔄 Arena zurückgesetzt: " + arenaId);
                } else {
                    response.put("success", false);
                    response.put("message", "Arena nicht gefunden");
                }
                
                String jsonResponse = mapToJson(response);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes());
                }
            } catch (Exception e) {
                System.err.println("Error in ResetArenaHandler: " + e.getMessage());
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }

    public static void main(String[] args) {
        try {
            start();
            System.out.println("Drücke Enter zum Beenden...");
            System.in.read();
            stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
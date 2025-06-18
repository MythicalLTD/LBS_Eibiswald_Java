package systems.mythical.myjavaproject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleWebServer {
    
    public static void start() throws Exception {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/", new TicTacToeHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        
        String localIP = InetAddress.getLocalHost().getHostAddress();
        System.out.println("🌐 TicTacToe Web Server gestartet!");
        System.out.println("📍 Lokale URL: http://localhost:" + port);
        System.out.println("🌍 Netzwerk URL: http://" + localIP + ":" + port);
        System.out.println("💡 Teile die Netzwerk URL mit Freunden zum Spielen!");
        System.out.println("⏹️  Drücke Ctrl+C zum Beenden");
    }
    
    static class TicTacToeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = getHTML();
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
        
        private String getHTML() {
            return """
                <!DOCTYPE html>
                <html lang="de">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>🎮 TicTacToe Browser Spiel</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body { 
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            min-height: 100vh;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                        }
                        .container {
                            background: white;
                            border-radius: 20px;
                            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                            padding: 40px;
                            max-width: 600px;
                            width: 90%;
                        }
                        .header {
                            text-align: center;
                            margin-bottom: 30px;
                        }
                        .header h1 {
                            color: #333;
                            font-size: 2.5em;
                            margin-bottom: 10px;
                        }
                        .header p {
                            color: #666;
                            font-size: 1.1em;
                        }
                        .form-group {
                            margin-bottom: 20px;
                        }
                        .form-group label {
                            display: block;
                            margin-bottom: 8px;
                            color: #333;
                            font-weight: 600;
                        }
                        .form-group input {
                            width: 100%;
                            padding: 12px;
                            border: 2px solid #e1e5e9;
                            border-radius: 10px;
                            font-size: 16px;
                            transition: border-color 0.3s;
                        }
                        .form-group input:focus {
                            outline: none;
                            border-color: #667eea;
                        }
                        .btn {
                            width: 100%;
                            padding: 15px;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            border: none;
                            border-radius: 10px;
                            font-size: 18px;
                            font-weight: 600;
                            cursor: pointer;
                            transition: transform 0.2s;
                        }
                        .btn:hover {
                            transform: translateY(-2px);
                        }
                        .btn:disabled {
                            opacity: 0.6;
                            cursor: not-allowed;
                            transform: none;
                        }
                        .game-board {
                            display: grid;
                            grid-template-columns: repeat(3, 100px);
                            grid-gap: 10px;
                            margin: 20px auto;
                            width: 320px;
                        }
                        .cell {
                            width: 100px;
                            height: 100px;
                            font-size: 3em;
                            font-weight: bold;
                            border: 3px solid #667eea;
                            border-radius: 10px;
                            cursor: pointer;
                            background: #f8f9fa;
                            transition: all 0.3s;
                        }
                        .cell:hover {
                            background: #e9ecef;
                            transform: scale(1.05);
                        }
                        .cell:disabled {
                            cursor: not-allowed;
                            opacity: 0.8;
                        }
                        .status {
                            text-align: center;
                            margin: 20px 0;
                            padding: 15px;
                            border-radius: 10px;
                            font-weight: 600;
                            font-size: 1.2em;
                        }
                        .status.waiting { background: #fff3cd; color: #856404; }
                        .status.playing { background: #d4edda; color: #155724; }
                        .status.win { background: #d4edda; color: #155724; }
                        .status.lose { background: #f8d7da; color: #721c24; }
                        .status.draw { background: #d1ecf1; color: #0c5460; }
                        .player-info {
                            background: #f8f9fa;
                            padding: 15px;
                            border-radius: 10px;
                            margin: 20px 0;
                            text-align: center;
                        }
                        .url-display {
                            background: #e9ecef;
                            padding: 10px;
                            border-radius: 5px;
                            font-family: monospace;
                            margin: 10px 0;
                            word-break: break-all;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎮 TicTacToe Browser</h1>
                            <p>Spiele TicTacToe direkt im Browser!</p>
                        </div>
                        
                        <div id="joinForm">
                            <div class="form-group">
                                <label for="playerName">👤 Dein Name:</label>
                                <input type="text" id="playerName" placeholder="Gib deinen Namen ein" maxlength="20">
                            </div>
                            <button class="btn" onclick="startGame()">🎯 Spiel starten</button>
                        </div>
                        
                        <div id="gameInfo" style="display: none;">
                            <div class="player-info">
                                <h3>🎮 Spiel Information</h3>
                                <p><strong>Server URL:</strong></p>
                                <div class="url-display" id="serverUrl"></div>
                                <p><strong>Status:</strong> <span id="gameStatus">Warte auf Spieler...</span></p>
                                <p><strong>Verbundene Spieler:</strong> <span id="playerCount">0</span>/2</p>
                            </div>
                        </div>
                        
                        <div id="gameBoard" style="display: none;">
                            <div id="status" class="status waiting">Verbinde...</div>
                            <div id="board" class="game-board"></div>
                        </div>
                    </div>
                    
                    <script>
                        let board = Array(9).fill('');
                        let currentPlayer = 'X';
                        let gameActive = false;
                        let playerName = '';
                        
                        function startGame() {
                            playerName = document.getElementById('playerName').value.trim();
                            if (!playerName) {
                                alert('Bitte gib deinen Namen ein!');
                                return;
                            }
                            
                            document.getElementById('serverUrl').textContent = window.location.href;
                            document.getElementById('gameInfo').style.display = 'block';
                            document.getElementById('joinForm').style.display = 'none';
                            document.getElementById('gameBoard').style.display = 'block';
                            
                            initGame();
                        }
                        
                        function initGame() {
                            const status = document.getElementById('status');
                            status.textContent = 'Spiel bereit! Du bist ' + currentPlayer;
                            status.className = 'status playing';
                            gameActive = true;
                            drawBoard();
                        }
                        
                        function drawBoard() {
                            const boardDiv = document.getElementById('board');
                            boardDiv.innerHTML = '';
                            
                            for (let i = 0; i < 9; i++) {
                                const cell = document.createElement('button');
                                cell.className = 'cell';
                                cell.textContent = board[i];
                                cell.disabled = board[i] !== '' || !gameActive;
                                cell.onclick = () => makeMove(i);
                                boardDiv.appendChild(cell);
                            }
                        }
                        
                        function makeMove(index) {
                            if (board[index] === '' && gameActive) {
                                board[index] = currentPlayer;
                                drawBoard();
                                
                                if (checkWin(board, currentPlayer)) {
                                    document.getElementById('status').textContent = '🎉 ' + playerName + ' gewinnt!';
                                    document.getElementById('status').className = 'status win';
                                    gameActive = false;
                                } else if (isDraw(board)) {
                                    document.getElementById('status').textContent = '🤝 Unentschieden!';
                                    document.getElementById('status').className = 'status draw';
                                    gameActive = false;
                                } else {
                                    currentPlayer = currentPlayer === 'X' ? 'O' : 'X';
                                    document.getElementById('status').textContent = 'Spieler ' + currentPlayer + ' ist dran';
                                }
                            }
                        }
                        
                        function checkWin(board, player) {
                            const winConditions = [
                                [0, 1, 2], [3, 4, 5], [6, 7, 8], // Reihen
                                [0, 3, 6], [1, 4, 7], [2, 5, 8], // Spalten
                                [0, 4, 8], [2, 4, 6] // Diagonalen
                            ];
                            
                            return winConditions.some(condition => {
                                return condition.every(index => {
                                    return board[index] === player;
                                });
                            });
                        }
                        
                        function isDraw(board) {
                            return board.every(cell => cell !== '');
                        }
                        
                        function resetGame() {
                            board = Array(9).fill('');
                            currentPlayer = 'X';
                            gameActive = true;
                            document.getElementById('status').textContent = 'Neues Spiel! Du bist ' + currentPlayer;
                            document.getElementById('status').className = 'status playing';
                            drawBoard();
                        }
                        
                        // Reset Button hinzufügen
                        document.addEventListener('DOMContentLoaded', function() {
                            const container = document.querySelector('.container');
                            const resetBtn = document.createElement('button');
                            resetBtn.textContent = '🔄 Neues Spiel';
                            resetBtn.className = 'btn';
                            resetBtn.style.marginTop = '20px';
                            resetBtn.onclick = resetGame;
                            container.appendChild(resetBtn);
                        });
                    </script>
                </body>
                </html>
                """;
        }
    }
    
    public static void main(String[] args) {
        try {
            start();
            System.out.println("Drücke Enter zum Beenden...");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
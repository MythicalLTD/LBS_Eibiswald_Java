# 🌐 Tic Tac Toe Netzwerk-Spiel

## Übersicht
Das Tic Tac Toe Spiel wurde um Netzwerk-Funktionalität erweitert, sodass zwei Spieler über LAN gegeneinander spielen können.

## 🎮 Wie man spielt

### Schritt 1: Server starten (Host)
1. Starten Sie das Hauptprogramm
2. Wählen Sie "Terminal Modus"
3. Wählen Sie "13. Tic Tac Toe (Netzwerk/LAN)"
4. Wählen Sie "1. Server starten (Host)"
5. Geben Sie einen Port ein (Standard: 8080)
6. Der Server wartet auf den zweiten Spieler

### Schritt 2: Client verbinden (Join)
1. Starten Sie das Hauptprogramm auf einem zweiten Computer
2. Wählen Sie "Terminal Modus"
3. Wählen Sie "13. Tic Tac Toe (Netzwerk/LAN)"
4. Wählen Sie "2. Client verbinden (Join)"
5. Geben Sie die IP-Adresse des Servers ein
6. Geben Sie den gleichen Port ein wie der Server

## 🔧 Technische Details

### Netzwerk-Protokoll
- **Port**: Standard 8080 (konfigurierbar)
- **Protokoll**: TCP Socket-Verbindung
- **Nachrichtenformat**: Text-basiert mit Doppelpunkten als Trennzeichen

### Nachrichten-Typen
- `WELCOME:playerNumber:symbol` - Willkommensnachricht
- `GAME_START` - Spielstart-Benachrichtigung
- `BOARD:boardState` - Aktueller Spielstand
- `YOUR_TURN` - Spieler ist am Zug
- `OPPONENT_TURN` - Gegner ist am Zug
- `MOVE:position` - Spielzug senden
- `MOVE_MADE:position:player` - Zug bestätigen
- `INVALID_MOVE` - Ungültiger Zug
- `GAME_END:result` - Spielende
- `QUIT` - Spiel beenden

## 🚀 Direkte Ausführung

### Server starten
```bash
java -cp target/classes systems.mythical.myjavaproject.TicTacToeServer [port]
```

### Client starten
```bash
java -cp target/classes systems.mythical.myjavaproject.TicTacToeClient [serverIP] [port]
```

## 🔍 Troubleshooting

### Verbindungsprobleme
- Stellen Sie sicher, dass beide Computer im gleichen Netzwerk sind
- Überprüfen Sie die Firewall-Einstellungen
- Verwenden Sie die korrekte IP-Adresse des Servers

### Port-Probleme
- Falls Port 8080 belegt ist, verwenden Sie einen anderen Port
- Stellen Sie sicher, dass beide Spieler den gleichen Port verwenden

### Spiel-Probleme
- Bei Verbindungsabbrüchen wird das Spiel automatisch beendet
- Ungültige Züge werden automatisch abgelehnt
- Das Spiel kann jederzeit mit 'q' beendet werden

## 📱 Netzwerk-Anforderungen
- Beide Computer müssen im gleichen LAN-Netzwerk sein
- TCP-Port 8080 (oder gewählter Port) muss verfügbar sein
- Java Runtime Environment (JRE) auf beiden Computern

## 🎯 Spielregeln
Die Spielregeln sind identisch mit dem lokalen Tic Tac Toe:
- Spieler 1 (X) beginnt
- Abwechselnde Züge
- Gewinner: 3 in einer Reihe (horizontal, vertikal oder diagonal)
- Unentschieden: Alle Felder belegt ohne Gewinner 
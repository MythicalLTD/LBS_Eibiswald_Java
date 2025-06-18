@echo off
echo ========================================
echo    TicTacToe Browser Server Builder
echo ========================================
echo.

echo 🔨 Compiling Java files...
javac -cp "lib/*" -d target/classes src/main/java/systems/mythical/myjavaproject/*.java

if %errorlevel% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo ✅ Compilation successful!

echo.
echo 🎮 Creating JAR file...
jar cfm target/TicTacToe.jar manifest.txt -C target/classes .

if %errorlevel% neq 0 (
    echo ❌ JAR creation failed!
    pause
    exit /b 1
)

echo ✅ JAR file created successfully!
echo.
echo 🚀 Starting TicTacToe application...
echo.
echo 📋 Available options:
echo    1. Terminal Modus
echo    2. GUI Modus
echo.
echo 💡 Choose option 1, then select option 15 for Browser-based TicTacToe
echo.

java -cp "target/classes;lib/*" systems.mythical.myjavaproject.Main

pause 
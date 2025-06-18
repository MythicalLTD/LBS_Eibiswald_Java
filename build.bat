@echo off
echo Building MyJavaProjectV2...

REM Create target directory if it doesn't exist
if not exist "target\classes" mkdir "target\classes"

REM Compile all Java files
echo Compiling Java files...
javac -d target\classes -cp "src\main\java" src\main\java\systems\mythical\myjavaproject\*.java

REM Create manifest file
echo Creating manifest...
echo Main-Class: systems.mythical.myjavaproject.Main > manifest.txt

REM Create JAR file
echo Creating JAR file...
jar cfm MyJavaProjectV2-1.0-SNAPSHOT.jar manifest.txt -C target\classes .

REM Clean up
del manifest.txt

echo Build complete! You can now run: java -jar MyJavaProjectV2-1.0-SNAPSHOT.jar
pause 
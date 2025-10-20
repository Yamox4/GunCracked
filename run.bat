@echo off
echo Building and running JMonkeyEngine Physics Demo...
echo.

echo Compiling project...
mvn clean compile

if %ERRORLEVEL% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Running physics demo...
mvn exec:java -Dexec.mainClass="com.example.game.PhysicsDemo"

pause
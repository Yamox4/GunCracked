@echo off
echo Building and running Physics Demo...
echo.

echo Compiling project...
mvn clean compile -q

if %ERRORLEVEL% neq 0 (
    echo Build failed! Check for compilation errors.
    pause
    exit /b 1
)

echo.
echo Choose demo to run:
echo 1. Simple Physics Test (basic functionality)
echo 2. Interactive Physics Demo (full UI)
echo.
set /p choice="Enter choice (1 or 2): "

if "%choice%"=="1" (
    echo Running Simple Physics Test...
    mvn exec:java -Dexec.mainClass="com.example.demo.SimplePhysicsTest" -q
) else if "%choice%"=="2" (
    echo Running Interactive Physics Demo...
    mvn exec:java -Dexec.mainClass="com.example.demo.DemoLauncher" -q
) else (
    echo Invalid choice. Running Simple Physics Test...
    mvn exec:java -Dexec.mainClass="com.example.demo.SimplePhysicsTest" -q
)

pause
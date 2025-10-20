@echo off
echo Running Physics Demo...
echo.

echo Compiling (if needed)...
mvn compile -q

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Starting Simple Physics Test Demo...
echo Controls: SPACE=sphere, 1=box, 2=capsule, R=reset, WASD=camera
echo.

mvn exec:java -Dexec.mainClass=com.example.demo.SimplePhysicsTest

pause
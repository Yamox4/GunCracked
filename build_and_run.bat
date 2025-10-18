@echo off
echo Building and running Tron Cube Game...

rem Create output directory
mkdir out 2>nul

rem Compile Java files
echo Compiling Java files...
javac -cp "lib/*" -d out src/main/java/com/game/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!

rem Extract native libraries
echo Extracting native libraries...
cd lib
jar -xf lwjgl-natives-windows.jar
jar -xf lwjgl-glfw-natives-windows.jar
cd ..

rem Run the game
echo Starting game...
java -cp "out;lib/*" -Djava.library.path=lib com.game.Main

pause
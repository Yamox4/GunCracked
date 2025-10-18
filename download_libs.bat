@echo off
echo Downloading LWJGL libraries...

mkdir lib 2>nul

echo Downloading LWJGL core...
curl -L -o lib/lwjgl.jar "https://repo1.maven.org/maven2/org/lwjgl/lwjgl/3.3.3/lwjgl-3.3.3.jar"
curl -L -o lib/lwjgl-natives-windows.jar "https://repo1.maven.org/maven2/org/lwjgl/lwjgl/3.3.3/lwjgl-3.3.3-natives-windows.jar"

echo Downloading LWJGL GLFW...
curl -L -o lib/lwjgl-glfw.jar "https://repo1.maven.org/maven2/org/lwjgl/lwjgl-glfw/3.3.3/lwjgl-glfw-3.3.3.jar"
curl -L -o lib/lwjgl-glfw-natives-windows.jar "https://repo1.maven.org/maven2/org/lwjgl/lwjgl-glfw/3.3.3/lwjgl-glfw-3.3.3-natives-windows.jar"

echo Downloading LWJGL OpenGL...
curl -L -o lib/lwjgl-opengl.jar "https://repo1.maven.org/maven2/org/lwjgl/lwjgl-opengl/3.3.3/lwjgl-opengl-3.3.3.jar"

echo Downloading JOML...
curl -L -o lib/joml.jar "https://repo1.maven.org/maven2/org/joml/joml/1.10.5/joml-1.10.5.jar"

echo Libraries downloaded successfully!
pause
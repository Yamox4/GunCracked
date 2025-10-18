package com.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;

public class Game {
    private long window;
    private Shader shader;
    private Camera camera;
    private Cube cube;
    private Ground ground;
    private Matrix4f projectionMatrix;
    
    private Vector3f cubePosition = new Vector3f(0, 2, 0);
    private float cubeSpeed = 5.0f;
    
    // Mouse input
    private boolean firstMouse = true;
    private double lastX = 512.0;
    private double lastY = 384.0;
    private boolean cameraMode = true; // Toggle between camera mode and cube control
    private GLFWCursorPosCallback cursorPosCallback;
    
    public Game(long window) {
        this.window = window;
    }
    
    public void init() {
        shader = new Shader();
        camera = new Camera();
        cube = new Cube();
        ground = new Ground();
        
        projectionMatrix = new Matrix4f().perspective(
            (float) Math.toRadians(60.0f), 1024.0f / 768.0f, 0.1f, 100.0f);
        
        camera.setPosition(new Vector3f(0, 5, 10));
        
        // Setup mouse input
        setupMouseInput();
        
        // Initially capture the cursor for camera mode
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }
    
    private void setupMouseInput() {
        cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                if (firstMouse) {
                    lastX = xpos;
                    lastY = ypos;
                    firstMouse = false;
                }
                
                double xOffset = xpos - lastX;
                double yOffset = lastY - ypos; // Reversed since y-coordinates go from bottom to top
                
                lastX = xpos;
                lastY = ypos;
                
                if (cameraMode) {
                    camera.processMouseMovement((float) xOffset, (float) yOffset);
                }
            }
        };
        GLFW.glfwSetCursorPosCallback(window, cursorPosCallback);
    }
    
    public void update() {
        float deltaTime = 0.016f; // ~60 FPS
        
        // Toggle between camera mode and cube control mode
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_TAB) == GLFW.GLFW_PRESS) {
            // Add a simple debounce mechanism
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            cameraMode = !cameraMode;
            
            if (cameraMode) {
                GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
                firstMouse = true;
            } else {
                GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            }
        }
        
        if (cameraMode) {
            // Camera movement controls
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
                camera.processKeyboard(Camera.CameraMovement.FORWARD, deltaTime);
            }
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
                camera.processKeyboard(Camera.CameraMovement.BACKWARD, deltaTime);
            }
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
                camera.processKeyboard(Camera.CameraMovement.LEFT, deltaTime);
            }
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
                camera.processKeyboard(Camera.CameraMovement.RIGHT, deltaTime);
            }
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
                camera.processKeyboard(Camera.CameraMovement.UP, deltaTime);
            }
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
                camera.processKeyboard(Camera.CameraMovement.DOWN, deltaTime);
            }
        } else {
            // Cube movement controls
            Vector3f movement = new Vector3f();
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
                movement.z -= 1;
            }
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
                movement.z += 1;
            }
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
                movement.x -= 1;
            }
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
                movement.x += 1;
            }
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
                movement.y += 1;
            }
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
                movement.y -= 1;
            }
            
            if (movement.length() > 0) {
                movement.normalize().mul(cubeSpeed * deltaTime);
                cubePosition.add(movement);
            }
        }
        
        // ESC to exit
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
    }
    
    public void render() {
        shader.use();
        
        Matrix4f viewMatrix = camera.getViewMatrix();
        Vector3f lightPos = new Vector3f(5.0f, 10.0f, 5.0f);
        
        // Set common uniforms
        shader.setUniform("viewMatrix", viewMatrix);
        shader.setUniform("projectionMatrix", projectionMatrix);
        shader.setUniform("lightPos", lightPos);
        shader.setUniform("viewPos", camera.getPosition());
        
        // Render ground (wireframe)
        Matrix4f modelMatrix = new Matrix4f().identity();
        Matrix4f mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", new Vector3f(0.0f, 1.0f, 1.0f)); // Cyan
        shader.setUniform("isWireframe", true);
        ground.render();
        
        // Render cube (solid with lighting)
        modelMatrix = new Matrix4f().identity().translate(cubePosition);
        mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", new Vector3f(1.0f, 0.5f, 0.0f)); // Orange
        shader.setUniform("isWireframe", false);
        cube.renderSolid();
        
        // Render cube wireframe overlay
        shader.setUniform("color", new Vector3f(1.0f, 1.0f, 0.0f)); // Yellow wireframe
        shader.setUniform("isWireframe", true);
        cube.renderWireframe();
    }
    
    public void cleanup() {
        shader.cleanup();
        cube.cleanup();
        ground.cleanup();
        
        // Free callbacks
        if (cursorPosCallback != null) {
            cursorPosCallback.free();
        }
    }
}
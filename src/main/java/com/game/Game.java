package com.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Game {
    private long window;
    private Shader shader;
    private Camera camera;
    private Cube cube;
    private Ground ground;
    private Matrix4f projectionMatrix;
    
    private Vector3f cubePosition = new Vector3f(0, 2, 0);
    private float cubeSpeed = 5.0f;
    
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
        camera.lookAt(cubePosition);
    }
    
    public void update() {
        float deltaTime = 0.016f; // ~60 FPS
        
        // Handle input
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
        
        // Update camera to follow cube
        Vector3f cameraOffset = new Vector3f(0, 5, 10);
        Vector3f targetCameraPos = new Vector3f(cubePosition).add(cameraOffset);
        camera.setPosition(targetCameraPos);
        camera.lookAt(cubePosition);
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
    }
}
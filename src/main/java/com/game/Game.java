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
    private float cubeSpeed = 8.0f;

    // Mouse input for camera
    private boolean firstMouse = true;
    private double lastX = 512.0;
    private double lastY = 384.0;
    private GLFWCursorPosCallback cursorPosCallback;

    // Camera follow settings
    private float cameraDistance = 8.0f;
    private float cameraHeight = 4.0f;
    private float cameraYaw = 0.0f;
    private float cameraPitch = -20.0f;
    private float mouseSensitivity = 0.3f;

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

        // Setup mouse input for camera control
        setupMouseInput();

        // Capture the cursor for camera control
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

        // Initialize camera position
        updateCameraPosition();
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

                // Update camera rotation around cube
                cameraYaw += (float) xOffset * mouseSensitivity;
                cameraPitch += (float) yOffset * mouseSensitivity;

                // Constrain pitch
                if (cameraPitch > 45.0f) {
                    cameraPitch = 45.0f;
                }
                if (cameraPitch < -60.0f) {
                    cameraPitch = -60.0f;
                }

                updateCameraPosition();
            }
        };
        GLFW.glfwSetCursorPosCallback(window, cursorPosCallback);
    }

    private void updateCameraPosition() {
        // Calculate camera position based on cube position and rotation
        float yawRad = (float) Math.toRadians(cameraYaw);
        float pitchRad = (float) Math.toRadians(cameraPitch);

        Vector3f cameraPos = new Vector3f();
        cameraPos.x = cubePosition.x + cameraDistance * (float) Math.cos(pitchRad) * (float) Math.cos(yawRad);
        cameraPos.y = cubePosition.y + cameraHeight + cameraDistance * (float) Math.sin(pitchRad);
        cameraPos.z = cubePosition.z + cameraDistance * (float) Math.cos(pitchRad) * (float) Math.sin(yawRad);

        camera.setPosition(cameraPos);

        // Make camera look at cube
        Vector3f direction = new Vector3f(cubePosition).sub(cameraPos).normalize();
        Vector3f target = new Vector3f(cameraPos).add(direction);
        camera.lookAt(target);
    }

    public void update() {
        float deltaTime = 0.016f; // ~60 FPS

        // Calculate camera position to get the viewing direction
        Vector3f cameraPos = camera.getPosition();
        
        // Calculate the direction from camera to cube (this is our "forward" for movement)
        Vector3f cameraToTarget = new Vector3f(cubePosition).sub(cameraPos).normalize();
        
        // Project forward direction onto horizontal plane (ignore Y component for horizontal movement)
        Vector3f forward = new Vector3f(cameraToTarget.x, 0, cameraToTarget.z).normalize();
        
        // Calculate right vector (perpendicular to forward on horizontal plane)
        Vector3f right = new Vector3f(forward.z, 0, -forward.x);
        Vector3f up = new Vector3f(0, 1, 0);

        // Cube movement controls (main player) - relative to camera direction
        Vector3f movement = new Vector3f();
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            movement.add(forward); // Move forward (away from camera)
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            movement.sub(forward); // Move backward (toward camera)
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            movement.sub(right); // Move left relative to camera view
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            movement.add(right); // Move right relative to camera view
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
            movement.add(up); // Move up (always world up)
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
            movement.sub(up); // Move down (always world down)
        }

        // Apply movement to cube
        if (movement.length() > 0) {
            movement.normalize().mul(cubeSpeed * deltaTime);
            cubePosition.add(movement);

            // Update camera to follow cube
            updateCameraPosition();
        }

        // Camera distance controls
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_Q) == GLFW.GLFW_PRESS) {
            cameraDistance = Math.max(3.0f, cameraDistance - 5.0f * deltaTime);
            updateCameraPosition();
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_E) == GLFW.GLFW_PRESS) {
            cameraDistance = Math.min(15.0f, cameraDistance + 5.0f * deltaTime);
            updateCameraPosition();
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

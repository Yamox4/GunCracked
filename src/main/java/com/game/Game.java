package com.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;

public class Game {

    private final long window;
    private Shader shader;
    private Camera camera;
    private Cube cube;
    private Ground ground;
    private Matrix4f projectionMatrix;

    private Vector3f cubePosition = new Vector3f(0, 0, 0);
    private final float cubeSpeed = 8.0f;
    private final float hoverHeight = 1.5f; // Fixed hover height above ground
    private float hoverOffset = 0.0f; // For subtle hovering animation
    private float hoverTime = 0.0f;

    // Mouse input for camera
    private boolean firstMouse = true;
    private double lastX = 512.0;
    private double lastY = 384.0;
    private GLFWCursorPosCallback cursorPosCallback;
    private org.lwjgl.glfw.GLFWScrollCallback scrollCallback;

    // Camera follow settings - IMPROVED VALUES
    private float cameraDistance = 10.0f;
    private final float cameraHeight = 2.0f;
    private float cameraYaw = 0.0f;
    private float cameraPitch = 20.0f;  // Start looking down slightly
    private final float mouseSensitivity = 0.15f; // Reduced for smoother control

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
        setupScrollInput();

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

                // Update camera rotation around cube - FIXED AXES
                cameraYaw -= (float) xOffset * mouseSensitivity;   // Negative for correct left/right
                cameraPitch -= (float) yOffset * mouseSensitivity; // Negative for correct up/down

                // Constrain pitch to prevent flipping
                if (cameraPitch > 80.0f) {
                    cameraPitch = 80.0f;
                }
                if (cameraPitch < -80.0f) {
                    cameraPitch = -80.0f;
                }

                updateCameraPosition();
            }
        };
        GLFW.glfwSetCursorPosCallback(window, cursorPosCallback);
    }

    private void setupScrollInput() {
        scrollCallback = new org.lwjgl.glfw.GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                // Zoom in/out based on scroll direction
                float zoomSpeed = 2.0f;
                cameraDistance -= (float) yoffset * zoomSpeed;

                // Constrain zoom limits
                cameraDistance = Math.max(3.0f, Math.min(15.0f, cameraDistance));

                updateCameraPosition();
            }
        };
        GLFW.glfwSetScrollCallback(window, scrollCallback);
    }

    private void updateCameraPosition() {
        // Calculate camera position using spherical coordinates - FIXED IMPLEMENTATION
        float yawRad = (float) Math.toRadians(cameraYaw);
        float pitchRad = (float) Math.toRadians(cameraPitch);

        // Calculate offset from cube position
        Vector3f offset = new Vector3f();
        offset.x = cameraDistance * (float) Math.cos(pitchRad) * (float) Math.sin(yawRad);
        offset.y = cameraDistance * (float) Math.sin(pitchRad);
        offset.z = cameraDistance * (float) Math.cos(pitchRad) * (float) Math.cos(yawRad);

        // Position camera relative to cube
        Vector3f cameraPos = new Vector3f(cubePosition).add(offset);
        cameraPos.y += cameraHeight; // Add base height offset

        camera.setPosition(cameraPos);
        camera.lookAt(cubePosition); // Always look at the cube
    }

    public void update() {
        float deltaTime = 0.016f; // ~60 FPS

        // Update hover animation
        hoverTime += deltaTime * 2.0f; // Speed of hover animation
        hoverOffset = (float) Math.sin(hoverTime) * 0.1f; // Subtle up/down movement

        // Calculate movement directions based on camera yaw (horizontal rotation only)
        float yawRad = (float) Math.toRadians(cameraYaw);

        // Forward direction (where camera is "looking" horizontally)
        Vector3f forward = new Vector3f(
                (float) Math.sin(yawRad), // X component
                0.0f, // Y component (always 0 for horizontal movement)
                (float) Math.cos(yawRad) // Z component
        );

        // Right direction (perpendicular to forward)
        Vector3f right = new Vector3f(
                (float) Math.cos(yawRad), // X component
                0.0f, // Y component
                -(float) Math.sin(yawRad) // Z component
        );

        // Cube movement controls - HORIZONTAL ONLY (Tron-style hovering player)
        Vector3f movement = new Vector3f();
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            movement.sub(forward); // Move forward (into the screen from camera perspective)
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            movement.add(forward); // Move backward (toward camera)
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            movement.sub(right); // Move left
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            movement.add(right); // Move right
        }

        // Apply horizontal movement only
        if (movement.length() > 0) {
            movement.normalize().mul(cubeSpeed * deltaTime);
            // Only update X and Z coordinates (horizontal movement)
            cubePosition.x += movement.x;
            cubePosition.z += movement.z;
            // Y coordinate is always fixed at hover height + animation offset
            cubePosition.y = hoverHeight + hoverOffset;

            // Update camera to follow cube
            updateCameraPosition();
        } else {
            // Even when not moving, update Y position for hover animation
            cubePosition.y = hoverHeight + hoverOffset;
            updateCameraPosition();
        }

        // Camera zoom is now handled by mouse scroll wheel
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

        // Render hovering player cube with tron-style effects
        // First render a slightly larger, dimmer "glow" effect
        modelMatrix = new Matrix4f().identity().translate(cubePosition).scale(1.1f);
        mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", new Vector3f(0.2f, 0.6f, 1.0f)); // Dim blue glow
        shader.setUniform("isWireframe", false);
        cube.renderSolid();

        // Render main cube (solid with lighting)
        modelMatrix = new Matrix4f().identity().translate(cubePosition);
        mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", new Vector3f(0.3f, 0.8f, 1.0f)); // Bright cyan/blue
        shader.setUniform("isWireframe", false);
        cube.renderSolid();

        // Render cube wireframe overlay for tron aesthetic
        shader.setUniform("color", new Vector3f(1.0f, 1.0f, 1.0f)); // White wireframe
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
        if (scrollCallback != null) {
            scrollCallback.free();
        }
    }
}

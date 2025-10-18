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
    private ParticleSystem particleSystem;
    private EnemyManager enemyManager;
    private Matrix4f projectionMatrix;

    private Vector3f cubePosition = new Vector3f(0, 0, 0);
    private final float cubeSpeed = 5.0f;
    private final float hoverHeight = 1.5f; // Fixed hover height above ground
    private float hoverOffset = 0.0f; // For subtle hovering animation
    private float hoverTime = 0.0f;

    // Jump mechanics
    private boolean isJumping = false;
    private boolean isGrounded = true;
    private boolean spaceKeyPressed = false; // Track space key state to prevent holding
    private float jumpVelocity = 0.0f;
    private float jumpCooldownTimer = 0.0f;
    private final float jumpStrength = 10.0f;
    private final float gravity = 15.0f;
    private final float maxJumpHeight = 4.0f;
    private final float jumpCooldown = 0.5f; // 0.5 second cooldown between jumps

    // Mouse input for camera
    private boolean firstMouse = true;
    private double lastX = 800.0;
    private double lastY = 514.0;
    private GLFWCursorPosCallback cursorPosCallback;
    private org.lwjgl.glfw.GLFWScrollCallback scrollCallback;

    // Camera follow settings - IMPROVED VALUES
    private float cameraDistance = 10.0f;
    private final float cameraHeight = 2.0f;
    private float cameraYaw = 0.0f;
    private float cameraPitch = 20.0f;  // Start looking down slightly
    private final float mouseSensitivity = 0.15f; // Reduced for smoother control

    // Dynamic FOV for movement feedback
    private final float baseFov = 60.0f; // Base field of view in degrees
    private final float maxFovIncrease = 10.0f; // Maximum FOV increase when moving
    private float currentFov = 60.0f; // Current FOV value
    private final float fovZoomInSpeed = 100.0f; // Almost instant zoom when starting to move
    private final float fovZoomOutSpeed = 120.0f; // Almost instant zoom back when stopping

    // Enhanced Tron/Cyberpunk visual effects
    private float glowTime = 0.0f; // For animated glow effects
    private float lightningTime = 0.0f; // For lightning/energy effects
    private float pulseTime = 0.0f; // For pulsing energy
    private boolean isCurrentlyMoving = false; // Track movement state for rendering

    // Player health and collision system
    private float playerHealth = 100.0f;
    private final float maxPlayerHealth = 100.0f;
    private final float cubeSize = 1.0f; // Size for collision detection
    private float damageTimer = 0.0f;
    private final float damageInterval = 1.0f; // Take damage every 1 second when touching enemies

    public Game(long window) {
        this.window = window;
    }

    public void init() {
        shader = new Shader();
        camera = new Camera();
        cube = new Cube();
        ground = new Ground();
        particleSystem = new ParticleSystem();
        enemyManager = new EnemyManager();

        // Initialize with base FOV - will be updated dynamically
        updateProjectionMatrix();

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

    private void updateProjectionMatrix() {
        projectionMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(currentFov), 1600.0f / 1028.0f, 0.1f, 100.0f);
    }

    private void handleCollisions(float deltaTime) {
        // Update damage timer
        if (damageTimer > 0) {
            damageTimer -= deltaTime;
        }

        // Check player-enemy collisions
        boolean hasCollision = enemyManager.checkPlayerCollisions(cubePosition, cubeSize);

        if (hasCollision && damageTimer <= 0) {
            // Take damage
            playerHealth -= 20.0f; // 20 damage per hit
            damageTimer = damageInterval; // Reset damage timer

            // Visual feedback - extra particle burst when hit
            particleSystem.emitLightningParticles(cubePosition, true, true);

            // Check if player is dead
            if (playerHealth <= 0) {
                playerHealth = 0;
                // TODO: Handle player death (restart game, show game over, etc.)
                System.out.println("Player defeated! Health: " + playerHealth);
            } else {
                System.out.println("Player hit! Health: " + playerHealth);
            }
        }
    }

    public void update() {
        float deltaTime = 0.016f; // ~60 FPS

        // Update hover animation (always active for continuous bobbing)
        hoverTime += deltaTime * 2.0f; // Speed of hover animation

        // Update tron visual effect timers
        glowTime += deltaTime * 3.0f; // Glow animation speed
        lightningTime += deltaTime * 8.0f; // Fast lightning effects
        pulseTime += deltaTime * 4.0f; // Energy pulse speed

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

        // Update jump cooldown timer
        if (jumpCooldownTimer > 0) {
            jumpCooldownTimer -= deltaTime;
        }

        // Jump input with cooldown and double jump prevention
        boolean spaceCurrentlyPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS;

        // Only jump if:
        // 1. Space key was just pressed (not held)
        // 2. Player is grounded (not in air)
        // 3. Cooldown has expired
        // 4. Not already jumping
        if (spaceCurrentlyPressed && !spaceKeyPressed && isGrounded && jumpCooldownTimer <= 0 && !isJumping) {
            isJumping = true;
            isGrounded = false;
            jumpVelocity = jumpStrength;
            jumpCooldownTimer = jumpCooldown; // Start cooldown
        }

        // Update space key state for next frame
        spaceKeyPressed = spaceCurrentlyPressed;

        // Calculate hover offset based on movement (enhanced bobbing when moving)
        boolean isMoving = movement.length() > 0;
        isCurrentlyMoving = isMoving; // Store for rendering
        if (isMoving) {
            // Enhanced bobbing when moving - faster and more pronounced
            hoverOffset = (float) Math.sin(hoverTime * 1.5f) * 0.15f; // More pronounced bobbing
        } else {
            // Subtle bobbing when stationary
            hoverOffset = (float) Math.sin(hoverTime) * 0.08f; // Gentle bobbing
        }

        // Dynamic FOV based on movement for speed sensation
        float targetFov = isMoving ? baseFov + maxFovIncrease : baseFov;

        // Smoothly transition FOV with different speeds for zoom in/out
        if (Math.abs(currentFov - targetFov) > 0.1f) {
            if (currentFov < targetFov) {
                // Zooming in (starting to move) - fast for sudden movement feel
                currentFov += fovZoomInSpeed * deltaTime;
                if (currentFov > targetFov) {
                    currentFov = targetFov;
                }
            } else {
                // Zooming out (stopping) - even faster to avoid weird lingering effect
                currentFov -= fovZoomOutSpeed * deltaTime;
                if (currentFov < targetFov) {
                    currentFov = targetFov;
                }
            }
            // Update projection matrix with new FOV
            updateProjectionMatrix();
        }

        // Apply horizontal movement
        if (isMoving) {
            movement.normalize().mul(cubeSpeed * deltaTime);
            // Only update X and Z coordinates (horizontal movement)
            cubePosition.x += movement.x;
            cubePosition.z += movement.z;
        }

        // Handle jump physics
        if (isJumping) {
            // Apply jump velocity to Y position
            cubePosition.y += jumpVelocity * deltaTime;

            // Apply gravity to reduce jump velocity
            jumpVelocity -= gravity * deltaTime;

            // Check if landed back at hover height
            if (cubePosition.y <= hoverHeight + hoverOffset) {
                cubePosition.y = hoverHeight + hoverOffset;
                isJumping = false;
                isGrounded = true; // Player has landed
                jumpVelocity = 0.0f;
            }

            // Prevent jumping too high
            if (cubePosition.y > hoverHeight + maxJumpHeight) {
                cubePosition.y = hoverHeight + maxJumpHeight;
                jumpVelocity = 0.0f; // Stop upward movement at max height
            }
        } else {
            // Normal hover animation when not jumping
            cubePosition.y = hoverHeight + hoverOffset;
            isGrounded = true; // Player is on ground when not jumping
        }

        // Update particle system
        particleSystem.emitLightningParticles(cubePosition, isMoving, isJumping);
        particleSystem.update(deltaTime);

        // Update enemy system
        enemyManager.update(deltaTime, cubePosition);

        // Handle collisions
        handleCollisions(deltaTime);

        // Update camera to follow cube
        updateCameraPosition();

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

        // CLEAN TRON CUBE RENDERING - CONSISTENT COLORS AND SIZES
        // Layer 1: Outer purple energy field (movement-reactive only)
        if (isCurrentlyMoving || isJumping) {
            modelMatrix = new Matrix4f().identity().translate(cubePosition).scale(1.4f);
            mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
            shader.setUniform("mvpMatrix", mvpMatrix);
            shader.setUniform("modelMatrix", modelMatrix);
            shader.setUniform("color", new Vector3f(0.4f, 0.1f, 0.6f)); // Consistent deep purple
            shader.setUniform("isWireframe", false);
            cube.renderSolid();
        }

        // Layer 2: Middle purple glow (movement only)
        if (isCurrentlyMoving) {
            modelMatrix = new Matrix4f().identity().translate(cubePosition).scale(1.25f);
            mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
            shader.setUniform("mvpMatrix", mvpMatrix);
            shader.setUniform("modelMatrix", modelMatrix);
            shader.setUniform("color", new Vector3f(0.6f, 0.2f, 0.8f)); // Consistent bright purple
            shader.setUniform("isWireframe", false);
            cube.renderSolid();
        }

        // Layer 3: Inner glow (consistent size)
        modelMatrix = new Matrix4f().identity().translate(cubePosition).scale(1.1f);
        mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", new Vector3f(0.3f, 0.8f, 1.0f)); // Consistent cyan glow
        shader.setUniform("isWireframe", false);
        cube.renderSolid();

        // Layer 4: Core cube (solid, consistent)
        modelMatrix = new Matrix4f().identity().translate(cubePosition);
        mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", new Vector3f(0.5f, 0.9f, 1.0f)); // Consistent bright core
        shader.setUniform("isWireframe", false);
        cube.renderSolid();

        // Layer 5: Primary wireframe (consistent white edges)
        shader.setUniform("color", new Vector3f(1.0f, 1.0f, 1.0f)); // Consistent white edges
        shader.setUniform("isWireframe", true);
        cube.renderWireframe();

        // Layer 6: Secondary wireframe (consistent size and color)
        modelMatrix = new Matrix4f().identity().translate(cubePosition).scale(1.05f);
        mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", new Vector3f(0.4f, 0.8f, 1.0f)); // Consistent cyan edges
        shader.setUniform("isWireframe", true);
        cube.renderWireframe();

        // Layer 7: Purple energy wireframe (movement-reactive, consistent)
        if (isCurrentlyMoving) {
            modelMatrix = new Matrix4f().identity().translate(cubePosition).scale(1.15f);
            mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
            shader.setUniform("mvpMatrix", mvpMatrix);
            shader.setUniform("modelMatrix", modelMatrix);
            shader.setUniform("color", new Vector3f(0.7f, 0.3f, 0.9f)); // Consistent purple energy
            shader.setUniform("isWireframe", true);
            cube.renderWireframe();
        }

        // Layer 8: Electric purple wireframe (jumping/moving, consistent)
        if (isJumping || isCurrentlyMoving) {
            modelMatrix = new Matrix4f().identity().translate(cubePosition).scale(1.3f);
            mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
            shader.setUniform("mvpMatrix", mvpMatrix);
            shader.setUniform("modelMatrix", modelMatrix);
            shader.setUniform("color", new Vector3f(0.9f, 0.5f, 1.0f)); // Consistent electric purple
            shader.setUniform("isWireframe", true);
            cube.renderWireframe();
        }

        // Render lightning particle effects
        particleSystem.render(shader, viewMatrix, projectionMatrix);

        // Render enemies
        enemyManager.render(shader, viewMatrix, projectionMatrix);
    }

    public void cleanup() {
        shader.cleanup();
        cube.cleanup();
        ground.cleanup();
        particleSystem.cleanup();
        enemyManager.cleanup();

        // Free callbacks
        if (cursorPosCallback != null) {
            cursorPosCallback.free();
        }
        if (scrollCallback != null) {
            scrollCallback.free();
        }
    }
}

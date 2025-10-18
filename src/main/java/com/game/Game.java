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
    private BulletManager bulletManager;
    private ExplosionSystem explosionSystem;
    private CoinManager coinManager;
    private LevelSystem levelSystem;
    private UIRenderer uiRenderer;
    private Matrix4f projectionMatrix;

    private Vector3f cubePosition = new Vector3f(0, 0, 0);
    private final float cubeSpeed = 7.0f; // Increased movement speed
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
    private final float gravity = 20.0f;
    private final float maxJumpHeight = 2.0f;
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
    private final float maxFovIncrease = 4.0f; // Subtle FOV increase when moving
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
    private final float damageInterval = 1.0f; // Take damage 1 per hit
    
    // Shooting system
    private boolean leftMousePressed = false;
    private int enemiesKilled = 0;
    
    // Debug and FPS tracking
    private int frameCount = 0;
    private float fpsTimer = 0.0f;
    private int currentFps = 0;

    // Game timer and pause system
    private float gameTimer = 0.0f;
    private final float maxGameTime = 600.0f; // 10 minutes in seconds
    private boolean isPaused = false;
    private boolean escapeKeyPressed = false;
    private boolean gameEnded = false;
    private long tronCursor;

    // Proper timing system
    private long lastFrameTime = 0;
    private float deltaTime = 0.0f;

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
        bulletManager = new BulletManager();
        explosionSystem = new ExplosionSystem();
        coinManager = new CoinManager();
        levelSystem = new LevelSystem();
        uiRenderer = new UIRenderer();

        // Initialize with base FOV - will be updated dynamically
        updateProjectionMatrix();

        // Setup mouse input for camera control
        setupMouseInput();
        setupScrollInput();

        // Create custom tron cursor
        createTronCursor();

        // Capture the cursor for camera control
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

        // Initialize camera position
        updateCameraPosition();
    }

    private void setupMouseInput() {
        cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                // Don't process mouse input when paused
                if (isPaused) {
                    return;
                }

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
                cameraDistance = Math.max(3.0f, Math.min(50.0f, cameraDistance));

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
                (float) Math.toRadians(currentFov), 1600.0f / 1028.0f, 0.1f, 1000.0f);
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
            playerHealth -= 1.0f; // 1 damage per hit
            damageTimer = damageInterval; // Reset damage timer

            // Check if player is dead
            if (playerHealth <= 0) {
                playerHealth = 0;
                // TODO: Handle player death (restart game, show game over, etc.)
                System.out.println("Player defeated! Health: " + playerHealth);
            }
        }
    }

    public void update() {
        // Calculate real deltaTime
        long currentTime = System.nanoTime();
        if (lastFrameTime == 0) {
            lastFrameTime = currentTime;
        }
        deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f; // Convert to seconds
        lastFrameTime = currentTime;

        // Cap deltaTime to prevent huge jumps (e.g., when debugging)
        if (deltaTime > 0.1f) {
            deltaTime = 0.016f; // Fall back to ~60 FPS
        }
        
        // Update FPS counter
        frameCount++;
        fpsTimer += deltaTime;
        if (fpsTimer >= 1.0f) {
            currentFps = frameCount;
            frameCount = 0;
            fpsTimer = 0.0f;
        }

        // Handle pause input (ESC key)
        handlePauseInput();

        // Don't update game logic when paused or game ended
        if (isPaused || gameEnded) {
            return;
        }

        // Update game timer
        gameTimer += deltaTime;

        // Check if game time limit reached
        if (gameTimer >= maxGameTime) {
            gameEnded = true;
            System.out.println("Game completed! You survived 10 minutes!");
            return;
        }

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
        
        // Test key to spawn coins for debugging
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS) {
            coinManager.spawnTestCoin(cubePosition);
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
        
        // Handle shooting input (left mouse button OR automatic shooting)
        boolean leftMouseCurrentlyPressed = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        
        // Auto-shoot at enemies with controlled rate
        Vector3f closestEnemyPos = enemyManager.findClosestEnemy(cubePosition);
        if (closestEnemyPos != null && bulletManager.canShoot()) {
            bulletManager.shoot(new Vector3f(cubePosition).add(0, 0.5f, 0), closestEnemyPos);
        }
        
        leftMousePressed = leftMouseCurrentlyPressed;

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

        // Update particle system (keeping system active but not applying to cube)
        particleSystem.update(deltaTime);

        // Update bullet system
        bulletManager.update(deltaTime);
        
        // Update enemy system
        enemyManager.update(deltaTime, cubePosition);
        
        // Check bullet-enemy collisions and create explosions
        EnemyManager.CollisionResult collisionResult = enemyManager.checkBulletCollisions(bulletManager, explosionSystem);
        enemiesKilled += collisionResult.enemiesKilled;
        
        // Spawn coins at kill positions
        for (Vector3f killPos : collisionResult.killPositions) {
            coinManager.spawnCoin(killPos);
        }
        
        // Add camera shake for bullet impacts
        if (collisionResult.bulletsHit > 0) {
            // Light shake for bullet hits
            camera.addShake(0.15f, 0.1f);
        }
        if (collisionResult.enemiesKilled > 0) {
            // Stronger shake for killing enemies
            camera.addShake(0.2f, 0.25f);
        }
        
        // Update explosion system
        explosionSystem.update(deltaTime);
        
        // Update coin system with level system
        coinManager.update(deltaTime, cubePosition, cubeSize, levelSystem);

        // Handle collisions
        handleCollisions(deltaTime);

        // Update camera shake
        camera.updateShake(deltaTime);
        
        // Update camera to follow cube
        updateCameraPosition();

        // Camera zoom is now handled by mouse scroll wheel
        // ESC key handling is now in handlePauseInput() method
    }

    private void handlePauseInput() {
        boolean escapeCurrentlyPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS;

        // Toggle pause when ESC is pressed (not held)
        if (escapeCurrentlyPressed && !escapeKeyPressed) {
            isPaused = !isPaused;

            if (isPaused) {
                // Show custom tron cursor when paused
                GLFW.glfwSetCursor(window, tronCursor);
                GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
                System.out.println("Game paused");
            } else {
                // Hide cursor when unpaused
                GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
                firstMouse = true; // Reset mouse to prevent camera jump
                System.out.println("Game resumed");
            }
        }

        escapeKeyPressed = escapeCurrentlyPressed;
    }

    private void createTronCursor() {
        // Create a simple tron-style cursor (16x16 pixels)
        int size = 16;
        int[] pixels = new int[size * size];

        // Create a glowing cross pattern
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int index = y * size + x;

                // Create cross pattern with glow
                boolean isCross = (x == size / 2 || y == size / 2);
                boolean isGlow = (Math.abs(x - size / 2) <= 1 || Math.abs(y - size / 2) <= 1);

                if (isCross) {
                    // Bright cyan center
                    pixels[index] = 0xFF00FFFF; // ARGB: Full alpha, cyan
                } else if (isGlow) {
                    // Dimmer cyan glow
                    pixels[index] = 0x8000FFFF; // ARGB: Half alpha, cyan
                } else {
                    // Transparent
                    pixels[index] = 0x00000000;
                }
            }
        }

        // Create GLFW cursor
        org.lwjgl.glfw.GLFWImage cursorImage = org.lwjgl.glfw.GLFWImage.malloc();
        cursorImage.width(size);
        cursorImage.height(size);

        java.nio.ByteBuffer pixelBuffer = org.lwjgl.system.MemoryUtil.memAlloc(size * size * 4);
        for (int pixel : pixels) {
            pixelBuffer.put((byte) ((pixel >> 16) & 0xFF)); // R
            pixelBuffer.put((byte) ((pixel >> 8) & 0xFF));  // G
            pixelBuffer.put((byte) (pixel & 0xFF));         // B
            pixelBuffer.put((byte) ((pixel >> 24) & 0xFF)); // A
        }
        pixelBuffer.flip();
        cursorImage.pixels(pixelBuffer);

        tronCursor = GLFW.glfwCreateCursor(cursorImage, size / 2, size / 2);

        // Cleanup
        org.lwjgl.system.MemoryUtil.memFree(pixelBuffer);
        cursorImage.free();
    }

    public void render() {
        shader.use();

        Matrix4f viewMatrix = camera.getViewMatrix();
        Vector3f lightPos = new Vector3f(cubePosition.x, cubePosition.y + 8.0f, cubePosition.z); // Light follows player

        // Set common uniforms
        shader.setUniform("viewMatrix", viewMatrix);
        shader.setUniform("projectionMatrix", projectionMatrix);
        shader.setUniform("lightPos", lightPos);
        shader.setUniform("viewPos", camera.getPosition());

        // Render ground (black grid with white/grey squares)
        ground.render(shader, viewMatrix, projectionMatrix);

        // SIMPLE CARTOONY YELLOW CUBE - NO SIZE CHANGES OR GLOW EFFECTS
        // Render solid yellow cube
        Matrix4f modelMatrix = new Matrix4f().identity().translate(cubePosition);
        Matrix4f mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", new Vector3f(1.0f, 1.0f, 0.0f)); // Bright yellow
        shader.setUniform("isWireframe", false);
        cube.renderSolid();

        // Render black outline edges with thick lines
        org.lwjgl.opengl.GL11.glLineWidth(4.0f); // Thick black edges
        shader.setUniform("color", new Vector3f(0.0f, 0.0f, 0.0f)); // Black edges
        shader.setUniform("isWireframe", true);
        cube.renderWireframe();
        org.lwjgl.opengl.GL11.glLineWidth(1.0f); // Reset line width

        // Particle system render removed from cube (system still available for other uses)
        
        // Render bullets
        bulletManager.render(shader, viewMatrix, projectionMatrix);

        // Render enemies
        enemyManager.render(shader, viewMatrix, projectionMatrix);
        
        // Render explosions
        explosionSystem.render(shader, viewMatrix, projectionMatrix);
        
        // Render glowing yellow coins
        coinManager.render(shader, viewMatrix, projectionMatrix, cubePosition, cubeSize);

        // Render UI elements
        renderUI();
    }

    private void renderUI() {
        // Disable depth testing for UI
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_DEPTH_TEST);

        // Set line width for better visibility
        org.lwjgl.opengl.GL11.glLineWidth(2.0f);

        // Render timer
        uiRenderer.renderTimer(gameTimer, shader, projectionMatrix);
        
        // Render debug console
        uiRenderer.renderDebugConsole(currentFps, enemyManager.getEnemyCount(), playerHealth, enemiesKilled, coinManager.getCoinsCollected(), shader, projectionMatrix);
        
        // Render level/exp bar at bottom
        uiRenderer.renderLevelBar(levelSystem, shader, projectionMatrix);

        // Render pause overlay if paused
        if (isPaused) {
            uiRenderer.renderPauseOverlay(shader, projectionMatrix);
        }

        // Reset line width and re-enable depth testing
        org.lwjgl.opengl.GL11.glLineWidth(1.0f);
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_DEPTH_TEST);
    }
    
    // Getter for coin manager (for level-up system)
    public CoinManager getCoinManager() {
        return coinManager;
    }
    
    // Getter for level system
    public LevelSystem getLevelSystem() {
        return levelSystem;
    }

    public void cleanup() {
        shader.cleanup();
        cube.cleanup();
        ground.cleanup();
        particleSystem.cleanup();
        bulletManager.cleanup();
        explosionSystem.cleanup();
        coinManager.cleanup();
        enemyManager.cleanup();
        uiRenderer.cleanup();

        // Free callbacks
        if (cursorPosCallback != null) {
            cursorPosCallback.free();
        }
        if (scrollCallback != null) {
            scrollCallback.free();
        }

        // Cleanup custom cursor
        if (tronCursor != 0) {
            GLFW.glfwDestroyCursor(tronCursor);
        }
    }
}

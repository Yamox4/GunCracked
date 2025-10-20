package com.example.game;

import com.example.modules.CameraModule;
import com.example.modules.ConfigModule;
import com.example.modules.InputModule;
import com.example.modules.LoggingModule;
import com.example.modules.PhysicsModule;
import com.example.modules.WorldModule;
import com.example.modules.entities.Entity;
import com.example.modules.entities.Transform;
import com.example.modules.physics.BoxCollider;
import com.example.modules.physics.CapsuleCollider;
import com.example.modules.physics.RigidBody;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.AppSettings;

/**
 * Main game application
 */
public class GameApplication extends SimpleApplication {
    
    private WorldModule worldModule;
    private PhysicsModule physicsModule;
    private InputModule inputModule;
    private CameraModule cameraModule;
    
    // Game entities
    private Entity player;
    private Entity ground;
    
    @Override
    public void simpleInitApp() {
        // Initialize modules
        initializeModules();
        
        // Create the first level
        createFirstLevel();
        
        LoggingModule.info("Game initialized - First Level loaded");
        LoggingModule.info("Controls: WASD to move, Mouse to look, ESC to exit");
    }
    
    private void initializeModules() {
        // Initialize config and logging
        ConfigModule.initialize();
        LoggingModule.initialize(ConfigModule.getLogLevel());
        
        // Initialize world module
        worldModule = new WorldModule();
        worldModule.initialize();
        
        // Initialize physics module
        physicsModule = new PhysicsModule(worldModule);
        physicsModule.initialize();
        
        // Initialize input module
        inputModule = new InputModule(inputManager);
        inputModule.initialize();
        
        // Initialize camera module
        cameraModule = new CameraModule(cam);
        cameraModule.initialize();
        
        LoggingModule.info("All modules initialized");
    }
    
    private void createFirstLevel() {
        LoggingModule.info("Creating first level...");
        
        // Create ground plane
        createGround();
        
        // Create player capsule
        createPlayer();
        
        // Setup camera
        setupCamera();
        
        LoggingModule.info("First level created");
    }
    
    private void createGround() {
        // Create ground entity
        ground = worldModule.createEntity("Ground");
        
        // Add transform
        Transform groundTransform = ground.addComponent(new Transform());
        groundTransform.setPosition(0, -1, 0);
        
        // Add physics collider
        BoxCollider groundCollider = ground.addComponent(new BoxCollider(20, 2, 20));
        groundCollider.setStatic(true);
        
        // Create visual representation
        Box groundBox = new Box(10, 1, 10);
        Geometry groundGeometry = new Geometry("GroundGeometry", groundBox);
        
        Material groundMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        groundMaterial.setColor("Color", ColorRGBA.Gray);
        groundGeometry.setMaterial(groundMaterial);
        
        groundGeometry.setLocalTranslation(0, -1, 0);
        rootNode.attachChild(groundGeometry);
        
        LoggingModule.info("Ground created: 20x2x20 plane at Y=-1");
    }
    
    private void createPlayer() {
        // Create player entity
        player = worldModule.createEntity("Player");
        
        // Add transform
        Transform playerTransform = player.addComponent(new Transform());
        playerTransform.setPosition(0, 2, 0);
        
        // Add physics components
        CapsuleCollider playerCollider = player.addComponent(new CapsuleCollider(0.5f, 2.0f));
        RigidBody playerRigidBody = player.addComponent(new RigidBody());
        playerRigidBody.setMass(70.0f); // 70kg player
        playerRigidBody.setDrag(0.9f);
        playerRigidBody.setBounciness(0.0f); // No bouncing for player
        
        // Create visual representation
        Cylinder playerCylinder = new Cylinder(8, 16, 0.5f, 2.0f);
        Geometry playerGeometry = new Geometry("PlayerGeometry", playerCylinder);
        
        Material playerMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        playerMaterial.setColor("Color", ColorRGBA.Blue);
        playerGeometry.setMaterial(playerMaterial);
        
        playerGeometry.setLocalTranslation(0, 2, 0);
        rootNode.attachChild(playerGeometry);
        
        LoggingModule.info("Player created: Capsule collider (radius=0.5, height=2.0) at Y=2");
    }
    
    private void setupCamera() {
        // Position camera behind and above the player
        cameraModule.setPosition(new Vector3f(0, 4, 8));
        cameraModule.lookAt(new Vector3f(0, 2, 0), Vector3f.UNIT_Y);
        
        LoggingModule.info("Camera positioned at (0, 4, 8) looking at player");
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        // Update modules
        inputModule.update();
        cameraModule.update(tpf);
        worldModule.update(tpf);
        physicsModule.update(tpf);
        
        // Handle input
        handleInput(tpf);
        
        // Update visual representations
        updateVisuals();
        
        // Exit on ESC
        if (inputModule.isKeyPressed("ESCAPE")) {
            stop();
        }
    }
    
    private void handleInput(float tpf) {
        if (player == null) return;
        
        RigidBody playerRigidBody = player.getComponent(RigidBody.class);
        if (playerRigidBody == null) return;
        
        Vector3f movement = new Vector3f();
        float moveSpeed = 500.0f; // Force strength
        
        // Movement input
        if (inputModule.isKeyDown("MOVE_FORWARD")) {
            movement.addLocal(cameraModule.getForward().mult(moveSpeed));
        }
        if (inputModule.isKeyDown("MOVE_BACKWARD")) {
            movement.addLocal(cameraModule.getForward().mult(-moveSpeed));
        }
        if (inputModule.isKeyDown("MOVE_LEFT")) {
            movement.addLocal(cameraModule.getRight().mult(-moveSpeed));
        }
        if (inputModule.isKeyDown("MOVE_RIGHT")) {
            movement.addLocal(cameraModule.getRight().mult(moveSpeed));
        }
        
        // Apply horizontal movement (no Y component)
        movement.y = 0;
        
        if (movement.lengthSquared() > 0) {
            playerRigidBody.addForce(movement.mult(tpf));
        }
        
        // Jump
        if (inputModule.isKeyPressed("JUMP")) {
            Vector3f jumpForce = new Vector3f(0, 15000, 0);
            playerRigidBody.addImpulse(jumpForce);
        }
        
        // Mouse look
        Vector3f mouseDelta = new Vector3f(inputModule.getMouseDelta().x, inputModule.getMouseDelta().y, 0);
        if (mouseDelta.lengthSquared() > 0) {
            cameraModule.handleMouseMovement(mouseDelta.x, mouseDelta.y);
        }
    }
    
    private void updateVisuals() {
        // Update player visual position
        if (player != null) {
            Transform playerTransform = player.getComponent(Transform.class);
            if (playerTransform != null) {
                Geometry playerGeometry = (Geometry) rootNode.getChild("PlayerGeometry");
                if (playerGeometry != null) {
                    playerGeometry.setLocalTranslation(playerTransform.getWorldPosition());
                    playerGeometry.setLocalRotation(playerTransform.getWorldRotation());
                }
            }
        }
    }
    
    @Override
    public void destroy() {
        // Cleanup modules
        if (physicsModule != null) physicsModule.cleanup();
        if (worldModule != null) worldModule.cleanup();
        if (inputModule != null) inputModule.cleanup();
        if (cameraModule != null) cameraModule.cleanup();
        ConfigModule.cleanup();
        LoggingModule.cleanup();
        
        super.destroy();
    }
    
    public static void main(String[] args) {
        // Initialize config first
        ConfigModule.initialize();
        
        GameApplication app = new GameApplication();
        
        // Configure app settings from config
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Game - First Level");
        settings.setResolution(ConfigModule.getWindowWidth(), ConfigModule.getWindowHeight());
        settings.setFullscreen(ConfigModule.isFullscreen());
        settings.setVSync(ConfigModule.isVSync());
        settings.setSamples(ConfigModule.getSamples());
        
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }
}
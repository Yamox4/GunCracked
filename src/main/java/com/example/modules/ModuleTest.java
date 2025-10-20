package com.example.modules;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

/**
 * Test application for JME modules
 */
public class ModuleTest extends SimpleApplication {
    
    private InputModule inputModule;
    private CameraModule cameraModule;
    
    @Override
    public void simpleInitApp() {
        // Initialize modules
        ConfigModule.initialize();
        LoggingModule.initialize(ConfigModule.getLogLevel());
        
        // Initialize input module
        inputModule = new InputModule(inputManager);
        inputModule.initialize();
        
        // Initialize camera module
        cameraModule = new CameraModule(cam);
        cameraModule.initialize();
        
        // Set initial camera position
        cameraModule.setPosition(new Vector3f(0, 2, 5));
        
        LoggingModule.info("All modules initialized successfully");
        LoggingModule.info("Press WASD to move, mouse to look, ESC to exit");
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        // Update modules
        inputModule.update();
        cameraModule.update(tpf);
        
        // Handle input
        handleMovement(tpf);
        handleMouseLook();
        
        // Exit on ESC
        if (inputModule.isKeyPressed("ESCAPE")) {
            stop();
        }
    }
    
    private void handleMovement(float tpf) {
        Vector3f movement = new Vector3f();
        float moveSpeed = 5.0f * tpf;
        
        if (inputModule.isKeyDown("MOVE_FORWARD")) {
            movement.addLocal(0, 0, -moveSpeed);
        }
        if (inputModule.isKeyDown("MOVE_BACKWARD")) {
            movement.addLocal(0, 0, moveSpeed);
        }
        if (inputModule.isKeyDown("MOVE_LEFT")) {
            movement.addLocal(-moveSpeed, 0, 0);
        }
        if (inputModule.isKeyDown("MOVE_RIGHT")) {
            movement.addLocal(moveSpeed, 0, 0);
        }
        
        if (movement.lengthSquared() > 0) {
            cameraModule.moveRelative(movement);
        }
    }
    
    private void handleMouseLook() {
        Vector3f mouseDelta = new Vector3f(inputModule.getMouseDelta().x, inputModule.getMouseDelta().y, 0);
        if (mouseDelta.lengthSquared() > 0) {
            cameraModule.handleMouseMovement(mouseDelta.x, mouseDelta.y);
        }
    }
    
    @Override
    public void destroy() {
        // Cleanup modules
        if (inputModule != null) inputModule.cleanup();
        if (cameraModule != null) cameraModule.cleanup();
        ConfigModule.cleanup();
        LoggingModule.cleanup();
        
        super.destroy();
    }
    
    public static void main(String[] args) {
        // Initialize config first
        ConfigModule.initialize();
        
        ModuleTest app = new ModuleTest();
        
        // Configure app settings from config
        AppSettings settings = new AppSettings(true);
        settings.setTitle(ConfigModule.getWindowTitle());
        settings.setResolution(ConfigModule.getWindowWidth(), ConfigModule.getWindowHeight());
        settings.setFullscreen(ConfigModule.isFullscreen());
        settings.setVSync(ConfigModule.isVSync());
        settings.setSamples(ConfigModule.getSamples());
        
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }
}
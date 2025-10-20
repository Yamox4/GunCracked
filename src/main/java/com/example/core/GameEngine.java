package com.example.core;

import com.example.config.GameConfig;
import com.example.input.GameInputManager;
import com.example.logging.GameLogger;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

/**
 * Main game engine class that integrates all core systems
 */
public abstract class GameEngine extends SimpleApplication implements GameLoop.GameLoopListener {
    
    protected GameTime gameTime;
    protected GameInputManager gameInput;
    protected GameLoop gameLoop;
    
    private boolean engineInitialized = false;
    
    public GameEngine() {
        super();
    }
    
    @Override
    public void simpleInitApp() {
        // Initialize core systems
        initializeCoreSystem();
        
        // Initialize game-specific content
        initializeGame();
        
        engineInitialized = true;
        GameLogger.info("Game engine initialized successfully");
    }
    
    /**
     * Initialize core engine systems
     */
    private void initializeCoreSystem() {
        // Initialize logging
        GameLogger.initialize();
        GameLogger.setLevel(GameConfig.getLogLevel());
        GameLogger.info("Initializing game engine...");
        
        // Initialize time system
        gameTime = new GameTime();
        GameLogger.info("Time system initialized");
        
        // Initialize input system
        gameInput = new GameInputManager(inputManager);
        GameLogger.info("Input system initialized");
        
        // Initialize game loop
        gameLoop = new GameLoop(this);
        GameLogger.info("Game loop initialized");
        
        // Configure jME3 settings
        configureJME3();
    }
    
    /**
     * Configure jME3 specific settings
     */
    private void configureJME3() {
        // Disable jME3's default input mappings that we don't want
        inputManager.clearMappings();
        
        // Set mouse cursor visibility
        inputManager.setCursorVisible(false);
        
        GameLogger.info("jME3 configuration applied");
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        if (!engineInitialized) return;
        
        // Update time
        gameTime.update();
        
        // Update input
        gameInput.update();
        
        // Handle exit condition
        if (gameInput.isKeyPressed("ESCAPE")) {
            stop();
        }
    }
    
    // GameLoop.GameLoopListener implementation
    @Override
    public void onUpdate(double deltaTime) {
        // Variable timestep update (input, audio, etc.)
        updateVariable(deltaTime);
    }
    
    @Override
    public void onFixedUpdate(double fixedDeltaTime) {
        // Fixed timestep update (physics, game logic)
        updateFixed(fixedDeltaTime);
    }
    
    @Override
    public void onRender(double interpolation) {
        // Render with interpolation
        render(interpolation);
    }
    
    @Override
    public boolean shouldExit() {
        return gameInput != null && gameInput.isKeyPressed("ESCAPE");
    }
    
    // Abstract methods for game-specific implementation
    protected abstract void initializeGame();
    protected abstract void updateVariable(double deltaTime);
    protected abstract void updateFixed(double fixedDeltaTime);
    protected abstract void render(double interpolation);
    
    // Utility methods
    protected GameTime getGameTime() { return gameTime; }
    protected GameInputManager getGameInput() { return gameInput; }
    
    /**
     * Create and configure application settings from config
     */
    public static AppSettings createAppSettings() {
        GameConfig.load();
        
        AppSettings appSettings = new AppSettings(true);
        appSettings.setTitle("Game Engine Demo");
        appSettings.setResolution(GameConfig.getWindowWidth(), GameConfig.getWindowHeight());
        appSettings.setFullscreen(GameConfig.isFullscreen());
        appSettings.setVSync(GameConfig.isVSync());
        appSettings.setSamples(GameConfig.getSamples());
        
        return appSettings;
    }
    
    /**
     * Start the game engine
     */
    public void startEngine() {
        AppSettings settings = createAppSettings();
        setSettings(settings);
        setShowSettings(false);
        start();
    }
}
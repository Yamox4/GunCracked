package com.physics3d.engine;

/**
 * Main interface for the 3D Physics Engine.
 * Provides lifecycle management and core engine functionality.
 */
public interface PhysicsEngine {
    
    /**
     * Initializes the physics engine with the given configuration.
     * 
     * @param configuration The engine configuration settings
     * @throws PhysicsEngineException if initialization fails
     */
    void initialize(EngineConfiguration configuration);
    
    /**
     * Starts the physics engine and begins simulation.
     * 
     * @throws PhysicsEngineException if the engine is not initialized or fails to start
     */
    void start();
    
    /**
     * Updates the physics simulation by one time step.
     * 
     * @param deltaTime The time elapsed since the last update in seconds
     */
    void update(float deltaTime);
    
    /**
     * Pauses the physics simulation.
     */
    void pause();
    
    /**
     * Resumes the physics simulation from a paused state.
     */
    void resume();
    
    /**
     * Stops the physics engine and cleans up resources.
     */
    void stop();
    
    /**
     * Shuts down the physics engine completely and releases all resources.
     */
    void shutdown();
    
    /**
     * Gets the current engine configuration.
     * 
     * @return The current configuration
     */
    EngineConfiguration getConfiguration();
    
    /**
     * Checks if the engine is currently initialized.
     * 
     * @return true if initialized, false otherwise
     */
    boolean isInitialized();
    
    /**
     * Checks if the engine is currently running.
     * 
     * @return true if running, false otherwise
     */
    boolean isRunning();
    
    /**
     * Checks if the engine is currently paused.
     * 
     * @return true if paused, false otherwise
     */
    boolean isPaused();
}
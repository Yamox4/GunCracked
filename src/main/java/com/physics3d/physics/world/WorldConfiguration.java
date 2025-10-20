package com.physics3d.physics.world;

import com.jme3.math.Vector3f;

/**
 * Configuration class for physics world settings.
 * Contains settings specific to physics world creation and management.
 */
public class WorldConfiguration {
    
    private Vector3f gravity = new Vector3f(0, -9.81f, 0);
    private float timeStep = 1.0f / 60.0f;
    private int maxSubSteps = 4;
    private boolean enableContinuousCollisionDetection = true;
    private int maxCollisionObjects = 2000;
    
    /**
     * Creates a default world configuration.
     */
    public WorldConfiguration() {
        // Default values are set in field declarations
    }
    
    /**
     * Gets the gravity vector.
     * 
     * @return The gravity vector
     */
    public Vector3f getGravity() {
        return gravity.clone();
    }
    
    /**
     * Sets the gravity vector.
     * 
     * @param gravity The gravity vector
     * @return This configuration for method chaining
     */
    public WorldConfiguration setGravity(Vector3f gravity) {
        this.gravity = gravity.clone();
        return this;
    }
    
    /**
     * Gets the physics time step.
     * 
     * @return The time step in seconds
     */
    public float getTimeStep() {
        return timeStep;
    }
    
    /**
     * Sets the physics time step.
     * 
     * @param timeStep The time step in seconds
     * @return This configuration for method chaining
     */
    public WorldConfiguration setTimeStep(float timeStep) {
        this.timeStep = timeStep;
        return this;
    }
    
    /**
     * Gets the maximum number of sub-steps per frame.
     * 
     * @return The maximum sub-steps
     */
    public int getMaxSubSteps() {
        return maxSubSteps;
    }
    
    /**
     * Sets the maximum number of sub-steps per frame.
     * 
     * @param maxSubSteps The maximum sub-steps
     * @return This configuration for method chaining
     */
    public WorldConfiguration setMaxSubSteps(int maxSubSteps) {
        this.maxSubSteps = maxSubSteps;
        return this;
    }
    
    /**
     * Checks if continuous collision detection is enabled.
     * 
     * @return true if CCD is enabled
     */
    public boolean isEnableContinuousCollisionDetection() {
        return enableContinuousCollisionDetection;
    }
    
    /**
     * Enables or disables continuous collision detection.
     * 
     * @param enableContinuousCollisionDetection true to enable CCD
     * @return This configuration for method chaining
     */
    public WorldConfiguration setEnableContinuousCollisionDetection(boolean enableContinuousCollisionDetection) {
        this.enableContinuousCollisionDetection = enableContinuousCollisionDetection;
        return this;
    }
    
    /**
     * Gets the maximum number of collision objects.
     * 
     * @return The maximum collision objects
     */
    public int getMaxCollisionObjects() {
        return maxCollisionObjects;
    }
    
    /**
     * Sets the maximum number of collision objects.
     * 
     * @param maxCollisionObjects The maximum collision objects
     * @return This configuration for method chaining
     */
    public WorldConfiguration setMaxCollisionObjects(int maxCollisionObjects) {
        this.maxCollisionObjects = maxCollisionObjects;
        return this;
    }
}
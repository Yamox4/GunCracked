package com.physics3d.engine;

import com.jme3.math.Vector3f;

/**
 * Configuration class for the physics engine.
 * Contains all settings needed to initialize and configure the engine.
 */
public class EngineConfiguration {
    
    // Physics settings
    private Vector3f gravity = new Vector3f(0, -9.81f, 0);
    private float timeStep = 1.0f / 60.0f;
    private int maxSubSteps = 4;
    private float fixedTimeStep = 1.0f / 60.0f;
    
    // Debug settings
    private boolean enableDebugDraw = false;
    private boolean enableProfiling = false;
    
    // Performance settings
    private int maxRigidBodies = 1000;
    private int maxCollisionObjects = 2000;
    private boolean enableObjectPooling = true;
    
    // Collision settings
    private int defaultCollisionGroup = 1;
    private int defaultCollisionMask = -1;
    
    /**
     * Creates a default engine configuration.
     */
    public EngineConfiguration() {
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
    public EngineConfiguration setGravity(Vector3f gravity) {
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
    public EngineConfiguration setTimeStep(float timeStep) {
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
    public EngineConfiguration setMaxSubSteps(int maxSubSteps) {
        this.maxSubSteps = maxSubSteps;
        return this;
    }
    
    /**
     * Gets the fixed time step for physics simulation.
     * 
     * @return The fixed time step in seconds
     */
    public float getFixedTimeStep() {
        return fixedTimeStep;
    }
    
    /**
     * Sets the fixed time step for physics simulation.
     * 
     * @param fixedTimeStep The fixed time step in seconds
     * @return This configuration for method chaining
     */
    public EngineConfiguration setFixedTimeStep(float fixedTimeStep) {
        this.fixedTimeStep = fixedTimeStep;
        return this;
    }
    
    /**
     * Checks if debug drawing is enabled.
     * 
     * @return true if debug drawing is enabled
     */
    public boolean isEnableDebugDraw() {
        return enableDebugDraw;
    }
    
    /**
     * Enables or disables debug drawing.
     * 
     * @param enableDebugDraw true to enable debug drawing
     * @return This configuration for method chaining
     */
    public EngineConfiguration setEnableDebugDraw(boolean enableDebugDraw) {
        this.enableDebugDraw = enableDebugDraw;
        return this;
    }
    
    /**
     * Checks if profiling is enabled.
     * 
     * @return true if profiling is enabled
     */
    public boolean isEnableProfiling() {
        return enableProfiling;
    }
    
    /**
     * Enables or disables profiling.
     * 
     * @param enableProfiling true to enable profiling
     * @return This configuration for method chaining
     */
    public EngineConfiguration setEnableProfiling(boolean enableProfiling) {
        this.enableProfiling = enableProfiling;
        return this;
    }
    
    /**
     * Gets the maximum number of rigid bodies.
     * 
     * @return The maximum rigid bodies
     */
    public int getMaxRigidBodies() {
        return maxRigidBodies;
    }
    
    /**
     * Sets the maximum number of rigid bodies.
     * 
     * @param maxRigidBodies The maximum rigid bodies
     * @return This configuration for method chaining
     */
    public EngineConfiguration setMaxRigidBodies(int maxRigidBodies) {
        this.maxRigidBodies = maxRigidBodies;
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
    public EngineConfiguration setMaxCollisionObjects(int maxCollisionObjects) {
        this.maxCollisionObjects = maxCollisionObjects;
        return this;
    }
    
    /**
     * Checks if object pooling is enabled.
     * 
     * @return true if object pooling is enabled
     */
    public boolean isEnableObjectPooling() {
        return enableObjectPooling;
    }
    
    /**
     * Enables or disables object pooling.
     * 
     * @param enableObjectPooling true to enable object pooling
     * @return This configuration for method chaining
     */
    public EngineConfiguration setEnableObjectPooling(boolean enableObjectPooling) {
        this.enableObjectPooling = enableObjectPooling;
        return this;
    }
    
    /**
     * Gets the default collision group.
     * 
     * @return The default collision group
     */
    public int getDefaultCollisionGroup() {
        return defaultCollisionGroup;
    }
    
    /**
     * Sets the default collision group.
     * 
     * @param defaultCollisionGroup The default collision group
     * @return This configuration for method chaining
     */
    public EngineConfiguration setDefaultCollisionGroup(int defaultCollisionGroup) {
        this.defaultCollisionGroup = defaultCollisionGroup;
        return this;
    }
    
    /**
     * Gets the default collision mask.
     * 
     * @return The default collision mask
     */
    public int getDefaultCollisionMask() {
        return defaultCollisionMask;
    }
    
    /**
     * Sets the default collision mask.
     * 
     * @param defaultCollisionMask The default collision mask
     * @return This configuration for method chaining
     */
    public EngineConfiguration setDefaultCollisionMask(int defaultCollisionMask) {
        this.defaultCollisionMask = defaultCollisionMask;
        return this;
    }
}
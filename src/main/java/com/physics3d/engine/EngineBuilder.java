package com.physics3d.engine;

import com.jme3.math.Vector3f;

/**
 * Builder class for creating and configuring a PhysicsEngine instance.
 * Provides a fluent API for engine construction.
 */
public class EngineBuilder {
    
    private final EngineConfiguration configuration;
    
    /**
     * Creates a new engine builder with default configuration.
     */
    public EngineBuilder() {
        this.configuration = new EngineConfiguration();
    }
    
    /**
     * Creates a new engine builder with the specified configuration.
     * 
     * @param configuration The base configuration to use
     */
    public EngineBuilder(EngineConfiguration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * Sets the gravity vector.
     * 
     * @param gravity The gravity vector
     * @return This builder for method chaining
     */
    public EngineBuilder withGravity(Vector3f gravity) {
        configuration.setGravity(gravity);
        return this;
    }
    
    /**
     * Sets the gravity using individual components.
     * 
     * @param x The x component of gravity
     * @param y The y component of gravity
     * @param z The z component of gravity
     * @return This builder for method chaining
     */
    public EngineBuilder withGravity(float x, float y, float z) {
        configuration.setGravity(new Vector3f(x, y, z));
        return this;
    }
    
    /**
     * Sets the physics time step.
     * 
     * @param timeStep The time step in seconds
     * @return This builder for method chaining
     */
    public EngineBuilder withTimeStep(float timeStep) {
        configuration.setTimeStep(timeStep);
        return this;
    }
    
    /**
     * Sets the maximum number of sub-steps per frame.
     * 
     * @param maxSubSteps The maximum sub-steps
     * @return This builder for method chaining
     */
    public EngineBuilder withMaxSubSteps(int maxSubSteps) {
        configuration.setMaxSubSteps(maxSubSteps);
        return this;
    }
    
    /**
     * Sets the fixed time step for physics simulation.
     * 
     * @param fixedTimeStep The fixed time step in seconds
     * @return This builder for method chaining
     */
    public EngineBuilder withFixedTimeStep(float fixedTimeStep) {
        configuration.setFixedTimeStep(fixedTimeStep);
        return this;
    }
    
    /**
     * Enables debug drawing.
     * 
     * @return This builder for method chaining
     */
    public EngineBuilder withDebugDraw() {
        configuration.setEnableDebugDraw(true);
        return this;
    }
    
    /**
     * Sets debug drawing state.
     * 
     * @param enabled true to enable debug drawing
     * @return This builder for method chaining
     */
    public EngineBuilder withDebugDraw(boolean enabled) {
        configuration.setEnableDebugDraw(enabled);
        return this;
    }
    
    /**
     * Enables profiling.
     * 
     * @return This builder for method chaining
     */
    public EngineBuilder withProfiling() {
        configuration.setEnableProfiling(true);
        return this;
    }
    
    /**
     * Sets profiling state.
     * 
     * @param enabled true to enable profiling
     * @return This builder for method chaining
     */
    public EngineBuilder withProfiling(boolean enabled) {
        configuration.setEnableProfiling(enabled);
        return this;
    }
    
    /**
     * Sets the maximum number of rigid bodies.
     * 
     * @param maxRigidBodies The maximum rigid bodies
     * @return This builder for method chaining
     */
    public EngineBuilder withMaxRigidBodies(int maxRigidBodies) {
        configuration.setMaxRigidBodies(maxRigidBodies);
        return this;
    }
    
    /**
     * Sets the maximum number of collision objects.
     * 
     * @param maxCollisionObjects The maximum collision objects
     * @return This builder for method chaining
     */
    public EngineBuilder withMaxCollisionObjects(int maxCollisionObjects) {
        configuration.setMaxCollisionObjects(maxCollisionObjects);
        return this;
    }
    
    /**
     * Enables object pooling.
     * 
     * @return This builder for method chaining
     */
    public EngineBuilder withObjectPooling() {
        configuration.setEnableObjectPooling(true);
        return this;
    }
    
    /**
     * Sets object pooling state.
     * 
     * @param enabled true to enable object pooling
     * @return This builder for method chaining
     */
    public EngineBuilder withObjectPooling(boolean enabled) {
        configuration.setEnableObjectPooling(enabled);
        return this;
    }
    
    /**
     * Sets the default collision group.
     * 
     * @param collisionGroup The default collision group
     * @return This builder for method chaining
     */
    public EngineBuilder withDefaultCollisionGroup(int collisionGroup) {
        configuration.setDefaultCollisionGroup(collisionGroup);
        return this;
    }
    
    /**
     * Sets the default collision mask.
     * 
     * @param collisionMask The default collision mask
     * @return This builder for method chaining
     */
    public EngineBuilder withDefaultCollisionMask(int collisionMask) {
        configuration.setDefaultCollisionMask(collisionMask);
        return this;
    }
    
    /**
     * Gets the current configuration being built.
     * 
     * @return The configuration
     */
    public EngineConfiguration getConfiguration() {
        return configuration;
    }
    
    /**
     * Builds and returns a new PhysicsEngine instance with the configured settings.
     * 
     * @return A new PhysicsEngine instance
     */
    public PhysicsEngine build() {
        // This will be implemented when we create the concrete engine implementation
        throw new UnsupportedOperationException("Engine implementation not yet available");
    }
    
    /**
     * Creates a new engine builder with default settings.
     * 
     * @return A new engine builder
     */
    public static EngineBuilder create() {
        return new EngineBuilder();
    }
    
    /**
     * Creates a new engine builder with the specified configuration.
     * 
     * @param configuration The base configuration
     * @return A new engine builder
     */
    public static EngineBuilder create(EngineConfiguration configuration) {
        return new EngineBuilder(configuration);
    }
}
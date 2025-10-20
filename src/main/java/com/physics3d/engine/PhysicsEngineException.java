package com.physics3d.engine;

/**
 * Exception thrown when physics engine operations fail.
 */
public class PhysicsEngineException extends RuntimeException {
    
    /**
     * Creates a new physics engine exception with the specified message.
     * 
     * @param message The exception message
     */
    public PhysicsEngineException(String message) {
        super(message);
    }
    
    /**
     * Creates a new physics engine exception with the specified message and cause.
     * 
     * @param message The exception message
     * @param cause The underlying cause
     */
    public PhysicsEngineException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates a new physics engine exception with the specified cause.
     * 
     * @param cause The underlying cause
     */
    public PhysicsEngineException(Throwable cause) {
        super(cause);
    }
}
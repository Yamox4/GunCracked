package com.physics3d.physics.body;

/**
 * Enumeration of rigid body types in the physics simulation.
 */
public enum BodyType {
    /**
     * Static bodies have infinite mass and never move.
     * Used for immovable objects like terrain and walls.
     */
    STATIC,
    
    /**
     * Kinematic bodies have infinite mass but can be moved programmatically.
     * Used for moving platforms and objects controlled by animation.
     */
    KINEMATIC,
    
    /**
     * Dynamic bodies have finite mass and respond to forces.
     * Used for objects that should behave according to physics laws.
     */
    DYNAMIC
}
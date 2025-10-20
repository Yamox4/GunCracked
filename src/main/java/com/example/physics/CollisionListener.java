package com.example.physics;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;

/**
 * Collision detection and handling system
 * Manages collision events between different physics objects
 */
public class CollisionListener implements PhysicsCollisionListener {
    
    private Map<String, BiConsumer<PhysicsRigidBody, PhysicsRigidBody>> collisionHandlers;
    private Map<PhysicsRigidBody, String> bodyToIdMap;
    
    public CollisionListener() {
        this.collisionHandlers = new HashMap<>();
        this.bodyToIdMap = new HashMap<>();
    }
    
    /**
     * Register a physics body with an ID for collision tracking
     */
    public void registerBody(String id, PhysicsRigidBody body) {
        bodyToIdMap.put(body, id);
    }
    
    /**
     * Unregister a physics body
     */
    public void unregisterBody(PhysicsRigidBody body) {
        bodyToIdMap.remove(body);
    }
    
    /**
     * Add a collision handler for specific object types
     * @param key Collision key (e.g., "sphere-box", "capsule-plane")
     * @param handler Function to handle the collision
     */
    public void addCollisionHandler(String key, BiConsumer<PhysicsRigidBody, PhysicsRigidBody> handler) {
        collisionHandlers.put(key, handler);
    }
    
    /**
     * Remove a collision handler
     */
    public void removeCollisionHandler(String key) {
        collisionHandlers.remove(key);
    }
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        PhysicsRigidBody bodyA = (PhysicsRigidBody) event.getObjectA();
        PhysicsRigidBody bodyB = (PhysicsRigidBody) event.getObjectB();
        
        String idA = bodyToIdMap.get(bodyA);
        String idB = bodyToIdMap.get(bodyB);
        
        if (idA != null && idB != null) {
            handleCollision(bodyA, bodyB, idA, idB);
        }
    }
    
    /**
     * Handle collision between two bodies
     */
    private void handleCollision(PhysicsRigidBody bodyA, PhysicsRigidBody bodyB, String idA, String idB) {
        // Get collision shape types
        String shapeA = getShapeType(bodyA);
        String shapeB = getShapeType(bodyB);
        
        // Create collision keys
        String key1 = shapeA + "-" + shapeB;
        String key2 = shapeB + "-" + shapeA;
        
        // Try to find a specific handler
        BiConsumer<PhysicsRigidBody, PhysicsRigidBody> handler = collisionHandlers.get(key1);
        if (handler != null) {
            handler.accept(bodyA, bodyB);
            return;
        }
        
        handler = collisionHandlers.get(key2);
        if (handler != null) {
            handler.accept(bodyB, bodyA);
            return;
        }
        
        // Try generic handlers
        handler = collisionHandlers.get("any-any");
        if (handler != null) {
            handler.accept(bodyA, bodyB);
        }
        
        // Default collision handling
        handleDefaultCollision(bodyA, bodyB, idA, idB);
    }
    
    /**
     * Get the shape type name from a physics body
     */
    private String getShapeType(PhysicsRigidBody body) {
        String className = body.getCollisionShape().getClass().getSimpleName();
        return className.toLowerCase().replace("collisionshape", "");
    }
    
    /**
     * Default collision handling - can be overridden
     */
    protected void handleDefaultCollision(PhysicsRigidBody bodyA, PhysicsRigidBody bodyB, String idA, String idB) {
        // Default behavior - apply some bounce effect
        float restitution = 0.5f;
        
        // Calculate collision normal and apply impulse
        Vector3f relativeVelocity = bodyA.getLinearVelocity().subtract(bodyB.getLinearVelocity());
        Vector3f impulse = relativeVelocity.mult(-restitution * 0.1f);
        
        if (bodyA.getMass() > 0) {
            bodyA.applyImpulse(impulse, Vector3f.ZERO);
        }
        if (bodyB.getMass() > 0) {
            bodyB.applyImpulse(impulse.negate(), Vector3f.ZERO);
        }
    }
    
    /**
     * Get collision information
     */
    public CollisionInfo getCollisionInfo(PhysicsCollisionEvent event) {
        return new CollisionInfo(
            (PhysicsRigidBody) event.getObjectA(),
            (PhysicsRigidBody) event.getObjectB(),
            event.getAppliedImpulse(),
            0f, // Distance not directly available in JME3
            event.getPositionWorldOnA(),
            event.getPositionWorldOnB(),
            event.getNormalWorldOnB()
        );
    }
    
    /**
     * Clear all handlers and registered bodies
     */
    public void clear() {
        collisionHandlers.clear();
        bodyToIdMap.clear();
    }
}
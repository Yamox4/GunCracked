package com.example.physics;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;

/**
 * Contains detailed information about a collision event
 */
public class CollisionInfo {
    
    private final PhysicsRigidBody bodyA;
    private final PhysicsRigidBody bodyB;
    private final float appliedImpulse;
    private final float distance;
    private final Vector3f positionWorldOnA;
    private final Vector3f positionWorldOnB;
    private final Vector3f normalWorldOnB;
    
    public CollisionInfo(PhysicsRigidBody bodyA, PhysicsRigidBody bodyB, 
                        float appliedImpulse, float distance,
                        Vector3f positionWorldOnA, Vector3f positionWorldOnB,
                        Vector3f normalWorldOnB) {
        this.bodyA = bodyA;
        this.bodyB = bodyB;
        this.appliedImpulse = appliedImpulse;
        this.distance = distance;
        this.positionWorldOnA = positionWorldOnA.clone();
        this.positionWorldOnB = positionWorldOnB.clone();
        this.normalWorldOnB = normalWorldOnB.clone();
    }
    
    public PhysicsRigidBody getBodyA() {
        return bodyA;
    }
    
    public PhysicsRigidBody getBodyB() {
        return bodyB;
    }
    
    public float getAppliedImpulse() {
        return appliedImpulse;
    }
    
    public float getDistance() {
        return distance;
    }
    
    public Vector3f getPositionWorldOnA() {
        return positionWorldOnA.clone();
    }
    
    public Vector3f getPositionWorldOnB() {
        return positionWorldOnB.clone();
    }
    
    public Vector3f getNormalWorldOnB() {
        return normalWorldOnB.clone();
    }
    
    /**
     * Get the collision point (average of both contact points)
     */
    public Vector3f getCollisionPoint() {
        return positionWorldOnA.add(positionWorldOnB).mult(0.5f);
    }
    
    /**
     * Get the relative velocity at collision
     */
    public Vector3f getRelativeVelocity() {
        return bodyA.getLinearVelocity().subtract(bodyB.getLinearVelocity());
    }
    
    /**
     * Check if this is a high-impact collision
     */
    public boolean isHighImpact(float threshold) {
        return appliedImpulse > threshold;
    }
    
    @Override
    public String toString() {
        return String.format("CollisionInfo{bodyA=%s, bodyB=%s, impulse=%.2f, distance=%.2f}", 
                           bodyA.toString(), bodyB.toString(), appliedImpulse, distance);
    }
}
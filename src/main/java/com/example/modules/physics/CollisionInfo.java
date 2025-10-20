package com.example.modules.physics;

import com.jme3.math.Vector3f;

/**
 * Information about a collision between two colliders
 */
public class CollisionInfo {
    public final Collider colliderA;
    public final Collider colliderB;
    public final Vector3f contactPoint;
    public final Vector3f normal;
    public final float penetrationDepth;
    public final boolean hasCollision;
    
    public CollisionInfo(Collider colliderA, Collider colliderB, 
                        Vector3f contactPoint, Vector3f normal, 
                        float penetrationDepth) {
        this.colliderA = colliderA;
        this.colliderB = colliderB;
        this.contactPoint = contactPoint != null ? contactPoint.clone() : new Vector3f();
        this.normal = normal != null ? normal.clone() : new Vector3f();
        this.penetrationDepth = penetrationDepth;
        this.hasCollision = penetrationDepth > 0;
    }
    
    /**
     * Create a no-collision result
     */
    public static CollisionInfo noCollision(Collider a, Collider b) {
        return new CollisionInfo(a, b, null, null, 0);
    }
    
    /**
     * Get the other collider in this collision
     */
    public Collider getOther(Collider collider) {
        return collider == colliderA ? colliderB : colliderA;
    }
    
    @Override
    public String toString() {
        return String.format("CollisionInfo[hasCollision=%s, penetration=%.3f, normal=%s]", 
                           hasCollision, penetrationDepth, normal);
    }
}
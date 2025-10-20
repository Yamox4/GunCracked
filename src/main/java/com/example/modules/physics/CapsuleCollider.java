package com.example.modules.physics;

import com.jme3.math.Vector3f;

/**
 * Capsule collider for collision detection
 */
public class CapsuleCollider extends Collider {
    private float radius;
    private float height;
    
    public CapsuleCollider(float radius, float height) {
        this.radius = radius;
        this.height = height;
    }
    
    @Override
    public boolean intersects(Collider other) {
        if (other instanceof SphereCollider) {
            return ((SphereCollider) other).intersects(this);
        } else if (other instanceof BoxCollider) {
            return ((BoxCollider) other).intersects(this);
        } else if (other instanceof CapsuleCollider) {
            return intersectsCapsule((CapsuleCollider) other);
        }
        return false;
    }
    
    @Override
    public CollisionInfo getCollisionInfo(Collider other) {
        if (other instanceof SphereCollider) {
            CollisionInfo info = ((SphereCollider) other).getCollisionInfo(this);
            return new CollisionInfo(this, other, info.contactPoint, 
                                   info.normal.negate(), info.penetrationDepth);
        } else if (other instanceof BoxCollider) {
            CollisionInfo info = ((BoxCollider) other).getCollisionInfo(this);
            return new CollisionInfo(this, other, info.contactPoint, 
                                   info.normal.negate(), info.penetrationDepth);
        } else if (other instanceof CapsuleCollider) {
            return getCollisionInfoCapsule((CapsuleCollider) other);
        }
        return CollisionInfo.noCollision(this, other);
    }
    
    private boolean intersectsCapsule(CapsuleCollider other) {
        // Simplified capsule-capsule collision (treat as spheres)
        Vector3f center1 = getWorldCenter();
        Vector3f center2 = other.getWorldCenter();
        float distance = center1.distance(center2);
        return distance <= (radius + other.radius);
    }
    
    private CollisionInfo getCollisionInfoCapsule(CapsuleCollider other) {
        if (!intersectsCapsule(other)) {
            return CollisionInfo.noCollision(this, other);
        }
        
        Vector3f center1 = getWorldCenter();
        Vector3f center2 = other.getWorldCenter();
        Vector3f direction = center2.subtract(center1);
        float distance = direction.length();
        
        if (distance < 0.0001f) {
            direction.set(1, 0, 0);
        } else {
            direction.normalizeLocal();
        }
        
        float combinedRadius = radius + other.radius;
        float penetration = combinedRadius - distance;
        Vector3f contactPoint = center1.add(direction.mult(radius));
        
        return new CollisionInfo(this, other, contactPoint, direction, penetration);
    }
    
    /**
     * Get the top sphere center of the capsule
     */
    public Vector3f getTopCenter() {
        Vector3f center = getWorldCenter();
        float halfHeight = (height - 2 * radius) * 0.5f;
        return center.add(0, halfHeight, 0);
    }
    
    /**
     * Get the bottom sphere center of the capsule
     */
    public Vector3f getBottomCenter() {
        Vector3f center = getWorldCenter();
        float halfHeight = (height - 2 * radius) * 0.5f;
        return center.add(0, -halfHeight, 0);
    }
    
    @Override
    public Bounds getWorldBounds() {
        Vector3f center = getWorldCenter();
        float halfHeight = height * 0.5f;
        Vector3f min = center.subtract(radius, halfHeight, radius);
        Vector3f max = center.add(radius, halfHeight, radius);
        return new Bounds(min, max);
    }
    
    // Getters/Setters
    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }
    
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }
    
    @Override
    public String toString() {
        return String.format("CapsuleCollider[radius=%.2f, height=%.2f]", radius, height);
    }
}
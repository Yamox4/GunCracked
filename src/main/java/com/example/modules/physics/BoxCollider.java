package com.example.modules.physics;

import com.example.modules.entities.Transform;
import com.jme3.math.Vector3f;

/**
 * Box collider for collision detection
 */
public class BoxCollider extends Collider {
    private Vector3f size;
    
    public BoxCollider(Vector3f size) {
        this.size = size.clone();
    }
    
    public BoxCollider(float width, float height, float depth) {
        this.size = new Vector3f(width, height, depth);
    }
    
    @Override
    public boolean intersects(Collider other) {
        if (other instanceof SphereCollider) {
            return ((SphereCollider) other).intersects(this);
        } else if (other instanceof BoxCollider) {
            return intersectsBox((BoxCollider) other);
        } else if (other instanceof CapsuleCollider) {
            return intersectsCapsule((CapsuleCollider) other);
        }
        return false;
    }
    
    @Override
    public CollisionInfo getCollisionInfo(Collider other) {
        if (other instanceof SphereCollider) {
            CollisionInfo info = ((SphereCollider) other).getCollisionInfo(this);
            // Flip the collision info
            return new CollisionInfo(this, other, info.contactPoint, 
                                   info.normal.negate(), info.penetrationDepth);
        } else if (other instanceof BoxCollider) {
            return getCollisionInfoBox((BoxCollider) other);
        } else if (other instanceof CapsuleCollider) {
            return getCollisionInfoCapsule((CapsuleCollider) other);
        }
        return CollisionInfo.noCollision(this, other);
    }
    
    private boolean intersectsBox(BoxCollider other) {
        Vector3f center1 = getWorldCenter();
        Vector3f center2 = other.getWorldCenter();
        Vector3f halfSize1 = size.mult(0.5f);
        Vector3f halfSize2 = other.size.mult(0.5f);
        
        Vector3f separation = center2.subtract(center1);
        
        return Math.abs(separation.x) <= (halfSize1.x + halfSize2.x) &&
               Math.abs(separation.y) <= (halfSize1.y + halfSize2.y) &&
               Math.abs(separation.z) <= (halfSize1.z + halfSize2.z);
    }
    
    private CollisionInfo getCollisionInfoBox(BoxCollider other) {
        if (!intersectsBox(other)) {
            return CollisionInfo.noCollision(this, other);
        }
        
        Vector3f center1 = getWorldCenter();
        Vector3f center2 = other.getWorldCenter();
        Vector3f halfSize1 = size.mult(0.5f);
        Vector3f halfSize2 = other.size.mult(0.5f);
        
        Vector3f separation = center2.subtract(center1);
        Vector3f overlap = new Vector3f(
            (halfSize1.x + halfSize2.x) - Math.abs(separation.x),
            (halfSize1.y + halfSize2.y) - Math.abs(separation.y),
            (halfSize1.z + halfSize2.z) - Math.abs(separation.z)
        );
        
        // Find minimum overlap axis
        Vector3f normal = new Vector3f();
        float penetration;
        
        if (overlap.x <= overlap.y && overlap.x <= overlap.z) {
            normal.set(separation.x > 0 ? 1 : -1, 0, 0);
            penetration = overlap.x;
        } else if (overlap.y <= overlap.z) {
            normal.set(0, separation.y > 0 ? 1 : -1, 0);
            penetration = overlap.y;
        } else {
            normal.set(0, 0, separation.z > 0 ? 1 : -1);
            penetration = overlap.z;
        }
        
        Vector3f contactPoint = center1.add(center2).multLocal(0.5f);
        return new CollisionInfo(this, other, contactPoint, normal, penetration);
    }
    
    private boolean intersectsCapsule(CapsuleCollider capsule) {
        // Simplified box-capsule collision (treat capsule as sphere)
        Vector3f boxCenter = getWorldCenter();
        Vector3f capsuleCenter = capsule.getWorldCenter();
        Vector3f halfSize = size.mult(0.5f);
        
        Vector3f closest = new Vector3f();
        Vector3f localPoint = capsuleCenter.subtract(boxCenter);
        
        closest.x = Math.max(-halfSize.x, Math.min(halfSize.x, localPoint.x));
        closest.y = Math.max(-halfSize.y, Math.min(halfSize.y, localPoint.y));
        closest.z = Math.max(-halfSize.z, Math.min(halfSize.z, localPoint.z));
        
        Vector3f closestWorld = boxCenter.add(closest);
        float distance = capsuleCenter.distance(closestWorld);
        
        return distance <= capsule.getRadius();
    }
    
    private CollisionInfo getCollisionInfoCapsule(CapsuleCollider capsule) {
        if (!intersectsCapsule(capsule)) {
            return CollisionInfo.noCollision(this, capsule);
        }
        
        // Simplified implementation
        Vector3f boxCenter = getWorldCenter();
        Vector3f capsuleCenter = capsule.getWorldCenter();
        Vector3f direction = capsuleCenter.subtract(boxCenter);
        
        if (direction.lengthSquared() < 0.0001f) {
            direction.set(1, 0, 0);
        } else {
            direction.normalizeLocal();
        }
        
        Vector3f contactPoint = boxCenter.add(capsuleCenter).multLocal(0.5f);
        float penetration = 1.0f; // Simplified
        
        return new CollisionInfo(this, capsule, contactPoint, direction, penetration);
    }
    
    @Override
    public Bounds getWorldBounds() {
        Vector3f center = getWorldCenter();
        Vector3f halfSize = size.mult(0.5f);
        return new Bounds(center.subtract(halfSize), center.add(halfSize));
    }
    
    // Getters/Setters
    public Vector3f getSize() { return size.clone(); }
    public void setSize(Vector3f size) { this.size.set(size); }
    public void setSize(float width, float height, float depth) { 
        this.size.set(width, height, depth); 
    }
    
    @Override
    public String toString() {
        return String.format("BoxCollider[size=%s]", size);
    }
}
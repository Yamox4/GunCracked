package com.example.modules.physics;

import com.example.modules.entities.Transform;
import com.jme3.math.Vector3f;

/**
 * Sphere collider for collision detection
 */
public class SphereCollider extends Collider {
    private float radius;
    
    public SphereCollider(float radius) {
        this.radius = radius;
    }
    
    @Override
    public boolean intersects(Collider other) {
        if (other instanceof SphereCollider) {
            return intersectsSphere((SphereCollider) other);
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
            return getCollisionInfoSphere((SphereCollider) other);
        } else if (other instanceof BoxCollider) {
            return getCollisionInfoBox((BoxCollider) other);
        } else if (other instanceof CapsuleCollider) {
            return getCollisionInfoCapsule((CapsuleCollider) other);
        }
        return CollisionInfo.noCollision(this, other);
    }
    
    private boolean intersectsSphere(SphereCollider other) {
        Vector3f center1 = getWorldCenter();
        Vector3f center2 = other.getWorldCenter();
        float distance = center1.distance(center2);
        return distance <= (radius + other.radius);
    }
    
    private CollisionInfo getCollisionInfoSphere(SphereCollider other) {
        Vector3f center1 = getWorldCenter();
        Vector3f center2 = other.getWorldCenter();
        Vector3f direction = center2.subtract(center1);
        float distance = direction.length();
        float combinedRadius = radius + other.radius;
        
        if (distance >= combinedRadius) {
            return CollisionInfo.noCollision(this, other);
        }
        
        // Avoid division by zero
        if (distance < 0.0001f) {
            direction.set(1, 0, 0);
            distance = 0.0001f;
        } else {
            direction.normalizeLocal();
        }
        
        float penetration = combinedRadius - distance;
        Vector3f contactPoint = center1.add(direction.mult(radius));
        
        return new CollisionInfo(this, other, contactPoint, direction, penetration);
    }
    
    private boolean intersectsBox(BoxCollider box) {
        Vector3f sphereCenter = getWorldCenter();
        Vector3f boxCenter = box.getWorldCenter();
        Vector3f boxSize = box.getSize();
        
        // Find closest point on box to sphere center
        Vector3f closest = new Vector3f();
        Vector3f halfSize = boxSize.mult(0.5f);
        Vector3f localPoint = sphereCenter.subtract(boxCenter);
        
        closest.x = Math.max(-halfSize.x, Math.min(halfSize.x, localPoint.x));
        closest.y = Math.max(-halfSize.y, Math.min(halfSize.y, localPoint.y));
        closest.z = Math.max(-halfSize.z, Math.min(halfSize.z, localPoint.z));
        
        Vector3f closestWorld = boxCenter.add(closest);
        float distance = sphereCenter.distance(closestWorld);
        
        return distance <= radius;
    }
    
    private CollisionInfo getCollisionInfoBox(BoxCollider box) {
        if (!intersectsBox(box)) {
            return CollisionInfo.noCollision(this, box);
        }
        
        Vector3f sphereCenter = getWorldCenter();
        Vector3f boxCenter = box.getWorldCenter();
        Vector3f boxSize = box.getSize();
        Vector3f halfSize = boxSize.mult(0.5f);
        Vector3f localPoint = sphereCenter.subtract(boxCenter);
        
        // Find closest point on box
        Vector3f closest = new Vector3f();
        closest.x = Math.max(-halfSize.x, Math.min(halfSize.x, localPoint.x));
        closest.y = Math.max(-halfSize.y, Math.min(halfSize.y, localPoint.y));
        closest.z = Math.max(-halfSize.z, Math.min(halfSize.z, localPoint.z));
        
        Vector3f closestWorld = boxCenter.add(closest);
        Vector3f direction = sphereCenter.subtract(closestWorld);
        float distance = direction.length();
        
        if (distance < 0.0001f) {
            // Sphere center is inside box, find minimum separation
            Vector3f penetrations = new Vector3f(
                halfSize.x - Math.abs(localPoint.x),
                halfSize.y - Math.abs(localPoint.y),
                halfSize.z - Math.abs(localPoint.z)
            );
            
            if (penetrations.x <= penetrations.y && penetrations.x <= penetrations.z) {
                direction.set(localPoint.x > 0 ? 1 : -1, 0, 0);
                distance = penetrations.x;
            } else if (penetrations.y <= penetrations.z) {
                direction.set(0, localPoint.y > 0 ? 1 : -1, 0);
                distance = penetrations.y;
            } else {
                direction.set(0, 0, localPoint.z > 0 ? 1 : -1);
                distance = penetrations.z;
            }
        } else {
            direction.normalizeLocal();
        }
        
        float penetration = radius - distance;
        return new CollisionInfo(this, box, closestWorld, direction, penetration);
    }
    
    private boolean intersectsCapsule(CapsuleCollider capsule) {
        // Simplified capsule-sphere collision
        Vector3f sphereCenter = getWorldCenter();
        Vector3f capsuleCenter = capsule.getWorldCenter();
        float distance = sphereCenter.distance(capsuleCenter);
        return distance <= (radius + capsule.getRadius());
    }
    
    private CollisionInfo getCollisionInfoCapsule(CapsuleCollider capsule) {
        if (!intersectsCapsule(capsule)) {
            return CollisionInfo.noCollision(this, capsule);
        }
        
        Vector3f center1 = getWorldCenter();
        Vector3f center2 = capsule.getWorldCenter();
        Vector3f direction = center2.subtract(center1);
        float distance = direction.length();
        
        if (distance < 0.0001f) {
            direction.set(1, 0, 0);
        } else {
            direction.normalizeLocal();
        }
        
        float combinedRadius = radius + capsule.getRadius();
        float penetration = combinedRadius - distance;
        Vector3f contactPoint = center1.add(direction.mult(radius));
        
        return new CollisionInfo(this, capsule, contactPoint, direction, penetration);
    }
    
    @Override
    public Bounds getWorldBounds() {
        Vector3f center = getWorldCenter();
        Vector3f radiusVec = new Vector3f(radius, radius, radius);
        return new Bounds(center.subtract(radiusVec), center.add(radiusVec));
    }
    
    // Getters/Setters
    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }
    
    @Override
    public String toString() {
        return String.format("SphereCollider[radius=%.2f]", radius);
    }
}
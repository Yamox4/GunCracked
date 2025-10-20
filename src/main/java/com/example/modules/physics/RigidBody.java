package com.example.modules.physics;

import com.example.modules.entities.Component;
import com.example.modules.entities.Transform;
import com.jme3.math.Vector3f;

/**
 * RigidBody component for physics simulation
 */
public class RigidBody extends Component {
    private Vector3f velocity = new Vector3f();
    private Vector3f acceleration = new Vector3f();
    private float mass = 1.0f;
    private float drag = 0.98f;
    private float bounciness = 0.5f;
    private boolean useGravity = true;
    private boolean isKinematic = false;
    
    @Override
    public void update(float deltaTime) {
        if (!enabled || isKinematic) return;
        
        Transform transform = entity.getComponent(Transform.class);
        if (transform == null) return;
        
        // Apply gravity
        if (useGravity) {
            acceleration.addLocal(0, -9.81f, 0);
        }
        
        // Update velocity
        velocity.addLocal(acceleration.mult(deltaTime));
        
        // Apply drag
        velocity.multLocal(drag);
        
        // Update position
        Vector3f deltaPosition = velocity.mult(deltaTime);
        transform.translate(deltaPosition);
        
        // Reset acceleration for next frame
        acceleration.set(0, 0, 0);
    }
    
    /**
     * Apply a force to the rigidbody
     */
    public void addForce(Vector3f force) {
        acceleration.addLocal(force.divide(mass));
    }
    
    /**
     * Apply an impulse to the rigidbody
     */
    public void addImpulse(Vector3f impulse) {
        velocity.addLocal(impulse.divide(mass));
    }
    
    /**
     * Handle collision response
     */
    public void handleCollision(CollisionInfo collision, RigidBody otherBody) {
        if (isKinematic) return;
        
        Vector3f normal = collision.normal;
        float penetration = collision.penetrationDepth;
        
        // Separate objects
        Transform transform = entity.getComponent(Transform.class);
        if (transform != null && penetration > 0) {
            Vector3f separation = normal.mult(penetration * 0.5f);
            
            if (otherBody == null || otherBody.isKinematic) {
                // Other object is static, move this object fully
                transform.translate(separation.negate());
            } else {
                // Both objects are dynamic, move both
                transform.translate(separation.negate().mult(0.5f));
                Transform otherTransform = otherBody.entity.getComponent(Transform.class);
                if (otherTransform != null) {
                    otherTransform.translate(separation.mult(0.5f));
                }
            }
        }
        
        // Apply collision response (bounce)
        float relativeVelocity = velocity.dot(normal);
        if (relativeVelocity > 0) return; // Objects separating
        
        float otherMass = otherBody != null ? otherBody.mass : Float.POSITIVE_INFINITY;
        float combinedBounciness = otherBody != null ? 
            (bounciness + otherBody.bounciness) * 0.5f : bounciness;
        
        float impulseScalar = -(1 + combinedBounciness) * relativeVelocity;
        impulseScalar /= (1.0f / mass + (otherBody != null ? 1.0f / otherMass : 0));
        
        Vector3f impulse = normal.mult(impulseScalar);
        addImpulse(impulse);
        
        if (otherBody != null && !otherBody.isKinematic) {
            otherBody.addImpulse(impulse.negate());
        }
    }
    
    // Getters/Setters
    public Vector3f getVelocity() { return velocity.clone(); }
    public void setVelocity(Vector3f velocity) { this.velocity.set(velocity); }
    public void setVelocity(float x, float y, float z) { this.velocity.set(x, y, z); }
    
    public Vector3f getAcceleration() { return acceleration.clone(); }
    public void setAcceleration(Vector3f acceleration) { this.acceleration.set(acceleration); }
    
    public float getMass() { return mass; }
    public void setMass(float mass) { this.mass = Math.max(0.001f, mass); }
    
    public float getDrag() { return drag; }
    public void setDrag(float drag) { this.drag = Math.max(0, Math.min(1, drag)); }
    
    public float getBounciness() { return bounciness; }
    public void setBounciness(float bounciness) { this.bounciness = Math.max(0, Math.min(1, bounciness)); }
    
    public boolean isUseGravity() { return useGravity; }
    public void setUseGravity(boolean useGravity) { this.useGravity = useGravity; }
    
    public boolean isKinematic() { return isKinematic; }
    public void setKinematic(boolean kinematic) { this.isKinematic = kinematic; }
    
    @Override
    public String toString() {
        return String.format("RigidBody[mass=%.2f, velocity=%s, kinematic=%s]", 
                           mass, velocity, isKinematic);
    }
}
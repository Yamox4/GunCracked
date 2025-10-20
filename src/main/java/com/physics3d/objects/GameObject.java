package com.physics3d.objects;

import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;

/**
 * Simple game object with position and basic properties.
 */
public class GameObject {
    
    protected Vector3f position;
    protected Vector3f velocity;
    protected ColorRGBA color;
    protected String name;
    protected boolean active;
    
    public GameObject(String name, Vector3f position) {
        this.name = name;
        this.position = position.clone();
        this.velocity = new Vector3f();
        this.color = ColorRGBA.White;
        this.active = true;
    }
    
    public GameObject(String name, Vector3f position, ColorRGBA color) {
        this(name, position);
        this.color = color;
    }
    
    /**
     * Update the game object.
     */
    public void update(float deltaTime) {
        if (!active) return;
        
        // Simple physics - apply velocity to position
        position.addLocal(velocity.mult(deltaTime));
    }
    
    // Getters and setters
    public Vector3f getPosition() { return position.clone(); }
    public void setPosition(Vector3f position) { this.position = position.clone(); }
    
    public Vector3f getVelocity() { return velocity.clone(); }
    public void setVelocity(Vector3f velocity) { this.velocity = velocity.clone(); }
    
    public ColorRGBA getColor() { return color; }
    public void setColor(ColorRGBA color) { this.color = color; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    @Override
    public String toString() {
        return name + " at " + position;
    }
}
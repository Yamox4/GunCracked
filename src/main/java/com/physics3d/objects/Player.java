package com.physics3d.objects;

import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;

/**
 * Simple player object.
 */
public class Player extends GameObject {
    
    private float moveSpeed = 5.0f;
    private float health = 100.0f;
    
    public Player(Vector3f position) {
        super("Player", position, ColorRGBA.Yellow);
    }
    
    /**
     * Move the player in a direction.
     */
    public void move(Vector3f direction, float deltaTime) {
        Vector3f movement = direction.mult(moveSpeed * deltaTime);
        position.addLocal(movement);
    }
    
    /**
     * Make the player jump.
     */
    public void jump() {
        velocity.y += 10.0f; // Simple jump impulse
    }
    
    // Player-specific getters/setters
    public float getMoveSpeed() { return moveSpeed; }
    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }
    
    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = Math.max(0, Math.min(100, health)); }
    
    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            System.out.println("Player died!");
            active = false;
        }
    }
    
    public void heal(float amount) {
        health = Math.min(100, health + amount);
    }
}
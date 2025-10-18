package com.game;

import org.joml.Vector3f;

public abstract class Enemy {
    protected Vector3f position;
    protected Vector3f velocity;
    protected Vector3f color;
    protected float health;
    protected float maxHealth;
    protected float speed;
    protected float size;
    protected boolean alive;
    protected float animationTime;
    protected boolean inCollision;
    
    public Enemy(Vector3f startPosition, float enemyHealth, float enemySpeed, float enemySize) {
        this.position = new Vector3f(startPosition);
        this.velocity = new Vector3f();
        this.health = enemyHealth;
        this.maxHealth = enemyHealth;
        this.speed = enemySpeed;
        this.size = enemySize;
        this.alive = true;
        this.animationTime = 0.0f;
        this.inCollision = false;
        this.color = new Vector3f(1.0f, 0.0f, 0.0f); // Default red
    }
    
    public abstract void update(float deltaTime, Vector3f playerPosition);
    public abstract Vector3f getColor();
    
    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            alive = false;
        }
    }
    
    // Getters and Setters
    public Vector3f getPosition() { return new Vector3f(position); }
    public void setPosition(Vector3f newPosition) { this.position.set(newPosition); }
    public void setInCollision(boolean collision) { this.inCollision = collision; }
    public float getSize() { return size; }
    public boolean isAlive() { return alive; }
    public boolean isInCollision() { return inCollision; }
    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    
    // Basic movement towards player
    protected void moveTowardsPlayer(Vector3f playerPosition, float deltaTime) {
        Vector3f direction = new Vector3f(playerPosition).sub(position);
        float distanceToPlayer = direction.length();
        
        if (distanceToPlayer > 1.0f) { // Stop when close to player (collision distance)
            direction.normalize().mul(speed * deltaTime);
            position.add(direction);
        }
    }
    
    // Keep enemy above ground
    protected void maintainHoverHeight(float hoverHeight) {
        if (position.y < hoverHeight) {
            position.y = hoverHeight;
        }
    }
}
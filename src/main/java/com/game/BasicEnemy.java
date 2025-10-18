package com.game;

import org.joml.Vector3f;

public class BasicEnemy extends Enemy {
    private float hoverOffset;
    
    public BasicEnemy(Vector3f startPosition) {
        super(startPosition, 1.0f, 2.0f, 0.8f); // Health set to 1
        this.color = new Vector3f(1.0f, 0.2f, 0.2f); // Red
        this.hoverOffset = 0.0f;
    }
    
    @Override
    public void update(float deltaTime, Vector3f playerPosition) {
        if (!alive) return;
        
        animationTime += deltaTime * 3.0f;
        hoverOffset = (float) Math.sin(animationTime) * 0.1f;
        
        // Move towards player
        moveTowardsPlayer(playerPosition, deltaTime);
        
        // Maintain hover height with bobbing
        maintainHoverHeight(1.5f + hoverOffset);
        position.y = 1.5f + hoverOffset;
    }
    
    @Override
    public Vector3f getColor() {
        // Pulse red color based on health
        float healthRatio = health / maxHealth;
        float pulse = 0.8f + 0.2f * (float) Math.sin(animationTime * 2.0f);
        return new Vector3f(1.0f * pulse, 0.2f * healthRatio, 0.2f * healthRatio);
    }
}
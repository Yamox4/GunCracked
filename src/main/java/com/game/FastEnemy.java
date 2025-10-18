package com.game;

import org.joml.Vector3f;

public class FastEnemy extends Enemy {
    private float dashCooldown;
    private float dashTimer;
    private boolean isDashing;
    
    public FastEnemy(Vector3f startPosition) {
        super(startPosition, 1.0f, 4.0f, 0.6f); // Health set to 1
        this.color = new Vector3f(1.0f, 1.0f, 0.2f); // Yellow
        this.dashCooldown = 3.0f;
        this.dashTimer = 0.0f;
        this.isDashing = false;
    }
    
    @Override
    public void update(float deltaTime, Vector3f playerPosition) {
        if (!alive) return;
        
        animationTime += deltaTime * 5.0f;
        dashTimer -= deltaTime;
        
        // Dash ability
        if (dashTimer <= 0 && !isDashing) {
            float distanceToPlayer = position.distance(playerPosition);
            if (distanceToPlayer > 3.0f && distanceToPlayer < 15.0f) {
                isDashing = true;
                dashTimer = 0.5f; // Dash duration
            } else {
                dashTimer = dashCooldown;
            }
        }
        
        // Movement
        float currentSpeed = isDashing ? speed * 3.0f : speed;
        Vector3f direction = new Vector3f(playerPosition).sub(position);
        if (direction.length() > 0.1f) {
            direction.normalize().mul(currentSpeed * deltaTime);
            position.add(direction);
        }
        
        if (dashTimer <= 0) {
            isDashing = false;
            dashTimer = dashCooldown;
        }
        
        // Maintain hover height
        float hoverHeight = 1.8f + (float) Math.sin(animationTime) * 0.15f;
        maintainHoverHeight(hoverHeight);
        position.y = hoverHeight;
    }
    
    @Override
    public Vector3f getColor() {
        float healthRatio = health / maxHealth;
        if (isDashing) {
            // Bright white-yellow when dashing
            float intensity = 0.9f + 0.1f * (float) Math.sin(animationTime * 10.0f);
            return new Vector3f(1.0f * intensity, 1.0f * intensity, 0.8f * intensity);
        } else {
            // Normal yellow
            float pulse = 0.7f + 0.3f * (float) Math.sin(animationTime * 1.5f);
            return new Vector3f(1.0f * pulse, 1.0f * pulse * healthRatio, 0.2f);
        }
    }
}
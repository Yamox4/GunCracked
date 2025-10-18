package com.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class BulletManager {
    private final List<Bullet> bullets;
    private float shootCooldown;
    private final float maxShootCooldown = 1.0f; // 0.5 seconds between shots (one at a time)
    
    public BulletManager() {
        bullets = new ArrayList<>();
        shootCooldown = 0.0f;
    }
    
    public void update(float deltaTime) {
        // Update cooldown
        if (shootCooldown > 0) {
            shootCooldown -= deltaTime;
        }
        
        // Update bullets
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update(deltaTime);
            if (!bullet.isActive()) {
                bullet.cleanup();
                iterator.remove();
            }
        }
    }
    
    public void shoot(Vector3f playerPos, Vector3f targetPos) {
        if (shootCooldown <= 0) {
            bullets.add(new Bullet(playerPos, targetPos));
            shootCooldown = maxShootCooldown;
        }
    }
    
    public boolean canShoot() {
        return shootCooldown <= 0;
    }
    
    public void render(Shader shader, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        for (Bullet bullet : bullets) {
            bullet.render(shader, viewMatrix, projectionMatrix);
        }
    }
    
    public List<Bullet> getBullets() {
        return bullets;
    }
    
    public void cleanup() {
        for (Bullet bullet : bullets) {
            bullet.cleanup();
        }
        bullets.clear();
    }
}
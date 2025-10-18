package com.game;

import java.util.List;

import org.joml.Vector3f;

public class CollisionSystem {
    
    // Check collision between two spheres (distance-based)
    public static boolean checkSphereCollision(Vector3f pos1, float radius1, Vector3f pos2, float radius2) {
        float distance = pos1.distance(pos2);
        return distance < (radius1 + radius2);
    }
    
    // Check collision between cube (treated as sphere) and sphere
    public static boolean checkCubeSphereCollision(Vector3f cubePos, float cubeSize, Vector3f spherePos, float sphereRadius) {
        // Treat cube as sphere with radius = half diagonal of cube
        float cubeRadius = cubeSize * 0.866f; // sqrt(3)/2 for cube diagonal
        return checkSphereCollision(cubePos, cubeRadius, spherePos, sphereRadius);
    }
    
    // Resolve collision by pushing objects apart
    public static void resolveSphereCollision(Vector3f pos1, float radius1, Vector3f pos2, float radius2, float pushStrength) {
        Vector3f direction = new Vector3f(pos2).sub(pos1);
        float distance = direction.length();
        
        if (distance < (radius1 + radius2) && distance > 0.001f) {
            // Normalize direction
            direction.normalize();
            
            // Calculate overlap
            float overlap = (radius1 + radius2) - distance;
            
            // Push objects apart (equal force on both)
            Vector3f pushVector = new Vector3f(direction).mul(overlap * pushStrength * 0.5f);
            
            pos1.sub(pushVector);
            pos2.add(pushVector);
        }
    }
    
    // Resolve collision with player (push enemy away, player takes damage)
    public static void resolvePlayerEnemyCollision(Vector3f playerPos, float playerRadius, 
                                                  Vector3f enemyPos, float enemyRadius) {
        Vector3f direction = new Vector3f(enemyPos).sub(playerPos);
        float distance = direction.length();
        
        if (distance < (playerRadius + enemyRadius) && distance > 0.001f) {
            // Normalize direction
            direction.normalize();
            
            // Calculate overlap
            float overlap = (playerRadius + enemyRadius) - distance;
            
            // Push enemy away from player (player stays in place)
            Vector3f pushVector = new Vector3f(direction).mul(overlap);
            enemyPos.add(pushVector);
        }
    }
    
    // Check if enemy is too close to other enemies and resolve
    public static void resolveEnemyCollisions(List<Enemy> enemies) {
        // Reset collision flags
        for (Enemy enemy : enemies) {
            enemy.setInCollision(false);
        }
        
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy1 = enemies.get(i);
            if (!enemy1.isAlive()) continue;
            
            for (int j = i + 1; j < enemies.size(); j++) {
                Enemy enemy2 = enemies.get(j);
                if (!enemy2.isAlive()) continue;
                
                Vector3f pos1 = enemy1.getPosition();
                Vector3f pos2 = enemy2.getPosition();
                float radius1 = enemy1.getSize() * 0.5f;
                float radius2 = enemy2.getSize() * 0.5f;
                
                if (checkSphereCollision(pos1, radius1, pos2, radius2)) {
                    // Set collision flags for visual feedback
                    enemy1.setInCollision(true);
                    enemy2.setInCollision(true);
                    
                    // Resolve collision by pushing enemies apart
                    resolveSphereCollision(pos1, radius1, pos2, radius2, 1.0f);
                    
                    // Update enemy positions
                    enemy1.setPosition(pos1);
                    enemy2.setPosition(pos2);
                }
            }
        }
    }
    
    // Check player collision with all enemies
    public static boolean checkPlayerEnemyCollisions(Vector3f playerPos, float playerSize, List<Enemy> enemies) {
        boolean hasCollision = false;
        float playerRadius = playerSize * 0.866f; // Cube treated as sphere
        
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            
            Vector3f enemyPos = enemy.getPosition();
            float enemyRadius = enemy.getSize() * 0.5f;
            
            if (checkCubeSphereCollision(playerPos, playerSize, enemyPos, enemyRadius)) {
                // Resolve collision (push enemy away)
                resolvePlayerEnemyCollision(playerPos, playerRadius, enemyPos, enemyRadius);
                enemy.setPosition(enemyPos);
                hasCollision = true;
            }
        }
        
        return hasCollision;
    }
}
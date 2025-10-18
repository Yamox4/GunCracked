package com.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class EnemyManager {
    private final List<Enemy> enemies;
    private final Random random;
    private final Sphere sphereRenderer;
    private float spawnTimer;
    private final float spawnInterval;
    private final float spawnDistance;
    private int waveNumber;
    
    public EnemyManager() {
        this.enemies = new ArrayList<>();
        this.random = new Random();
        this.sphereRenderer = new Sphere();
        this.spawnTimer = 0.0f;
        this.spawnInterval = 1.0f; // Spawn every 3 seconds
        this.spawnDistance = 20.0f; // Spawn 20 units away from player
        this.waveNumber = 1;
    }
    
    public void update(float deltaTime, Vector3f playerPosition) {
        // Update spawn timer
        spawnTimer += deltaTime;
        
        // Spawn new enemies
        if (spawnTimer >= spawnInterval) {
            spawnEnemy(playerPosition);
            spawnTimer = 0.0f;
        }
        
        // Update all enemies
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime, playerPosition);
        }
        
        // Handle enemy-to-enemy collisions
        CollisionSystem.resolveEnemyCollisions(enemies);
        
        // Remove dead enemies
        enemies.removeIf(enemy -> !enemy.isAlive());
        
        // Increase difficulty over time
        if (enemies.isEmpty() && spawnTimer > spawnInterval / 2) {
            waveNumber++;
            spawnWave(playerPosition);
        }
    }
    
    private void spawnEnemy(Vector3f playerPosition) {
        Vector3f spawnPos = getRandomSpawnPosition(playerPosition);
        
        // Choose enemy type based on wave number and randomness
        float typeChance = random.nextFloat();
        Enemy newEnemy;
        
        if (waveNumber > 3 && typeChance < 0.3f) {
            newEnemy = new FastEnemy(spawnPos);
        } else {
            newEnemy = new BasicEnemy(spawnPos);
        }
        
        enemies.add(newEnemy);
    }
    
    private void spawnWave(Vector3f playerPosition) {
        int enemyCount = Math.min(2 + waveNumber / 2, 8); // Max 8 enemies
        
        for (int i = 0; i < enemyCount; i++) {
            Vector3f spawnPos = getRandomSpawnPosition(playerPosition);
            
            Enemy newEnemy;
            if (waveNumber > 2 && i % 3 == 0) {
                newEnemy = new FastEnemy(spawnPos);
            } else {
                newEnemy = new BasicEnemy(spawnPos);
            }
            
            enemies.add(newEnemy);
        }
    }
    
    private Vector3f getRandomSpawnPosition(Vector3f playerPosition) {
        float angle = random.nextFloat() * 2.0f * (float) Math.PI;
        float distance = spawnDistance + random.nextFloat() * 10.0f;
        
        float x = playerPosition.x + (float) Math.cos(angle) * distance;
        float z = playerPosition.z + (float) Math.sin(angle) * distance;
        float y = 1.5f + random.nextFloat() * 2.0f;
        
        return new Vector3f(x, y, z);
    }
    
    public void render(Shader shader, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        for (Enemy enemy : enemies) {
            renderEnemy(enemy, shader, viewMatrix, projectionMatrix);
        }
    }
    
    private void renderEnemy(Enemy enemy, Shader shader, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        Vector3f pos = enemy.getPosition();
        float size = enemy.getSize();
        Vector3f color = enemy.getColor();
        
        // Render enemy sphere
        Matrix4f modelMatrix = new Matrix4f().identity().translate(pos).scale(size);
        Matrix4f mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);
        
        shader.setUniform("mvpMatrix", mvpMatrix);
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("color", color);
        shader.setUniform("isWireframe", false);
        
        sphereRenderer.render();
        
        // Render wireframe overlay
        shader.setUniform("color", new Vector3f(1.0f, 1.0f, 1.0f));
        shader.setUniform("isWireframe", true);
        sphereRenderer.render();
    }
    
    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies);
    }
    
    // Check and resolve collisions with player
    public boolean checkPlayerCollisions(Vector3f playerPosition, float playerSize) {
        return CollisionSystem.checkPlayerEnemyCollisions(playerPosition, playerSize, enemies);
    }
    
    public void cleanup() {
        sphereRenderer.cleanup();
    }
}
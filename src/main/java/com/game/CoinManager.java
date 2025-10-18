package com.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class CoinManager {
    private final List<Coin> coins;
    private int vaoId;
    private int vboId;
    private int eboId;
    private int coinsCollected;
    
    public CoinManager() {
        this.coins = new ArrayList<>();
        this.coinsCollected = 0;
        createCoinMesh();
    }
    
    private void createCoinMesh() {
        // Create a cylinder-like coin shape (octagon for performance)
        int segments = 8;
        float radius = 0.225f; // Match coin size (50% smaller)
        float thickness = 0.1f;
        
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        
        // Top face vertices
        for (int i = 0; i < segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            
            // Position + Normal
            vertices.add(x); vertices.add(thickness / 2); vertices.add(z);
            vertices.add(0.0f); vertices.add(1.0f); vertices.add(0.0f); // Up normal
        }
        
        // Bottom face vertices
        for (int i = 0; i < segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            
            // Position + Normal
            vertices.add(x); vertices.add(-thickness / 2); vertices.add(z);
            vertices.add(0.0f); vertices.add(-1.0f); vertices.add(0.0f); // Down normal
        }
        
        // Center vertices for top and bottom
        vertices.add(0.0f); vertices.add(thickness / 2); vertices.add(0.0f);
        vertices.add(0.0f); vertices.add(1.0f); vertices.add(0.0f); // Up normal
        
        vertices.add(0.0f); vertices.add(-thickness / 2); vertices.add(0.0f);
        vertices.add(0.0f); vertices.add(-1.0f); vertices.add(0.0f); // Down normal
        
        // Top face indices
        int topCenter = segments * 2;
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            indices.add(topCenter);
            indices.add(i);
            indices.add(next);
        }
        
        // Bottom face indices
        int bottomCenter = segments * 2 + 1;
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            indices.add(bottomCenter);
            indices.add(segments + next);
            indices.add(segments + i);
        }
        
        // Side faces
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            
            // First triangle
            indices.add(i);
            indices.add(segments + i);
            indices.add(next);
            
            // Second triangle
            indices.add(next);
            indices.add(segments + i);
            indices.add(segments + next);
        }
        
        // Convert to arrays
        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }
        
        int[] indexArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indexArray[i] = indices.get(i);
        }
        
        // Create OpenGL buffers
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        
        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexArray, GL15.GL_STATIC_DRAW);
        
        eboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexArray, GL15.GL_STATIC_DRAW);
        
        // Position attribute
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);
        
        // Normal attribute
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);
        
        GL30.glBindVertexArray(0);
    }
    
    public void spawnCoin(Vector3f position) {
        Coin newCoin = new Coin(position);
        coins.add(newCoin);
        System.out.println("💰 Coin spawned at: " + position + " | Total active coins: " + coins.size());
    }
    
    public void update(float deltaTime, Vector3f playerPosition, float playerSize, LevelSystem levelSystem) {
        Iterator<Coin> iterator = coins.iterator();
        while (iterator.hasNext()) {
            Coin coin = iterator.next();
            
            // Update coin physics and magnet
            coin.update(deltaTime, playerPosition);
            
            // Check collision-based collection
            if (coin.checkCollision(playerPosition, playerSize) && !coin.isCollected() && !coin.isCollecting()) {
                coin.collect(); // Start collection animation
                coinsCollected++;
                
                // Add 1 EXP per coin to level system
                boolean leveledUp = levelSystem.addExp(1);
                
                System.out.println("✨ COIN COLLECTED! ✨ +1 EXP | Total coins: " + coinsCollected + 
                                 " | " + levelSystem.getDebugInfo());
                
                if (leveledUp) {
                    System.out.println("🎉 LEVEL UP! 🎉 Reached level " + levelSystem.getCurrentLevel());
                }
            }
            
            // Remove coins that are fully collected or expired
            if (!coin.isActive()) {
                iterator.remove();
            }
        }
    }
    
    // Backward compatibility method
    public void update(float deltaTime, Vector3f playerPosition, float playerSize) {
        // This method is kept for compatibility but should not be used with level system
        update(deltaTime, playerPosition, playerSize, null);
    }
    
    public void render(Shader shader, Matrix4f viewMatrix, Matrix4f projectionMatrix, Vector3f playerPosition, float playerSize) {
        if (coins.isEmpty()) return;
        
        GL30.glBindVertexArray(vaoId);
        
        for (Coin coin : coins) {
            if (!coin.isActive()) continue;
            
            Vector3f pos = coin.getPosition();
            float rotation = coin.getRotationY();
            float size = coin.getSize();
            float lifeRatio = coin.getLifeRatio();
            
            // Collection animation effects
            float collectionProgress = coin.getCollectionProgress();
            float animationScale = coin.isCollecting() ? (1.0f - collectionProgress * 0.5f) : 1.0f;
            float animationBrightness = coin.isCollecting() ? (1.0f + collectionProgress * 2.0f) : 1.0f;
            
            // Near-collection visual feedback
            boolean nearCollection = coin.isNearCollection(playerPosition, playerSize);
            float proximityBrightness = nearCollection ? 1.3f : 1.0f;
            float proximityScale = nearCollection ? 1.1f : 1.0f;
            
            // Enable additive blending for glow effect
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            
            // Render multiple glow layers for BRIGHT GOLD glowing effect
            
            // Layer 1: Outer glow (largest, most transparent)
            Matrix4f outerGlowMatrix = new Matrix4f().identity()
                .translate(pos)
                .rotateY((float) Math.toRadians(rotation))
                .scale(size * 4.0f * animationScale * proximityScale);
            Matrix4f outerMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(outerGlowMatrix);
            
            Vector3f outerGlowColor = new Vector3f(1.0f, 0.8f, 0.0f).mul(1.2f * lifeRatio * animationBrightness * proximityBrightness); // Bright gold outer glow
            shader.setUniform("mvpMatrix", outerMvp);
            shader.setUniform("modelMatrix", outerGlowMatrix);
            shader.setUniform("color", outerGlowColor);
            shader.setUniform("isWireframe", false);
            GL11.glDrawElements(GL11.GL_TRIANGLES, 72, GL11.GL_UNSIGNED_INT, 0);
            
            // Layer 2: Middle glow
            Matrix4f middleGlowMatrix = new Matrix4f().identity()
                .translate(pos)
                .rotateY((float) Math.toRadians(rotation))
                .scale(size * 3.0f * animationScale * proximityScale);
            Matrix4f middleMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(middleGlowMatrix);
            
            Vector3f middleGlowColor = new Vector3f(1.0f, 0.9f, 0.0f).mul(1.5f * lifeRatio * animationBrightness * proximityBrightness); // Bright yellow middle
            shader.setUniform("mvpMatrix", middleMvp);
            shader.setUniform("modelMatrix", middleGlowMatrix);
            shader.setUniform("color", middleGlowColor);
            shader.setUniform("isWireframe", false);
            GL11.glDrawElements(GL11.GL_TRIANGLES, 72, GL11.GL_UNSIGNED_INT, 0);
            
            // Layer 3: Core coin (bright gold)
            Matrix4f coreMatrix = new Matrix4f().identity()
                .translate(pos)
                .rotateY((float) Math.toRadians(rotation))
                .scale(size * 2.0f * animationScale * proximityScale);
            Matrix4f coreMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(coreMatrix);
            
            Vector3f coreColor = new Vector3f(1.0f, 0.85f, 0.0f).mul(2.0f * lifeRatio * animationBrightness * proximityBrightness); // Bright gold core
            shader.setUniform("mvpMatrix", coreMvp);
            shader.setUniform("modelMatrix", coreMatrix);
            shader.setUniform("color", coreColor);
            shader.setUniform("isWireframe", false);
            GL11.glDrawElements(GL11.GL_TRIANGLES, 72, GL11.GL_UNSIGNED_INT, 0);
            
            // Layer 4: Bright inner core
            Matrix4f innerCoreMatrix = new Matrix4f().identity()
                .translate(pos)
                .rotateY((float) Math.toRadians(rotation))
                .scale(size * 1.2f * animationScale * proximityScale);
            Matrix4f innerMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(innerCoreMatrix);
            
            Vector3f innerCoreColor = new Vector3f(1.0f, 1.0f, 0.2f).mul(3.0f * lifeRatio * animationBrightness * proximityBrightness); // Very bright yellow center
            shader.setUniform("mvpMatrix", innerMvp);
            shader.setUniform("modelMatrix", innerCoreMatrix);
            shader.setUniform("color", innerCoreColor);
            shader.setUniform("isWireframe", false);
            GL11.glDrawElements(GL11.GL_TRIANGLES, 72, GL11.GL_UNSIGNED_INT, 0);
            
            // Reset blending
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
        
        GL30.glBindVertexArray(0);
    }
    
    // Backward compatibility render method
    public void render(Shader shader, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        render(shader, viewMatrix, projectionMatrix, new Vector3f(0, 0, 0), 1.0f);
    }
    
    public int getCoinsCollected() {
        return coinsCollected;
    }
    
    public int getActiveCoins() {
        return coins.size();
    }
    
    // Method to spend coins (for level-up system)
    public boolean spendCoins(int amount) {
        if (coinsCollected >= amount) {
            coinsCollected -= amount;
            System.out.println("Spent " + amount + " coins. Remaining: " + coinsCollected);
            return true;
        }
        return false;
    }
    
    // Method to add coins directly (for testing or rewards)
    public void addCoins(int amount) {
        coinsCollected += amount;
        System.out.println("Added " + amount + " coins. Total: " + coinsCollected);
    }
    
    // Debug method to spawn a test coin near player
    public void spawnTestCoin(Vector3f playerPosition) {
        Vector3f testPos = new Vector3f(playerPosition).add(2.0f, 1.0f, 0.0f);
        spawnCoin(testPos);
        System.out.println("🧪 Test coin spawned near player");
    }
    
    // Debug method to get collision info
    public void debugCollisionInfo(Vector3f playerPosition, float playerSize) {
        if (!coins.isEmpty()) {
            Coin closestCoin = coins.get(0);
            float closestDistance = Float.MAX_VALUE;
            
            for (Coin coin : coins) {
                float distance = coin.getDistanceToPlayer(playerPosition);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestCoin = coin;
                }
            }
            
            boolean wouldCollide = closestCoin.checkCollision(playerPosition, playerSize);
            System.out.println("Closest coin distance: " + String.format("%.2f", closestDistance) + 
                             ", Would collide: " + wouldCollide + 
                             ", Player size: " + playerSize + 
                             ", Coin size: " + closestCoin.getSize());
        }
    }
    
    public void cleanup() {
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(eboId);
        GL30.glDeleteVertexArrays(vaoId);
    }
}
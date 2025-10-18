package com.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ExplosionSystem {
    
    private final List<Explosion> explosions;
    private final Sphere sphereRenderer;
    private final Random random;
    
    private static class Explosion {
        Vector3f position;
        float time;
        float maxTime;
        List<ExplosionParticle> particles;
        
        Explosion(Vector3f pos) {
            position = new Vector3f(pos);
            time = 0.0f;
            maxTime = 1.5f; // 1.5 second explosion
            particles = new ArrayList<>();
            
            // Create explosion particles
            Random rand = new Random();
            int particleCount = 15 + rand.nextInt(10); // 15-25 particles
            
            for (int i = 0; i < particleCount; i++) {
                Vector3f particlePos = new Vector3f(pos);
                
                // Random direction for explosion
                Vector3f velocity = new Vector3f(
                    (rand.nextFloat() - 0.5f) * 15.0f,
                    (rand.nextFloat() - 0.5f) * 15.0f + 5.0f, // Slight upward bias
                    (rand.nextFloat() - 0.5f) * 15.0f
                );
                
                float size = 0.1f + rand.nextFloat() * 0.3f;
                float lifetime = 0.8f + rand.nextFloat() * 0.7f;
                
                particles.add(new ExplosionParticle(particlePos, velocity, size, lifetime));
            }
        }
        
        void update(float deltaTime) {
            time += deltaTime;
            
            // Update all particles
            Iterator<ExplosionParticle> iterator = particles.iterator();
            while (iterator.hasNext()) {
                ExplosionParticle particle = iterator.next();
                particle.update(deltaTime);
                if (!particle.isAlive()) {
                    iterator.remove();
                }
            }
        }
        
        boolean isAlive() {
            return time < maxTime && !particles.isEmpty();
        }
    }
    
    private static class ExplosionParticle {
        Vector3f position;
        Vector3f velocity;
        float size;
        float life;
        float maxLife;
        Vector3f color;
        
        ExplosionParticle(Vector3f pos, Vector3f vel, float particleSize, float lifetime) {
            position = new Vector3f(pos);
            velocity = new Vector3f(vel);
            size = particleSize;
            life = lifetime;
            maxLife = lifetime;
            
            // Pure white explosion colors
            Random rand = new Random();
            float brightness = 0.8f + rand.nextFloat() * 0.2f; // 0.8 to 1.0 brightness
            color = new Vector3f(brightness, brightness, brightness); // Pure white
        }
        
        void update(float deltaTime) {
            // Move particle
            position.add(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);
            
            // Apply gravity and air resistance
            velocity.y -= 8.0f * deltaTime; // Gravity
            velocity.mul(0.95f); // Air resistance
            
            // Decrease life
            life -= deltaTime;
        }
        
        boolean isAlive() {
            return life > 0;
        }
        
        float getLifeRatio() {
            return life / maxLife;
        }
    }
    
    public ExplosionSystem() {
        explosions = new ArrayList<>();
        sphereRenderer = new Sphere();
        random = new Random();
    }
    
    public void createExplosion(Vector3f position) {
        explosions.add(new Explosion(position));
    }
    
    public void update(float deltaTime) {
        Iterator<Explosion> iterator = explosions.iterator();
        while (iterator.hasNext()) {
            Explosion explosion = iterator.next();
            explosion.update(deltaTime);
            if (!explosion.isAlive()) {
                iterator.remove();
            }
        }
    }
    
    public void render(Shader shader, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        for (Explosion explosion : explosions) {
            renderExplosion(explosion, shader, viewMatrix, projectionMatrix);
        }
    }
    
    private void renderExplosion(Explosion explosion, Shader shader, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        for (ExplosionParticle particle : explosion.particles) {
            float lifeRatio = particle.getLifeRatio();
            
            // Render particle with multiple layers for glow effect
            
            // Outer glow
            Matrix4f outerMatrix = new Matrix4f().identity()
                .translate(particle.position)
                .scale(particle.size * 2.0f * lifeRatio);
            Matrix4f outerMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(outerMatrix);
            
            shader.setUniform("mvpMatrix", outerMvp);
            shader.setUniform("modelMatrix", outerMatrix);
            shader.setUniform("color", new Vector3f(particle.color).mul(0.3f * lifeRatio));
            shader.setUniform("isWireframe", false);
            sphereRenderer.render();
            
            // Core particle
            Matrix4f coreMatrix = new Matrix4f().identity()
                .translate(particle.position)
                .scale(particle.size * lifeRatio);
            Matrix4f coreMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(coreMatrix);
            
            shader.setUniform("mvpMatrix", coreMvp);
            shader.setUniform("modelMatrix", coreMatrix);
            shader.setUniform("color", new Vector3f(particle.color).mul(lifeRatio));
            shader.setUniform("isWireframe", false);
            sphereRenderer.render();
        }
    }
    
    public void cleanup() {
        sphereRenderer.cleanup();
    }
}
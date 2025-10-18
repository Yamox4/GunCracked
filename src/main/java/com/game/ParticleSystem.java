package com.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ParticleSystem {

    private List<Particle> particles;
    private Random random;
    private int vaoId;
    private int vboId;
    private int eboId;
    private final int maxParticles = 200;

    private static class Particle {

        Vector3f position;
        Vector3f velocity;
        Vector3f color;
        float life;
        float maxLife;
        float size;

        Particle(Vector3f pos, Vector3f vel, Vector3f col, float lifetime, float particleSize) {
            position = new Vector3f(pos);
            velocity = new Vector3f(vel);
            color = new Vector3f(col);
            life = lifetime;
            maxLife = lifetime;
            size = particleSize;
        }

        void update(float deltaTime) {
            // Update position
            position.add(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);

            // Update life
            life -= deltaTime;

            // Fade out over time
            float alpha = life / maxLife;
            color.mul(alpha);

            // Gravity effect for some particles
            velocity.y -= 2.0f * deltaTime;

            // Damping
            velocity.mul(0.98f);
        }

        boolean isAlive() {
            return life > 0;
        }
    }

    public ParticleSystem() {
        particles = new ArrayList<>();
        random = new Random();
        createBuffers();
    }

    private void createBuffers() {
        // Create a tiny cube for particle rendering (more visible than points)
        float[] vertices = {
            // Front face
            -0.02f, -0.02f,  0.02f,  0.0f,  0.0f,  1.0f,
             0.02f, -0.02f,  0.02f,  0.0f,  0.0f,  1.0f,
             0.02f,  0.02f,  0.02f,  0.0f,  0.0f,  1.0f,
            -0.02f,  0.02f,  0.02f,  0.0f,  0.0f,  1.0f,
            // Back face
            -0.02f, -0.02f, -0.02f,  0.0f,  0.0f, -1.0f,
             0.02f, -0.02f, -0.02f,  0.0f,  0.0f, -1.0f,
             0.02f,  0.02f, -0.02f,  0.0f,  0.0f, -1.0f,
            -0.02f,  0.02f, -0.02f,  0.0f,  0.0f, -1.0f
        };
        
        int[] indices = {
            // Front face
            0, 1, 2, 2, 3, 0,
            // Back face
            4, 5, 6, 6, 7, 4,
            // Left face
            7, 3, 0, 0, 4, 7,
            // Right face
            1, 5, 6, 6, 2, 1,
            // Top face
            3, 2, 6, 6, 7, 3,
            // Bottom face
            0, 1, 5, 5, 4, 0
        };

        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
        
        eboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        // Position attribute
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);
        
        // Normal attribute
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

    public void emitLightningParticles(Vector3f cubePosition, boolean isMoving, boolean isJumping) {
        if (particles.size() >= maxParticles) {
            return;
        }

        int particleCount = 0;
        if (isMoving) {
            particleCount += 8;
        }
        if (isJumping) {
            particleCount += 12;
        }

        for (int i = 0; i < particleCount; i++) {
            // Random position around cube
            Vector3f pos = new Vector3f(
                    cubePosition.x + (random.nextFloat() - 0.5f) * 2.0f,
                    cubePosition.y + (random.nextFloat() - 0.5f) * 2.0f,
                    cubePosition.z + (random.nextFloat() - 0.5f) * 2.0f
            );

            // Random velocity for lightning effect
            Vector3f vel = new Vector3f(
                    (random.nextFloat() - 0.5f) * 8.0f,
                    (random.nextFloat() - 0.5f) * 8.0f + 2.0f,
                    (random.nextFloat() - 0.5f) * 8.0f
            );

            // Purple lightning colors
            Vector3f color;
            if (isJumping) {
                // Bright electric purple-white for jumping
                color = new Vector3f(
                        0.8f + random.nextFloat() * 0.2f,
                        0.4f + random.nextFloat() * 0.3f,
                        1.0f
                );
            } else {
                // Deep purple for movement
                color = new Vector3f(
                        0.6f + random.nextFloat() * 0.2f,
                        0.2f + random.nextFloat() * 0.2f,
                        0.8f + random.nextFloat() * 0.2f
                );
            }

            float lifetime = 0.5f + random.nextFloat() * 0.8f;
            float size = 0.05f + random.nextFloat() * 0.1f;

            particles.add(new Particle(pos, vel, color, lifetime, size));
        }
    }

    public void update(float deltaTime) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update(deltaTime);
            if (!particle.isAlive()) {
                iterator.remove();
            }
        }
    }

    public void render(Shader shader, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        if (particles.isEmpty()) {
            return;
        }

        GL30.glBindVertexArray(vaoId);

        for (Particle particle : particles) {
            // Create model matrix for particle position and size
            Matrix4f modelMatrix = new Matrix4f().identity()
                .translate(particle.position)
                .scale(particle.size * 10.0f); // Scale up the tiny cube based on particle size
            Matrix4f mvpMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(modelMatrix);

            shader.setUniform("mvpMatrix", mvpMatrix);
            shader.setUniform("modelMatrix", modelMatrix);
            shader.setUniform("color", particle.color);
            shader.setUniform("isWireframe", false);

            GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0);
        }

        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(eboId);
        GL30.glDeleteVertexArrays(vaoId);
    }
}

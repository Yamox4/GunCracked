package com.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Bullet {

    private final Vector3f position;
    private final Vector3f direction;
    private final float speed;
    private float lifetime;
    private boolean active;

    // Sphere renderer for bullet
    private final Sphere sphereRenderer;

    // Trail system
    private final java.util.List<Vector3f> trailPositions;
    private final int maxTrailLength = 10;

    public Bullet(Vector3f startPos, Vector3f targetPos) {
        this.position = new Vector3f(startPos);
        this.direction = new Vector3f(targetPos).sub(startPos).normalize();
        this.speed = 10.0f; // Very fast bullet
        this.lifetime = 3.0f; // 3 second max lifetime
        this.active = true;
        this.trailPositions = new java.util.ArrayList<>();
        this.sphereRenderer = new Sphere();

    }

    public void update(float deltaTime) {
        if (!active) {
            return;
        }

        // Add current position to trail
        trailPositions.add(new Vector3f(position));

        // Limit trail length
        if (trailPositions.size() > maxTrailLength) {
            trailPositions.remove(0);
        }

        // Move bullet
        position.add(direction.x * speed * deltaTime,
                direction.y * speed * deltaTime,
                direction.z * speed * deltaTime);

        // Update lifetime
        lifetime -= deltaTime;
        if (lifetime <= 0) {
            active = false;
        }
    }

    public void render(Shader shader, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        if (!active) {
            return;
        }

        // Render white trail first (behind bullet) - using small spheres
        for (int i = 0; i < trailPositions.size(); i++) {
            Vector3f trailPos = trailPositions.get(i);
            float trailAlpha = (float) i / trailPositions.size(); // Fade from 0 to 1
            float trailSize = 0.03f + (trailAlpha * 0.05f); // Small trail spheres

            Matrix4f trailMatrix = new Matrix4f().identity()
                    .translate(trailPos)
                    .scale(trailSize);
            Matrix4f trailMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(trailMatrix);

            shader.setUniform("mvpMatrix", trailMvp);
            shader.setUniform("modelMatrix", trailMatrix);
            shader.setUniform("color", new Vector3f(1.0f, 1.0f, 1.0f).mul(trailAlpha * 0.8f)); // White trail
            shader.setUniform("isWireframe", false);

            sphereRenderer.render();
        }

        // Render blue glowing bullet sphere with multiple layers
        // Layer 1: Outer blue glow (largest sphere)
        Matrix4f glowMatrix1 = new Matrix4f().identity()
                .translate(position)
                .scale(1.0f); // Much larger outer glow sphere
        Matrix4f glowMvp1 = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(glowMatrix1);

        shader.setUniform("mvpMatrix", glowMvp1);
        shader.setUniform("modelMatrix", glowMatrix1);
        shader.setUniform("color", new Vector3f(0.2f, 0.4f, 1.0f).mul(1.0f)); // Bright blue glow
        shader.setUniform("isWireframe", false);
        sphereRenderer.render();

        // Layer 2: Middle blue glow sphere
        Matrix4f glowMatrix2 = new Matrix4f().identity()
                .translate(position)
                .scale(0.7f); // Much larger medium glow sphere
        Matrix4f glowMvp2 = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(glowMatrix2);

        shader.setUniform("mvpMatrix", glowMvp2);
        shader.setUniform("modelMatrix", glowMatrix2);
        shader.setUniform("color", new Vector3f(0.3f, 0.6f, 1.0f).mul(1.2f)); // Very bright blue
        shader.setUniform("isWireframe", false);
        sphereRenderer.render();

        // Layer 3: Core bullet sphere (brightest blue)
        Matrix4f coreMatrix = new Matrix4f().identity()
                .translate(position)
                .scale(0.5f); // Much larger core sphere size
        Matrix4f coreMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(coreMatrix);

        shader.setUniform("mvpMatrix", coreMvp);
        shader.setUniform("modelMatrix", coreMatrix);
        shader.setUniform("color", new Vector3f(0.5f, 0.8f, 1.0f).mul(1.5f)); // Super bright blue core
        shader.setUniform("isWireframe", false);
        sphereRenderer.render();
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public boolean isActive() {
        return active;
    }

    public void setInactive() {
        active = false;
    }

    public void cleanup() {
        sphereRenderer.cleanup();
    }
}

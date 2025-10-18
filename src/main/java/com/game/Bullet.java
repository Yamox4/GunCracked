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
    private final int maxTrailLength = 25; // Longer trail

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

        // Render enhanced trail with multiple layers
        for (int i = 0; i < trailPositions.size(); i++) {
            Vector3f trailPos = trailPositions.get(i);
            float trailAlpha = (float) i / trailPositions.size(); // Fade from 0 to 1
            float baseSize = 0.02f + (trailAlpha * 0.08f); // Larger trail spheres

            // Layer 1: Outer trail glow (blue-white)
            Matrix4f trailGlowMatrix = new Matrix4f().identity()
                    .translate(trailPos)
                    .scale(baseSize * 2.5f);
            Matrix4f trailGlowMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(trailGlowMatrix);

            shader.setUniform("mvpMatrix", trailGlowMvp);
            shader.setUniform("modelMatrix", trailGlowMatrix);
            shader.setUniform("color", new Vector3f(0.4f, 0.7f, 1.0f).mul(trailAlpha * 0.3f)); // Blue glow
            shader.setUniform("isWireframe", false);
            sphereRenderer.render();

            // Layer 2: Middle trail (bright white-blue)
            Matrix4f trailMiddleMatrix = new Matrix4f().identity()
                    .translate(trailPos)
                    .scale(baseSize * 1.8f);
            Matrix4f trailMiddleMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(trailMiddleMatrix);

            shader.setUniform("mvpMatrix", trailMiddleMvp);
            shader.setUniform("modelMatrix", trailMiddleMatrix);
            shader.setUniform("color", new Vector3f(0.8f, 0.9f, 1.0f).mul(trailAlpha * 0.6f)); // Bright white-blue
            shader.setUniform("isWireframe", false);
            sphereRenderer.render();

            // Layer 3: Core trail (pure white)
            Matrix4f trailCoreMatrix = new Matrix4f().identity()
                    .translate(trailPos)
                    .scale(baseSize);
            Matrix4f trailCoreMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(trailCoreMatrix);

            shader.setUniform("mvpMatrix", trailCoreMvp);
            shader.setUniform("modelMatrix", trailCoreMatrix);
            shader.setUniform("color", new Vector3f(1.0f, 1.0f, 1.0f).mul(trailAlpha * 0.9f)); // Pure white core
            shader.setUniform("isWireframe", false);
            sphereRenderer.render();
        }

        // Render enhanced bullet with dramatic light effects
        float pulseTime = System.nanoTime() / 1_000_000_000.0f; // Time for pulsing effect
        float pulse = 0.8f + 0.2f * (float) Math.sin(pulseTime * 15.0f); // Fast pulse

        // Layer 1: Outer energy field (largest, pulsing)
        Matrix4f energyMatrix = new Matrix4f().identity()
                .translate(position)
                .scale(0.4f * pulse); // Pulsing outer energy field
        Matrix4f energyMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(energyMatrix);

        shader.setUniform("mvpMatrix", energyMvp);
        shader.setUniform("modelMatrix", energyMatrix);
        shader.setUniform("color", new Vector3f(0.1f, 0.3f, 1.0f).mul(0.4f * pulse)); // Soft blue energy
        shader.setUniform("isWireframe", false);
        sphereRenderer.render();

        // Layer 2: Outer blue glow (large sphere)
        Matrix4f glowMatrix1 = new Matrix4f().identity()
                .translate(position)
                .scale(0.25f); // Larger outer glow sphere
        Matrix4f glowMvp1 = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(glowMatrix1);

        shader.setUniform("mvpMatrix", glowMvp1);
        shader.setUniform("modelMatrix", glowMatrix1);
        shader.setUniform("color", new Vector3f(0.2f, 0.5f, 1.0f).mul(0.8f)); // Bright blue glow
        shader.setUniform("isWireframe", false);
        sphereRenderer.render();

        // Layer 3: Middle blue glow sphere
        Matrix4f glowMatrix2 = new Matrix4f().identity()
                .translate(position)
                .scale(0.18f); // Medium glow sphere
        Matrix4f glowMvp2 = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(glowMatrix2);

        shader.setUniform("mvpMatrix", glowMvp2);
        shader.setUniform("modelMatrix", glowMatrix2);
        shader.setUniform("color", new Vector3f(0.4f, 0.7f, 1.0f).mul(1.0f)); // Very bright blue
        shader.setUniform("isWireframe", false);
        sphereRenderer.render();

        // Layer 4: Inner bright core
        Matrix4f innerMatrix = new Matrix4f().identity()
                .translate(position)
                .scale(0.12f); // Inner core
        Matrix4f innerMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(innerMatrix);

        shader.setUniform("mvpMatrix", innerMvp);
        shader.setUniform("modelMatrix", innerMatrix);
        shader.setUniform("color", new Vector3f(0.6f, 0.9f, 1.0f).mul(1.3f)); // Brilliant blue-white
        shader.setUniform("isWireframe", false);
        sphereRenderer.render();

        // Layer 5: Core bullet sphere (brightest, pulsing)
        Matrix4f coreMatrix = new Matrix4f().identity()
                .translate(position)
                .scale(0.08f * pulse); // Pulsing core
        Matrix4f coreMvp = new Matrix4f(projectionMatrix).mul(viewMatrix).mul(coreMatrix);

        shader.setUniform("mvpMatrix", coreMvp);
        shader.setUniform("modelMatrix", coreMatrix);
        shader.setUniform("color", new Vector3f(0.9f, 0.95f, 1.0f).mul(1.8f * pulse)); // Super bright pulsing core
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

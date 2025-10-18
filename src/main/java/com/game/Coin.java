package com.game;

import org.joml.Vector3f;

public class Coin {

    private Vector3f position;
    private Vector3f velocity;
    private float rotationY;
    private float rotationSpeed;
    private float life;
    private final float maxLife;
    private boolean collected;
    private float bobOffset;
    private float bobTime;
    private final float size;
    private boolean collecting; // Collection animation state
    private float collectionTime; // Collection animation timer
    private final float collectionDuration = 0.3f; // How long collection animation lasts

    public Coin(Vector3f startPosition) {
        this.position = new Vector3f(startPosition);
        this.position.y = 1.0f; // Set to hover height immediately
        this.velocity = new Vector3f(0, 0, 0); // No initial velocity - just float
        this.rotationY = 0.0f;
        this.rotationSpeed = 180.0f + (float) Math.random() * 180.0f; // Random spin speed
        this.life = 30.0f; // Coins last longer
        this.maxLife = 30.0f;
        this.collected = false;
        this.bobOffset = 0.0f;
        this.bobTime = 0.0f;
        this.collecting = false;
        this.collectionTime = 0.0f;
        this.size = 0.225f; // 50% smaller coin size
    }

    public void update(float deltaTime, Vector3f playerPosition) {
        if (collected) {
            return;
        }

        // Handle collection animation
        if (collecting) {
            collectionTime += deltaTime;

            // Smooth collection animation - move towards player
            Vector3f toPlayer = new Vector3f(playerPosition).sub(position);
            float distanceToPlayer = toPlayer.length();

            if (distanceToPlayer > 0.1f) {
                toPlayer.normalize();
                float collectionSpeed = 30.0f; // Fast collection speed
                position.add(toPlayer.x * collectionSpeed * deltaTime,
                        toPlayer.y * collectionSpeed * deltaTime,
                        toPlayer.z * collectionSpeed * deltaTime);
            }

            // Finish collection when close to player
            if (distanceToPlayer < 0.3f) {
                collected = true;
                return;
            }

            // Continue with rotation during collection
            rotationY += rotationSpeed * deltaTime * 4.0f;
            if (rotationY > 360.0f) {
                rotationY -= 360.0f;
            }

            return; // Skip normal physics during collection
        }

        // Calculate distance to player
        float distance = position.distance(playerPosition);

        // Magnet effect - pull towards player when close
        float magnetRadius = 3.0f; // Magnet activation distance
        boolean inMagnetRange = distance < magnetRadius && distance > 0.3f;

        if (inMagnetRange && !collecting) {
            // Calculate direction to player
            Vector3f toPlayer = new Vector3f(playerPosition).sub(position);
            if (toPlayer.length() > 0) {
                toPlayer.normalize();

                // Strong magnet force
                float magnetStrength = 20.0f;
                float falloff = 1.0f - (distance / magnetRadius);
                float magnetForce = magnetStrength * falloff * falloff;

                // Apply magnet force
                velocity.add(toPlayer.x * magnetForce * deltaTime,
                        toPlayer.y * magnetForce * deltaTime * 0.5f, // Less Y pull
                        toPlayer.z * magnetForce * deltaTime);

                // Magnet is working
            }
        }

        // Simple floating behavior - NO GRAVITY, just floating and bobbing
        float hoverHeight = 1.0f; // Target hover height

        if (!inMagnetRange) {
            // Just float and bob - no physics
            bobTime += deltaTime * 2.0f;
            bobOffset = (float) Math.sin(bobTime) * 0.15f;
            position.y = hoverHeight + bobOffset;
            
            // Dampen any horizontal velocity
            velocity.x *= 0.95f;
            velocity.z *= 0.95f;
        } else {
            // When magnetized, apply velocity
            position.add(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);
        }

        // Update rotation
        float rotationMultiplier = inMagnetRange ? 3.0f : 1.0f;
        rotationY += rotationSpeed * deltaTime * rotationMultiplier;
        if (rotationY > 360.0f) {
            rotationY -= 360.0f;
        }

        // Update life
        life -= deltaTime;
    }

    public boolean checkCollision(Vector3f playerPosition, float playerSize) {
        if (collected || collecting) {
            return false;
        }

        // Simple and reliable collision detection
        float collectionRadius = 1.0f; // Fixed collection radius
        float distance = position.distance(playerPosition);
        return distance < collectionRadius;
    }

    // Additional method for magnet range detection
    public boolean isInMagnetRange(Vector3f playerPosition, float magnetRadius) {
        if (collected || collecting) {
            return false;
        }

        float distance = position.distance(playerPosition);
        boolean inRange = distance < magnetRadius && distance > 0.3f;

        // Remove debug spam
        return inRange;
    }

    // Method to get distance to player for debugging
    public float getDistanceToPlayer(Vector3f playerPosition) {
        return position.distance(playerPosition);
    }

    // Get collision radius for debugging
    public float getCollisionRadius() {
        return size * 0.8f;
    }

    // Check if coin is close to being collected (for visual feedback)
    public boolean isNearCollection(Vector3f playerPosition, float playerSize) {
        float coinRadius = size * 0.8f;
        float playerRadius = playerSize * 0.5f;
        float totalRadius = (coinRadius + playerRadius) * 1.2f; // 20% larger detection zone

        float distance = position.distance(playerPosition);
        return distance < totalRadius;
    }

    public void collect() {
        if (!collecting && !collected) {
            collecting = true;
            collectionTime = 0.0f;
        }
    }

    public boolean isActive() {
        return !collected && life > 0;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotationY() {
        return rotationY;
    }

    public float getSize() {
        return size;
    }

    public float getLifeRatio() {
        return life / maxLife;
    }

    public boolean isCollected() {
        return collected;
    }

    public boolean isCollecting() {
        return collecting;
    }

    public float getCollectionProgress() {
        if (!collecting) {
            return 0.0f;
        }
        return Math.min(collectionTime / collectionDuration, 1.0f);
    }
}

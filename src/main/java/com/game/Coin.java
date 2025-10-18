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
        this.velocity = new Vector3f(
                (float) (Math.random() - 0.5) * 4.0f, // Random horizontal velocity
                2.0f + (float) Math.random() * 3.0f, // Upward velocity
                (float) (Math.random() - 0.5) * 4.0f // Random horizontal velocity
        );
        this.rotationY = 0.0f;
        this.rotationSpeed = 180.0f + (float) Math.random() * 180.0f; // Random spin speed
        this.life = 15.0f; // Coins last 15 seconds
        this.maxLife = 15.0f;
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

            // Finish collection when very close to player
            if (distanceToPlayer < 0.2f) {
                collected = true;
                System.out.println("Coin fully collected and marked for removal");
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

        // Simple floating behavior - no complex physics
        float hoverHeight = 1.0f; // Target hover height

        if (!inMagnetRange) {
            // Apply gentle settling physics only when not magnetized
            position.add(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);

            // Apply gravity only if above hover height
            if (position.y > hoverHeight) {
                velocity.y -= 5.0f * deltaTime;
            }

            // Ground collision - settle at hover height
            if (position.y <= hoverHeight) {
                position.y = hoverHeight;
                velocity.y = 0.0f; // Stop vertical movement
                velocity.x *= 0.9f; // Apply friction
                velocity.z *= 0.9f;
            }
        } else {
            // When magnetized, just apply velocity directly
            position.add(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);
        }

        // Simple bobbing animation when settled
        if (Math.abs(velocity.x) < 0.1f && Math.abs(velocity.z) < 0.1f && !inMagnetRange) {
            bobTime += deltaTime * 2.0f;
            bobOffset = (float) Math.sin(bobTime) * 0.1f;
            position.y = hoverHeight + bobOffset;
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

        // Use proper 3D collision detection with both coin and player sizes
        float coinRadius = size * 0.8f; // Coin collision radius (slightly smaller than visual)
        float playerRadius = playerSize * 0.5f; // Player collision radius
        float totalRadius = coinRadius + playerRadius;

        float distance = position.distance(playerPosition);
        return distance < totalRadius;
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

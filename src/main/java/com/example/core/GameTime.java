package com.example.core;

/**
 * Time utilities for the game engine
 */
public class GameTime {
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    
    private long startTime;
    private long lastFrameTime;
    private double deltaTime;
    private double totalTime;
    private int frameCount;
    private double fps;
    private double fpsUpdateTimer;
    
    public GameTime() {
        this.startTime = System.nanoTime();
        this.lastFrameTime = startTime;
        this.deltaTime = 0.0;
        this.totalTime = 0.0;
        this.frameCount = 0;
        this.fps = 0.0;
        this.fpsUpdateTimer = 0.0;
    }
    
    /**
     * Update time calculations - call once per frame
     */
    public void update() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastFrameTime) / (double) NANOS_PER_SECOND;
        totalTime = (currentTime - startTime) / (double) NANOS_PER_SECOND;
        lastFrameTime = currentTime;
        
        // Update FPS counter
        frameCount++;
        fpsUpdateTimer += deltaTime;
        if (fpsUpdateTimer >= 1.0) {
            fps = frameCount / fpsUpdateTimer;
            frameCount = 0;
            fpsUpdateTimer = 0.0;
        }
    }
    
    /**
     * Get delta time in seconds since last frame
     */
    public double getDeltaTime() {
        return deltaTime;
    }
    
    /**
     * Get total elapsed time since game start in seconds
     */
    public double getTotalTime() {
        return totalTime;
    }
    
    /**
     * Get current frames per second
     */
    public double getFPS() {
        return fps;
    }
    
    /**
     * Get current time in nanoseconds
     */
    public static long getCurrentTimeNanos() {
        return System.nanoTime();
    }
    
    /**
     * Get current time in milliseconds
     */
    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
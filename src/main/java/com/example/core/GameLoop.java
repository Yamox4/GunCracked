package com.example.core;

/**
 * Fixed-timestep game loop with accumulator pattern
 * Ensures consistent physics updates regardless of frame rate
 */
public class GameLoop {
    private static final double FIXED_TIMESTEP = 1.0 / 60.0; // 60 Hz
    private static final double MAX_ACCUMULATOR = 0.25; // Prevent spiral of death
    
    private double accumulator = 0.0;
    private double currentTime;
    private boolean running = false;
    
    private GameLoopListener listener;
    
    public interface GameLoopListener {
        void onUpdate(double deltaTime);
        void onFixedUpdate(double fixedDeltaTime);
        void onRender(double interpolation);
        boolean shouldExit();
    }
    
    public GameLoop(GameLoopListener listener) {
        this.listener = listener;
        this.currentTime = GameTime.getCurrentTimeNanos() / 1_000_000_000.0;
    }
    
    /**
     * Start the game loop
     */
    public void start() {
        running = true;
        run();
    }
    
    /**
     * Stop the game loop
     */
    public void stop() {
        running = false;
    }
    
    /**
     * Main game loop implementation
     */
    private void run() {
        while (running && !listener.shouldExit()) {
            double newTime = GameTime.getCurrentTimeNanos() / 1_000_000_000.0;
            double frameTime = newTime - currentTime;
            
            // Prevent spiral of death
            if (frameTime > MAX_ACCUMULATOR) {
                frameTime = MAX_ACCUMULATOR;
            }
            
            currentTime = newTime;
            accumulator += frameTime;
            
            // Variable timestep update (input, audio, etc.)
            listener.onUpdate(frameTime);
            
            // Fixed timestep updates (physics, game logic)
            while (accumulator >= FIXED_TIMESTEP) {
                listener.onFixedUpdate(FIXED_TIMESTEP);
                accumulator -= FIXED_TIMESTEP;
            }
            
            // Render with interpolation
            double interpolation = accumulator / FIXED_TIMESTEP;
            listener.onRender(interpolation);
            
            // Yield to prevent 100% CPU usage
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Get the fixed timestep value
     */
    public static double getFixedTimestep() {
        return FIXED_TIMESTEP;
    }
    
    /**
     * Get target FPS (1 / fixed timestep)
     */
    public static double getTargetFPS() {
        return 1.0 / FIXED_TIMESTEP;
    }
}
package com.example.demo;

import com.example.core.GameEngine;
import com.example.logging.GameLogger;
import com.example.math.GameMath;
import com.example.math.GameMath.AABB;
import com.example.math.GameMath.Quat;
import com.example.math.GameMath.Vec3;

/**
 * Minimal test of core systems only:
 * - Fixed-timestep loop (60 Hz) with accumulator
 * - Time utilities
 * - Input
 * - Logging/config
 * - Math primitives (Vec3, Quat, Mat4, AABB)
 */
public class CoreSystemsTest extends GameEngine {
    
    private double fixedUpdateCounter = 0.0;
    
    @Override
    protected void initializeGame() {
        GameLogger.info("Testing core systems...");
        
        // Test math primitives
        testMathPrimitives();
        
        GameLogger.info("Core systems test initialized - Press ESC to exit");
    }
    
    private void testMathPrimitives() {
        GameLogger.info("Testing math primitives...");
        
        // Test Vec3
        Vec3 v1 = new Vec3(1, 2, 3);
        Vec3 v2 = new Vec3(4, 5, 6);
        Vec3 v3 = v1.add(v2);
        GameLogger.info("Vec3 test: " + v1 + " + " + v2 + " = " + v3);
        
        // Test Quat
        Quat q1 = Quat.fromAngles(0, GameMath.HALF_PI, 0);
        Vec3 rotatedVector = q1.multiply(Vec3.FORWARD);
        GameLogger.info("Quat test: Rotating FORWARD by 90° Y = " + rotatedVector);
        
        // Test AABB
        AABB box1 = new AABB(new Vec3(-1, -1, -1), new Vec3(1, 1, 1));
        AABB box2 = new AABB(new Vec3(0, 0, 0), new Vec3(2, 2, 2));
        boolean intersects = box1.intersects(box2);
        GameLogger.info("AABB test: " + box1 + " intersects " + box2 + " = " + intersects);
        
        GameLogger.info("Math primitives test completed");
    }
    
    @Override
    protected void updateVariable(double deltaTime) {
        // Test input system
        if (getGameInput().isKeyPressed("INTERACT")) {
            GameLogger.info("Interact key (E) pressed!");
        }
        
        if (getGameInput().isMousePressed("MOUSE_LEFT")) {
            GameLogger.info("Left mouse button pressed!");
        }
    }
    
    @Override
    protected void updateFixed(double fixedDeltaTime) {
        fixedUpdateCounter += fixedDeltaTime;
        
        // Log every 2 seconds to show fixed timestep is working
        if (fixedUpdateCounter >= 2.0) {
            GameLogger.info(String.format("Fixed timestep (60Hz) working - FPS: %.1f, Time: %.2fs", 
                getGameTime().getFPS(), getGameTime().getTotalTime()));
            fixedUpdateCounter = 0.0;
        }
    }
    
    @Override
    protected void render(double interpolation) {
        // Nothing to render, just testing core systems
    }
    
    public static void main(String[] args) {
        GameLogger.info("Starting Core Systems Test...");
        
        CoreSystemsTest test = new CoreSystemsTest();
        test.startEngine();
    }
}
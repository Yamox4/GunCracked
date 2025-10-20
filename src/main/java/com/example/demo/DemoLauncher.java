package com.example.demo;

import com.jme3.system.AppSettings;

/**
 * Demo launcher with proper settings
 */
public class DemoLauncher {
    
    public static void main(String[] args) {
        // Create and configure the demo
        InteractivePhysicsDemo demo = new InteractivePhysicsDemo();
        
        // Configure settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Interactive Physics Demo - JMonkeyEngine");
        settings.setResolution(1400, 900);
        settings.setVSync(true);
        settings.setSamples(4); // Anti-aliasing
        settings.setFrameRate(280);
        settings.setFullscreen(false);
        
        demo.setSettings(settings);
        demo.setShowSettings(false); // Skip settings dialog
        demo.start();
    }
}
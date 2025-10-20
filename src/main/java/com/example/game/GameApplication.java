package com.example.game;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

/**
 * Main application entry point
 */
public class GameApplication extends SimpleApplication {
    
    public static void main(String[] args) {
        GameApplication app = new GameApplication();
        
        // Configure application settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("JMonkeyEngine 3D Physics Engine Demo");
        settings.setResolution(1280, 720);
        settings.setVSync(true);
        settings.setSamples(4); // Anti-aliasing
        settings.setFrameRate(60);
        
        app.setSettings(settings);
        app.setShowSettings(false); // Skip settings dialog
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // Start the physics demo
        PhysicsDemo demo = new PhysicsDemo();
        demo.setSettings(settings);
        demo.setShowSettings(false);
        
        // Stop this application and start the demo
        stop();
        demo.start();
    }
}
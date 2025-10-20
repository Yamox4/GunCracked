package com.physics3d;

import com.physics3d.game.GameLoop;
import com.jme3.system.AppSettings;

/**
 * Main class to start the 3D game.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting 3D Game...");

        // Create game with disabled audio to avoid OpenAL issues
        GameLoop game = new GameLoop();

        // Configure settings to disable audio
        AppSettings settings = new AppSettings(true);
        settings.setTitle("3D Physics Game Demo");
        settings.setResolution(1024, 768);
        settings.setAudioRenderer(null); // Disable audio

        game.setSettings(settings);
        game.setShowSettings(false); // Skip settings dialog
        game.start();
    }
}

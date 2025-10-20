package com.example.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * JME Config Module - configuration management
 */
public class ConfigModule {
    private static final String CONFIG_FILE = "jme_config.properties";
    private static Properties properties = new Properties();
    private static boolean initialized = false;
    
    /**
     * Initialize the config module
     */
    public static void initialize() {
        if (initialized) return;
        
        setDefaults();
        loadFromFile();
        initialized = true;
    }
    
    /**
     * Set default configuration values
     */
    private static void setDefaults() {
        // Window settings
        properties.setProperty("window.width", "1024");
        properties.setProperty("window.height", "768");
        properties.setProperty("window.fullscreen", "false");
        properties.setProperty("window.title", "JME Application");
        
        // Graphics settings
        properties.setProperty("graphics.vsync", "true");
        properties.setProperty("graphics.samples", "4");
        properties.setProperty("graphics.gamma_correction", "false");
        
        // Input settings
        properties.setProperty("input.mouse_sensitivity", "1.0");
        properties.setProperty("input.invert_mouse", "false");
        
        // Audio settings
        properties.setProperty("audio.master_volume", "1.0");
        properties.setProperty("audio.music_volume", "0.8");
        properties.setProperty("audio.sfx_volume", "1.0");
        
        // Logging settings
        properties.setProperty("logging.level", "INFO");
        properties.setProperty("logging.console_output", "true");
    }
    
    /**
     * Load configuration from file
     */
    private static void loadFromFile() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Failed to load config file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Save configuration to file
     */
    public static void save() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "JME Module Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }
    
    // Generic getters/setters
    public static String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static void setString(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static void setInt(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    public static float getFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static void setFloat(String key, float value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }
    
    public static void setBoolean(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    // Convenience methods for common settings
    public static int getWindowWidth() { return getInt("window.width", 1024); }
    public static int getWindowHeight() { return getInt("window.height", 768); }
    public static boolean isFullscreen() { return getBoolean("window.fullscreen", false); }
    public static String getWindowTitle() { return getString("window.title", "JME Application"); }
    
    public static boolean isVSync() { return getBoolean("graphics.vsync", true); }
    public static int getSamples() { return getInt("graphics.samples", 4); }
    
    public static float getMouseSensitivity() { return getFloat("input.mouse_sensitivity", 1.0f); }
    public static boolean isMouseInverted() { return getBoolean("input.invert_mouse", false); }
    
    public static float getMasterVolume() { return getFloat("audio.master_volume", 1.0f); }
    
    public static Level getLogLevel() {
        String levelStr = getString("logging.level", "INFO");
        try {
            return Level.parse(levelStr);
        } catch (IllegalArgumentException e) {
            return Level.INFO;
        }
    }
    
    /**
     * Cleanup the config module
     */
    public static void cleanup() {
        save();
        properties.clear();
        initialized = false;
    }
}
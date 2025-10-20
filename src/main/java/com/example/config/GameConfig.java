package com.example.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Game configuration management
 */
public class GameConfig {
    private static final String CONFIG_FILE = "game.properties";
    private static Properties properties = new Properties();
    private static boolean loaded = false;
    
    // Default values
    private static final String DEFAULT_WINDOW_WIDTH = "1024";
    private static final String DEFAULT_WINDOW_HEIGHT = "768";
    private static final String DEFAULT_FULLSCREEN = "false";
    private static final String DEFAULT_VSYNC = "true";
    private static final String DEFAULT_SAMPLES = "4";
    private static final String DEFAULT_LOG_LEVEL = "INFO";
    private static final String DEFAULT_MOUSE_SENSITIVITY = "1.0";
    private static final String DEFAULT_MASTER_VOLUME = "1.0";
    
    /**
     * Load configuration from file
     */
    public static void load() {
        if (loaded) return;
        
        // Set defaults
        setDefaults();
        
        // Try to load from file
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Failed to load config file: " + e.getMessage());
            }
        }
        
        loaded = true;
    }
    
    /**
     * Save configuration to file
     */
    public static void save() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Game Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }
    
    /**
     * Set default configuration values
     */
    private static void setDefaults() {
        properties.setProperty("window.width", DEFAULT_WINDOW_WIDTH);
        properties.setProperty("window.height", DEFAULT_WINDOW_HEIGHT);
        properties.setProperty("window.fullscreen", DEFAULT_FULLSCREEN);
        properties.setProperty("graphics.vsync", DEFAULT_VSYNC);
        properties.setProperty("graphics.samples", DEFAULT_SAMPLES);
        properties.setProperty("logging.level", DEFAULT_LOG_LEVEL);
        properties.setProperty("input.mouse_sensitivity", DEFAULT_MOUSE_SENSITIVITY);
        properties.setProperty("audio.master_volume", DEFAULT_MASTER_VOLUME);
    }
    
    // Window settings
    public static int getWindowWidth() {
        return getInt("window.width", Integer.parseInt(DEFAULT_WINDOW_WIDTH));
    }
    
    public static void setWindowWidth(int width) {
        setInt("window.width", width);
    }
    
    public static int getWindowHeight() {
        return getInt("window.height", Integer.parseInt(DEFAULT_WINDOW_HEIGHT));
    }
    
    public static void setWindowHeight(int height) {
        setInt("window.height", height);
    }
    
    public static boolean isFullscreen() {
        return getBoolean("window.fullscreen", Boolean.parseBoolean(DEFAULT_FULLSCREEN));
    }
    
    public static void setFullscreen(boolean fullscreen) {
        setBoolean("window.fullscreen", fullscreen);
    }
    
    // Graphics settings
    public static boolean isVSync() {
        return getBoolean("graphics.vsync", Boolean.parseBoolean(DEFAULT_VSYNC));
    }
    
    public static void setVSync(boolean vsync) {
        setBoolean("graphics.vsync", vsync);
    }
    
    public static int getSamples() {
        return getInt("graphics.samples", Integer.parseInt(DEFAULT_SAMPLES));
    }
    
    public static void setSamples(int samples) {
        setInt("graphics.samples", samples);
    }
    
    // Logging settings
    public static Level getLogLevel() {
        String levelStr = getString("logging.level", DEFAULT_LOG_LEVEL);
        try {
            return Level.parse(levelStr);
        } catch (IllegalArgumentException e) {
            return Level.INFO;
        }
    }
    
    public static void setLogLevel(Level level) {
        setString("logging.level", level.getName());
    }
    
    // Input settings
    public static float getMouseSensitivity() {
        return getFloat("input.mouse_sensitivity", Float.parseFloat(DEFAULT_MOUSE_SENSITIVITY));
    }
    
    public static void setMouseSensitivity(float sensitivity) {
        setFloat("input.mouse_sensitivity", sensitivity);
    }
    
    // Audio settings
    public static float getMasterVolume() {
        return getFloat("audio.master_volume", Float.parseFloat(DEFAULT_MASTER_VOLUME));
    }
    
    public static void setMasterVolume(float volume) {
        setFloat("audio.master_volume", volume);
    }
    
    // Generic getters/setters
    private static String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    private static void setString(String key, String value) {
        properties.setProperty(key, value);
    }
    
    private static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private static void setInt(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    private static float getFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private static void setFloat(String key, float value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    private static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }
    
    private static void setBoolean(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }
}
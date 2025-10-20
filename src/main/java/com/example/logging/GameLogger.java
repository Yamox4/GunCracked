package com.example.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Game logging system with different log levels and formatting
 */
public class GameLogger {
    private static final Logger logger = Logger.getLogger("GameEngine");
    private static boolean initialized = false;
    
    /**
     * Initialize the logging system
     */
    public static void initialize() {
        if (initialized) return;
        
        // Remove default handlers
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
        for (var handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }
        
        // Add custom console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new GameLogFormatter());
        
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        
        initialized = true;
        info("Game logging system initialized");
    }
    
    /**
     * Log debug message
     */
    public static void debug(String message) {
        logger.log(Level.FINE, message);
    }
    
    /**
     * Log debug message with exception
     */
    public static void debug(String message, Throwable throwable) {
        logger.log(Level.FINE, message, throwable);
    }
    
    /**
     * Log info message
     */
    public static void info(String message) {
        logger.log(Level.INFO, message);
    }
    
    /**
     * Log info message with exception
     */
    public static void info(String message, Throwable throwable) {
        logger.log(Level.INFO, message, throwable);
    }
    
    /**
     * Log warning message
     */
    public static void warn(String message) {
        logger.log(Level.WARNING, message);
    }
    
    /**
     * Log warning message with exception
     */
    public static void warn(String message, Throwable throwable) {
        logger.log(Level.WARNING, message, throwable);
    }
    
    /**
     * Log error message
     */
    public static void error(String message) {
        logger.log(Level.SEVERE, message);
    }
    
    /**
     * Log error message with exception
     */
    public static void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }
    
    /**
     * Set logging level
     */
    public static void setLevel(Level level) {
        logger.setLevel(level);
    }
    
    /**
     * Custom log formatter for game messages
     */
    private static class GameLogFormatter extends Formatter {
        private static final DateTimeFormatter TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            
            // Timestamp
            sb.append("[").append(LocalDateTime.now().format(TIME_FORMATTER)).append("] ");
            
            // Level
            String level = record.getLevel().getName();
            sb.append("[").append(level).append("] ");
            
            // Message
            sb.append(record.getMessage());
            
            // Exception if present
            if (record.getThrown() != null) {
                sb.append("\n").append(record.getThrown().toString());
                for (StackTraceElement element : record.getThrown().getStackTrace()) {
                    sb.append("\n\tat ").append(element.toString());
                }
            }
            
            sb.append("\n");
            return sb.toString();
        }
    }
}
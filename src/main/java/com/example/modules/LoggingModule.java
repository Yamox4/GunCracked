package com.example.modules;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * JME Logging Module - centralized logging system
 */
public class LoggingModule {
    private static Logger logger;
    private static boolean initialized = false;
    
    /**
     * Initialize the logging module
     */
    public static void initialize() {
        initialize(Level.INFO);
    }
    
    /**
     * Initialize the logging module with specific level
     */
    public static void initialize(Level level) {
        if (initialized) return;
        
        logger = Logger.getLogger("JME_MODULE");
        
        // Remove default handlers
        Logger rootLogger = Logger.getLogger("");
        for (var handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }
        
        // Add custom console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new ModuleLogFormatter());
        
        logger.addHandler(consoleHandler);
        logger.setLevel(level);
        logger.setUseParentHandlers(false);
        
        initialized = true;
        info("Logging module initialized");
    }
    
    /**
     * Set logging level
     */
    public static void setLevel(Level level) {
        if (logger != null) {
            logger.setLevel(level);
        }
    }
    
    // Logging methods
    public static void debug(String message) {
        if (logger != null) logger.log(Level.FINE, message);
    }
    
    public static void info(String message) {
        if (logger != null) logger.log(Level.INFO, message);
    }
    
    public static void warn(String message) {
        if (logger != null) logger.log(Level.WARNING, message);
    }
    
    public static void error(String message) {
        if (logger != null) logger.log(Level.SEVERE, message);
    }
    
    public static void error(String message, Throwable throwable) {
        if (logger != null) logger.log(Level.SEVERE, message, throwable);
    }
    
    /**
     * Cleanup the logging module
     */
    public static void cleanup() {
        if (logger != null) {
            for (var handler : logger.getHandlers()) {
                handler.close();
                logger.removeHandler(handler);
            }
        }
        initialized = false;
    }
    
    /**
     * Custom log formatter
     */
    private static class ModuleLogFormatter extends Formatter {
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
            }
            
            sb.append("\n");
            return sb.toString();
        }
    }
}
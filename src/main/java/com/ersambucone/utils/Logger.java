package com.ersambucone.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Optimized logger with asynchronous file logging and log level filtering
 */
public class Logger {
    // Log levels
    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }
    
    // Current log level - only messages at this level or higher will be logged
    private static Level currentLevel = Level.INFO;
    
    // Log file
    private static final String LOG_FILE = "ersambucone/client.log";
    private static final int MAX_LOG_SIZE_KB = 1024; // 1MB max log size
    
    // Async logging
    private static final Queue<LogEntry> logQueue = new ConcurrentLinkedQueue<>();
    private static final ScheduledExecutorService logExecutor = Executors.newSingleThreadScheduledExecutor();
    private static boolean initialized = false;
    
    // Log format
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Initialize the logger
     */
    private static void initialize() {
        if (initialized) return;
        
        // Start background thread to process log queue
        logExecutor.scheduleAtFixedRate(() -> {
            processLogQueue();
        }, 0, 1, TimeUnit.SECONDS);
        
        // Create log directory if it doesn't exist
        File logDir = new File("ersambucone");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
        // Check log file size and rotate if needed
        File logFile = new File(LOG_FILE);
        if (logFile.exists() && logFile.length() > MAX_LOG_SIZE_KB * 1024) {
            rotateLogFile();
        }
        
        // Add shutdown hook to flush logs
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            processLogQueue();
            logExecutor.shutdown();
        }));
        
        initialized = true;
        log("Logger initialized");
    }
    
    /**
     * Rotate log file
     */
    private static void rotateLogFile() {
        File logFile = new File(LOG_FILE);
        if (!logFile.exists()) return;
        
        // Rename current log file
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        File backupFile = new File(LOG_FILE + "." + timestamp);
        logFile.renameTo(backupFile);
    }
    
    /**
     * Process queued log entries
     */
    private static void processLogQueue() {
        if (logQueue.isEmpty()) return;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            LogEntry entry;
            while ((entry = logQueue.poll()) != null) {
                writer.println(entry.formattedMessage);
            }
        } catch (IOException e) {
            // Can't log the error because it would cause recursion
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
    
    /**
     * Log a message at INFO level
     * 
     * @param message The message to log
     */
    public static void log(String message) {
        log(Level.INFO, message);
    }
    
    /**
     * Log a message at the specified level
     * 
     * @param level The log level
     * @param message The message to log
     */
    public static void log(Level level, String message) {
        if (level.ordinal() < currentLevel.ordinal()) {
            return; // Skip messages below current level
        }
        
        if (!initialized) {
            initialize();
        }
        
        String timestamp = dateFormat.format(new Date());
        String formattedMessage = String.format("[%s] [%s] [ErSambucone] %s", 
                timestamp, level.name(), message);
        
        // Print to console
        if (level == Level.ERROR) {
            System.err.println(formattedMessage);
        } else {
            System.out.println(formattedMessage);
        }
        
        // Queue for file logging
        logQueue.add(new LogEntry(level, formattedMessage));
    }
    
    /**
     * Log a debug message
     * 
     * @param message The message to log
     */
    public static void debug(String message) {
        log(Level.DEBUG, message);
    }
    
    /**
     * Log a warning message
     * 
     * @param message The message to log
     */
    public static void warn(String message) {
        log(Level.WARN, message);
    }
    
    /**
     * Log an error message
     * 
     * @param message The message to log
     */
    public static void error(String message) {
        log(Level.ERROR, message);
    }
    
    /**
     * Log an exception
     * 
     * @param e The exception to log
     */
    public static void error(Exception e) {
        log(Level.ERROR, e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            log(Level.ERROR, "  at " + element.toString());
        }
    }
    
    /**
     * Set the current log level
     * 
     * @param level The new log level
     */
    public static void setLevel(Level level) {
        currentLevel = level;
        log("Log level set to " + level.name());
    }
    
    /**
     * Check if debug logging is enabled
     * 
     * @return True if debug logging is enabled
     */
    public static boolean isDebugEnabled() {
        return currentLevel == Level.DEBUG;
    }
    
    /**
     * Log entry for async processing
     */
    private static class LogEntry {
        final Level level;
        final String formattedMessage;
        
        LogEntry(Level level, String formattedMessage) {
            this.level = level;
            this.formattedMessage = formattedMessage;
        }
    }
}
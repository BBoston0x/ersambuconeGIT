package com.ersambucone.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Monitors performance metrics for the client
 * Helps identify modules or features that might be causing lag
 */
public class PerformanceMonitor {
    // Track FPS over time
    private final int[] fpsHistory = new int[60]; // Last 60 seconds
    private int fpsHistoryIndex = 0;
    private long lastFpsUpdate = 0;
    
    // Track memory usage
    private long lastMemoryCheck = 0;
    private long lastUsedMemory = 0;
    private static final long MEMORY_CHECK_INTERVAL = 5000; // 5 seconds
    
    // Track active modules and their impact
    private final Map<String, ModuleMetrics> moduleMetrics = new ConcurrentHashMap<>();
    
    // Track frame time (using LongAdder for better concurrency)
    private final LongAdder totalFrameTime = new LongAdder();
    private final LongAdder frameCount = new LongAdder();
    private long lastFrameTimeReset = System.currentTimeMillis();
    private static final long FRAME_TIME_RESET_INTERVAL = 10000; // 10 seconds
    
    // Performance warning thresholds
    private static final int LOW_FPS_THRESHOLD = 30;
    private static final int HIGH_MEMORY_THRESHOLD_MB = 1024; // 1GB
    
    /**
     * Updates FPS history
     * 
     * @param currentFps Current FPS
     */
    public void updateFps(int currentFps) {
        long now = System.currentTimeMillis();
        if (now - lastFpsUpdate > 1000) { // Update once per second
            fpsHistory[fpsHistoryIndex] = currentFps;
            fpsHistoryIndex = (fpsHistoryIndex + 1) % fpsHistory.length;
            lastFpsUpdate = now;
        }
    }
    
    /**
     * Gets the average FPS over the last minute
     * 
     * @return Average FPS
     */
    public int getAverageFps() {
        int sum = 0;
        int count = 0;
        
        for (int fps : fpsHistory) {
            if (fps > 0) {
                sum += fps;
                count++;
            }
        }
        
        return count > 0 ? sum / count : 0;
    }
    
    /**
     * Updates memory usage metrics
     */
    public void updateMemoryUsage() {
        long now = System.currentTimeMillis();
        if (now - lastMemoryCheck > MEMORY_CHECK_INTERVAL) {
            Runtime runtime = Runtime.getRuntime();
            lastUsedMemory = runtime.totalMemory() - runtime.freeMemory();
            lastMemoryCheck = now;
        }
    }
    
    /**
     * Gets the current memory usage in MB
     * 
     * @return Memory usage in MB
     */
    public int getMemoryUsageMB() {
        return (int) (lastUsedMemory / (1024 * 1024));
    }
    
    /**
     * Tracks when a module is enabled
     * 
     * @param moduleName Module name
     */
    public void trackModuleEnabled(String moduleName) {
        moduleMetrics.computeIfAbsent(moduleName, k -> new ModuleMetrics())
                     .recordEnable();
    }
    
    /**
     * Tracks when a module is disabled
     * 
     * @param moduleName Module name
     */
    public void trackModuleDisabled(String moduleName) {
        ModuleMetrics metrics = moduleMetrics.get(moduleName);
        if (metrics != null) {
            metrics.recordDisable();
        }
    }
    
    /**
     * Records frame time for performance monitoring
     * 
     * @param frameTimeNanos Frame time in nanoseconds
     */
    public void recordFrameTime(long frameTimeNanos) {
        totalFrameTime.add(frameTimeNanos);
        frameCount.increment();
        
        long now = System.currentTimeMillis();
        if (now - lastFrameTimeReset > FRAME_TIME_RESET_INTERVAL) {
            // Reset counters periodically
            totalFrameTime.reset();
            frameCount.reset();
            lastFrameTimeReset = now;
        }
    }
    
    /**
     * Gets the average frame time in milliseconds
     * 
     * @return Average frame time in ms
     */
    public double getAverageFrameTimeMs() {
        long frames = frameCount.sum();
        if (frames == 0) return 0;
        
        return (totalFrameTime.sum() / 1_000_000.0) / frames;
    }
    
    /**
     * Checks if the client is experiencing performance issues
     * 
     * @return true if performance issues are detected
     */
    public boolean hasPerformanceIssues() {
        return getAverageFps() < LOW_FPS_THRESHOLD || 
               getMemoryUsageMB() > HIGH_MEMORY_THRESHOLD_MB;
    }
    
    /**
     * Gets performance recommendations based on current metrics
     * 
     * @return A string with performance recommendations
     */
    public String getPerformanceRecommendations() {
        StringBuilder recommendations = new StringBuilder();
        
        if (getAverageFps() < LOW_FPS_THRESHOLD) {
            recommendations.append("- Low FPS detected. Consider disabling some modules.\n");
        }
        
        if (getMemoryUsageMB() > HIGH_MEMORY_THRESHOLD_MB) {
            recommendations.append("- High memory usage. Consider restarting the client.\n");
        }
        
        // Find modules that might be causing issues
        moduleMetrics.forEach((name, metrics) -> {
            if (metrics.getEnableCount() > 0 && metrics.getTotalActiveTimeSeconds() > 300) {
                recommendations.append("- Module '").append(name)
                              .append("' has been active for a long time. Consider disabling if not needed.\n");
            }
        });
        
        return recommendations.length() > 0 ? recommendations.toString() : "No performance issues detected.";
    }
    
    /**
     * Gets performance metrics for all modules
     * 
     * @return Map of module names to their metrics
     */
    public Map<String, ModuleMetrics> getModuleMetrics() {
        return new HashMap<>(moduleMetrics);
    }
    
    /**
     * Metrics for a single module
     */
    public static class ModuleMetrics {
        private long enableCount = 0;
        private long disableCount = 0;
        private long totalActiveTime = 0;
        private long lastEnableTime = 0;
        
        public void recordEnable() {
            enableCount++;
            lastEnableTime = System.currentTimeMillis();
        }
        
        public void recordDisable() {
            disableCount++;
            if (lastEnableTime > 0) {
                totalActiveTime += System.currentTimeMillis() - lastEnableTime;
            }
        }
        
        public long getEnableCount() {
            return enableCount;
        }
        
        public long getDisableCount() {
            return disableCount;
        }
        
        public long getTotalActiveTimeSeconds() {
            return totalActiveTime / 1000;
        }
    }
}
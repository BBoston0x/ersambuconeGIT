package com.ersambucone.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
    
    // Track frame time
    private final AtomicLong totalFrameTime = new AtomicLong(0);
    private final AtomicLong frameCount = new AtomicLong(0);
    private long lastFrameTimeReset = System.currentTimeMillis();
    private static final long FRAME_TIME_RESET_INTERVAL = 10000; // 10 seconds
    
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
        totalFrameTime.addAndGet(frameTimeNanos);
        frameCount.incrementAndGet();
        
        long now = System.currentTimeMillis();
        if (now - lastFrameTimeReset > FRAME_TIME_RESET_INTERVAL) {
            // Reset counters periodically
            totalFrameTime.set(0);
            frameCount.set(0);
            lastFrameTimeReset = now;
        }
    }
    
    /**
     * Gets the average frame time in milliseconds
     * 
     * @return Average frame time in ms
     */
    public double getAverageFrameTimeMs() {
        long frames = frameCount.get();
        if (frames == 0) return 0;
        
        return (totalFrameTime.get() / 1_000_000.0) / frames;
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
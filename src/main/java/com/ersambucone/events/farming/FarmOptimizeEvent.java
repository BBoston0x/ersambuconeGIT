package com.ersambucone.events.farming;

import java.util.List;

/**
 * Event fired when a farm is optimized
 */
public class FarmOptimizeEvent {
    private final String farmType;
    private final List<String> optimizations;
    private final double efficiencyImprovement;
    private final long timestamp;
    
    /**
     * Creates a new FarmOptimizeEvent
     * 
     * @param farmType The farm type
     * @param optimizations The optimizations
     * @param efficiencyImprovement The efficiency improvement
     */
    public FarmOptimizeEvent(String farmType, List<String> optimizations, double efficiencyImprovement) {
        this.farmType = farmType;
        this.optimizations = optimizations;
        this.efficiencyImprovement = efficiencyImprovement;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Gets the farm type
     * 
     * @return The farm type
     */
    public String getFarmType() {
        return farmType;
    }
    
    /**
     * Gets the optimizations
     * 
     * @return The optimizations
     */
    public List<String> getOptimizations() {
        return optimizations;
    }
    
    /**
     * Gets the efficiency improvement
     * 
     * @return The efficiency improvement
     */
    public double getEfficiencyImprovement() {
        return efficiencyImprovement;
    }
    
    /**
     * Gets the timestamp
     * 
     * @return The timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
}
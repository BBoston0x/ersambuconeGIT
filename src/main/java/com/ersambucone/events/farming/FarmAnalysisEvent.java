package com.ersambucone.events.farming;

/**
 * Event fired when a farm is analyzed
 */
public class FarmAnalysisEvent {
    private final String farmType;
    private final int cropCount;
    private final double efficiency;
    private final long timestamp;
    
    /**
     * Creates a new FarmAnalysisEvent
     * 
     * @param farmType The farm type
     * @param cropCount The crop count
     * @param efficiency The efficiency
     */
    public FarmAnalysisEvent(String farmType, int cropCount, double efficiency) {
        this.farmType = farmType;
        this.cropCount = cropCount;
        this.efficiency = efficiency;
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
     * Gets the crop count
     * 
     * @return The crop count
     */
    public int getCropCount() {
        return cropCount;
    }
    
    /**
     * Gets the efficiency
     * 
     * @return The efficiency
     */
    public double getEfficiency() {
        return efficiency;
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
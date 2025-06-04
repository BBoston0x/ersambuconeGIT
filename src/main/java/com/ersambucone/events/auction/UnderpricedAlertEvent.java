package com.ersambucone.events.auction;

import com.ersambucone.data.Auction;

/**
 * Event fired when an underpriced auction is detected
 */
public class UnderpricedAlertEvent {
    private final Auction auction;
    private final double estimatedValue;
    private final double priceDifference;
    private final long timestamp;
    
    /**
     * Creates a new UnderpricedAlertEvent
     * 
     * @param auction The auction
     * @param estimatedValue The estimated value
     * @param priceDifference The price difference
     */
    public UnderpricedAlertEvent(Auction auction, double estimatedValue, double priceDifference) {
        this.auction = auction;
        this.estimatedValue = estimatedValue;
        this.priceDifference = priceDifference;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Gets the auction
     * 
     * @return The auction
     */
    public Auction getAuction() {
        return auction;
    }
    
    /**
     * Gets the estimated value
     * 
     * @return The estimated value
     */
    public double getEstimatedValue() {
        return estimatedValue;
    }
    
    /**
     * Gets the price difference
     * 
     * @return The price difference
     */
    public double getPriceDifference() {
        return priceDifference;
    }
    
    /**
     * Gets the price difference as a percentage
     * 
     * @return The price difference as a percentage
     */
    public double getPriceDifferencePercentage() {
        return (priceDifference / estimatedValue) * 100;
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
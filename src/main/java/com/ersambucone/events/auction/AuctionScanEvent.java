package com.ersambucone.events.auction;

import com.ersambucone.data.Auction;

import java.util.List;

/**
 * Event fired when auctions are scanned
 */
public class AuctionScanEvent {
    private final List<Auction> auctions;
    private final long timestamp;
    
    /**
     * Creates a new AuctionScanEvent
     * 
     * @param auctions The auctions
     */
    public AuctionScanEvent(List<Auction> auctions) {
        this.auctions = auctions;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Gets the auctions
     * 
     * @return The auctions
     */
    public List<Auction> getAuctions() {
        return auctions;
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
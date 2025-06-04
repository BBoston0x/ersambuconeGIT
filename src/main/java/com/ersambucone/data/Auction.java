package com.ersambucone.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an auction in the Skyblock auction house
 */
public class Auction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final UUID auctionId;
    private final String itemName;
    private final String itemId;
    private final long startingBid;
    private final long highestBid;
    private final long endTime;
    private final String seller;
    private final Map<String, Object> extraData;
    
    /**
     * Creates a new Auction
     * 
     * @param auctionId The auction ID
     * @param itemName The item name
     * @param itemId The item ID
     * @param startingBid The starting bid
     * @param highestBid The highest bid
     * @param endTime The end time
     * @param seller The seller
     */
    public Auction(UUID auctionId, String itemName, String itemId, long startingBid, 
                  long highestBid, long endTime, String seller) {
        this.auctionId = auctionId;
        this.itemName = itemName;
        this.itemId = itemId;
        this.startingBid = startingBid;
        this.highestBid = highestBid;
        this.endTime = endTime;
        this.seller = seller;
        this.extraData = new HashMap<>();
    }
    
    /**
     * Gets the auction ID
     * 
     * @return The auction ID
     */
    public UUID getAuctionId() {
        return auctionId;
    }
    
    /**
     * Gets the item name
     * 
     * @return The item name
     */
    public String getItemName() {
        return itemName;
    }
    
    /**
     * Gets the item ID
     * 
     * @return The item ID
     */
    public String getItemId() {
        return itemId;
    }
    
    /**
     * Gets the starting bid
     * 
     * @return The starting bid
     */
    public long getStartingBid() {
        return startingBid;
    }
    
    /**
     * Gets the highest bid
     * 
     * @return The highest bid
     */
    public long getHighestBid() {
        return highestBid;
    }
    
    /**
     * Gets the current price (highest bid or starting bid if no bids)
     * 
     * @return The current price
     */
    public long getCurrentPrice() {
        return highestBid > 0 ? highestBid : startingBid;
    }
    
    /**
     * Gets the end time
     * 
     * @return The end time
     */
    public long getEndTime() {
        return endTime;
    }
    
    /**
     * Gets the seller
     * 
     * @return The seller
     */
    public String getSeller() {
        return seller;
    }
    
    /**
     * Gets the time remaining
     * 
     * @return The time remaining in milliseconds
     */
    public long getTimeRemaining() {
        return Math.max(0, endTime - System.currentTimeMillis());
    }
    
    /**
     * Checks if the auction has ended
     * 
     * @return True if the auction has ended
     */
    public boolean hasEnded() {
        return System.currentTimeMillis() >= endTime;
    }
    
    /**
     * Sets extra data
     * 
     * @param key The key
     * @param value The value
     */
    public void setExtraData(String key, Object value) {
        extraData.put(key, value);
    }
    
    /**
     * Gets extra data
     * 
     * @param key The key
     * @return The value
     */
    public Object getExtraData(String key) {
        return extraData.get(key);
    }
    
    @Override
    public String toString() {
        return "Auction{" +
                "itemName='" + itemName + '\'' +
                ", price=" + getCurrentPrice() +
                ", seller='" + seller + '\'' +
                '}';
    }
}
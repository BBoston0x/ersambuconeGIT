package com.ersambucone.ai;

import com.ersambucone.data.Auction;
import com.ersambucone.utils.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Model for predicting item prices
 */
public class PriceModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Map<String, Double> itemAverages = new ConcurrentHashMap<>();

    /**
     * Loads the model from a file
     * 
     * @param path The path to the file
     * @throws IOException If an I/O error occurs
     */
    public void load(String path) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            @SuppressWarnings("unchecked")
            Map<String, Double> loadedMap = (Map<String, Double>) ois.readObject();
            itemAverages.putAll(loadedMap);
            Logger.log("Loaded price model with " + itemAverages.size() + " items");
        } catch (ClassNotFoundException e) {
            throw new IOException("Corrupted model: " + e.getMessage());
        }
    }
    
    /**
     * Saves the model to a file
     * 
     * @param path The path to the file
     * @throws IOException If an I/O error occurs
     */
    public void save(String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(itemAverages);
            Logger.log("Saved price model with " + itemAverages.size() + " items");
        }
    }

    /**
     * Checks if an auction is underpriced
     * 
     * @param auction The auction
     * @return True if the auction is underpriced
     */
    public boolean isUnderpriced(Auction auction) {
        String key = auction.getItemId();
        double avg = itemAverages.getOrDefault(key, Double.MAX_VALUE);
        return auction.getCurrentPrice() < (avg * 0.85);
    }

    /**
     * Updates the model with new auction data
     * 
     * @param auctions The auctions
     */
    public void update(List<Auction> auctions) {
        auctions.forEach(auction -> {
            String key = auction.getItemId();
            itemAverages.merge(key, (double) auction.getCurrentPrice(), 
                (old, newVal) -> (old * 0.7 + newVal * 0.3)); // Weighted average
        });
    }
    
    /**
     * Gets the estimated price for an item
     * 
     * @param itemId The item ID
     * @return The estimated price, or -1 if unknown
     */
    public double getEstimatedPrice(String itemId) {
        return itemAverages.getOrDefault(itemId, -1.0);
    }
    
    /**
     * Gets the number of items in the model
     * 
     * @return The number of items
     */
    public int getItemCount() {
        return itemAverages.size();
    }
}
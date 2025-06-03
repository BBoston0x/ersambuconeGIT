package com.ersambucone.modules.impl.utility;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;

/**
 * Provides utility functions for inventory management
 */
public class InventoryUtils extends Module {
    private boolean autoSort = false;
    private int sortDelay = 500;
    
    public InventoryUtils() {
        super("InventoryUtils", "Provides utility functions for inventory management", Category.UTILITY);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("InventoryUtils will " + (autoSort ? "automatically sort inventory" : "not automatically sort inventory"));
        Logger.log("Sort delay: " + sortDelay + "ms");
    }
    
    /**
     * Sets whether to automatically sort the inventory
     * 
     * @param autoSort True to automatically sort, false otherwise
     */
    public void setAutoSort(boolean autoSort) {
        this.autoSort = autoSort;
        if (isEnabled()) {
            Logger.log("InventoryUtils will " + (autoSort ? "automatically sort inventory" : "not automatically sort inventory"));
        }
    }
    
    /**
     * Sets the sort delay
     * 
     * @param sortDelay The sort delay in milliseconds
     */
    public void setSortDelay(int sortDelay) {
        this.sortDelay = sortDelay;
        if (isEnabled()) {
            Logger.log("Sort delay set to: " + sortDelay + "ms");
        }
    }
    
    /**
     * Checks if auto-sort is enabled
     * 
     * @return True if auto-sort is enabled, false otherwise
     */
    public boolean isAutoSortEnabled() {
        return autoSort;
    }
    
    /**
     * Gets the sort delay
     * 
     * @return The sort delay in milliseconds
     */
    public int getSortDelay() {
        return sortDelay;
    }
}
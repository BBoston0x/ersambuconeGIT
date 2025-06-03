package com.ersambucone.modules.impl.monitoring;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;

/**
 * Analyzes farming efficiency and yield
 */
public class FarmAnalytics extends Module {
    private boolean trackYield = true;
    private String displayMode = "Simple";
    
    public FarmAnalytics() {
        super("FarmAnalytics", "Analyzes farming efficiency and yield", Category.MONITORING);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("Track yield: " + (trackYield ? "enabled" : "disabled"));
        Logger.log("Display mode: " + displayMode);
    }
    
    /**
     * Sets whether to track yield
     * 
     * @param trackYield True to track yield, false otherwise
     */
    public void setTrackYield(boolean trackYield) {
        this.trackYield = trackYield;
        if (isEnabled()) {
            Logger.log("Track yield: " + (trackYield ? "enabled" : "disabled"));
        }
    }
    
    /**
     * Sets the display mode
     * 
     * @param displayMode The display mode (Simple, Detailed, Graph)
     */
    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
        if (isEnabled()) {
            Logger.log("Display mode set to: " + displayMode);
        }
    }
    
    /**
     * Checks if yield tracking is enabled
     * 
     * @return True if yield tracking is enabled, false otherwise
     */
    public boolean isTrackYieldEnabled() {
        return trackYield;
    }
    
    /**
     * Gets the display mode
     * 
     * @return The display mode
     */
    public String getDisplayMode() {
        return displayMode;
    }
}
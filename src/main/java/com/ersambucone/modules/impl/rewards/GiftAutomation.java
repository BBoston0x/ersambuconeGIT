package com.ersambucone.modules.impl.rewards;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;

/**
 * Automates gift-related actions
 */
public class GiftAutomation extends Module {
    private boolean autoThank = true;
    private String thankMessage = "Thanks!";
    
    public GiftAutomation() {
        super("GiftAutomation", "Automates gift-related actions", Category.REWARDS);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("GiftAutomation will " + (autoThank ? "automatically thank for gifts" : "not automatically thank for gifts"));
        Logger.log("Thank message: " + thankMessage);
    }
    
    /**
     * Sets whether to automatically thank for gifts
     * 
     * @param autoThank True to automatically thank, false otherwise
     */
    public void setAutoThank(boolean autoThank) {
        this.autoThank = autoThank;
        if (isEnabled()) {
            Logger.log("GiftAutomation will " + (autoThank ? "automatically thank for gifts" : "not automatically thank for gifts"));
        }
    }
    
    /**
     * Sets the thank message
     * 
     * @param thankMessage The thank message
     */
    public void setThankMessage(String thankMessage) {
        this.thankMessage = thankMessage;
        if (isEnabled()) {
            Logger.log("Thank message set to: " + thankMessage);
        }
    }
    
    /**
     * Checks if auto-thank is enabled
     * 
     * @return True if auto-thank is enabled, false otherwise
     */
    public boolean isAutoThankEnabled() {
        return autoThank;
    }
    
    /**
     * Gets the thank message
     * 
     * @return The thank message
     */
    public String getThankMessage() {
        return thankMessage;
    }
}
package com.ersambucone.modules.impl.rewards;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;

/**
 * Tracks and notifies about available rewards
 */
public class RewardTracker extends Module {
    private boolean notifications = true;
    private boolean autoClaim = false;
    
    public RewardTracker() {
        super("RewardTracker", "Tracks and notifies about available rewards", Category.REWARDS);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("RewardTracker will " + (notifications ? "show notifications" : "not show notifications"));
        Logger.log("RewardTracker will " + (autoClaim ? "automatically claim rewards" : "not automatically claim rewards"));
    }
    
    /**
     * Sets whether notifications should be shown
     * 
     * @param notifications True to show notifications, false otherwise
     */
    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
        if (isEnabled()) {
            Logger.log("RewardTracker will " + (notifications ? "show notifications" : "not show notifications"));
        }
    }
    
    /**
     * Sets whether rewards should be automatically claimed
     * 
     * @param autoClaim True to automatically claim rewards, false otherwise
     */
    public void setAutoClaim(boolean autoClaim) {
        this.autoClaim = autoClaim;
        if (isEnabled()) {
            Logger.log("RewardTracker will " + (autoClaim ? "automatically claim rewards" : "not automatically claim rewards"));
        }
    }
    
    /**
     * Checks if notifications are enabled
     * 
     * @return True if notifications are enabled, false otherwise
     */
    public boolean isNotificationsEnabled() {
        return notifications;
    }
    
    /**
     * Checks if auto-claim is enabled
     * 
     * @return True if auto-claim is enabled, false otherwise
     */
    public boolean isAutoClaimEnabled() {
        return autoClaim;
    }
}
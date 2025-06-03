package com.ersambucone.modules.impl.utility;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;

/**
 * Logs chat messages to a file
 */
public class ChatLogger extends Module {
    private boolean logToFile = true;
    private String filterMode = "All";
    
    public ChatLogger() {
        super("ChatLogger", "Logs chat messages to a file", Category.UTILITY);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("ChatLogger will " + (logToFile ? "log to file" : "not log to file"));
        Logger.log("Filter mode: " + filterMode);
    }
    
    /**
     * Sets whether to log to a file
     * 
     * @param logToFile True to log to a file, false otherwise
     */
    public void setLogToFile(boolean logToFile) {
        this.logToFile = logToFile;
        if (isEnabled()) {
            Logger.log("ChatLogger will " + (logToFile ? "log to file" : "not log to file"));
        }
    }
    
    /**
     * Sets the filter mode
     * 
     * @param filterMode The filter mode (All, Commands, Private)
     */
    public void setFilterMode(String filterMode) {
        this.filterMode = filterMode;
        if (isEnabled()) {
            Logger.log("Filter mode set to: " + filterMode);
        }
    }
    
    /**
     * Checks if logging to file is enabled
     * 
     * @return True if logging to file is enabled, false otherwise
     */
    public boolean isLogToFileEnabled() {
        return logToFile;
    }
    
    /**
     * Gets the filter mode
     * 
     * @return The filter mode
     */
    public String getFilterMode() {
        return filterMode;
    }
}
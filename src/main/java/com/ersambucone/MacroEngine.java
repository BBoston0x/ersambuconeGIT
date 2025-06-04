package com.ersambucone;

import com.ersambucone.utils.Logger;

/**
 * Engine for handling macros and automated tasks
 */
public class MacroEngine {
    private static MacroEngine INSTANCE;
    private boolean active = false;
    
    /**
     * Creates a new MacroEngine
     */
    private MacroEngine() {
        Logger.log("Initializing Macro Engine");
    }
    
    /**
     * Gets the MacroEngine instance
     * 
     * @return The MacroEngine instance
     */
    public static MacroEngine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MacroEngine();
        }
        return INSTANCE;
    }
    
    /**
     * Checks if the macro engine is active
     * 
     * @return True if the macro engine is active
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets the active state of the macro engine
     * 
     * @param active The new active state
     */
    public void setActive(boolean active) {
        this.active = active;
        Logger.log("Macro engine " + (active ? "activated" : "deactivated"));
    }
    
    /**
     * Toggles the active state of the macro engine
     * 
     * @return The new active state
     */
    public boolean toggle() {
        active = !active;
        Logger.log("Macro engine " + (active ? "activated" : "deactivated"));
        return active;
    }
}
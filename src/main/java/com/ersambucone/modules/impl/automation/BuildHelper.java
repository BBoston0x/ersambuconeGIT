package com.ersambucone.modules.impl.automation;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;

/**
 * Helps with building structures
 */
public class BuildHelper extends Module {
    private String mode = "Circle";
    private int radius = 5;
    
    public BuildHelper() {
        super("BuildHelper", "Helps with building structures", Category.AUTOMATION);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("Build mode: " + mode);
        Logger.log("Radius: " + radius);
    }
    
    /**
     * Sets the build mode
     * 
     * @param mode The build mode (Circle, Square, Line)
     */
    public void setMode(String mode) {
        this.mode = mode;
        if (isEnabled()) {
            Logger.log("Build mode set to: " + mode);
        }
    }
    
    /**
     * Sets the radius
     * 
     * @param radius The radius (1-20)
     */
    public void setRadius(int radius) {
        this.radius = Math.max(1, Math.min(20, radius));
        if (isEnabled()) {
            Logger.log("Radius set to: " + this.radius);
        }
    }
    
    /**
     * Gets the build mode
     * 
     * @return The build mode
     */
    public String getMode() {
        return mode;
    }
    
    /**
     * Gets the radius
     * 
     * @return The radius
     */
    public int getRadius() {
        return radius;
    }
}
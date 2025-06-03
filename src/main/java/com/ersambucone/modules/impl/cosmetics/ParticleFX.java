package com.ersambucone.modules.impl.cosmetics;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;

/**
 * Adds custom particle effects to the player
 */
public class ParticleFX extends Module {
    private int density = 5;
    private String color = "Rainbow";
    
    public ParticleFX() {
        super("ParticleFX", "Adds custom particle effects to the player", Category.COSMETICS);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("Particle density: " + density);
        Logger.log("Particle color: " + color);
    }
    
    /**
     * Sets the particle density
     * 
     * @param density The particle density (1-10)
     */
    public void setDensity(int density) {
        this.density = Math.max(1, Math.min(10, density));
        if (isEnabled()) {
            Logger.log("Particle density set to: " + this.density);
        }
    }
    
    /**
     * Sets the particle color
     * 
     * @param color The particle color (Rainbow, Red, Blue, Green)
     */
    public void setColor(String color) {
        this.color = color;
        if (isEnabled()) {
            Logger.log("Particle color set to: " + color);
        }
    }
    
    /**
     * Gets the particle density
     * 
     * @return The particle density
     */
    public int getDensity() {
        return density;
    }
    
    /**
     * Gets the particle color
     * 
     * @return The particle color
     */
    public String getColor() {
        return color;
    }
}
package com.ersambucone.modules.impl.cosmetics;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;

/**
 * Provides custom capes for the player
 */
public class CapeSystem extends Module {
    private String capeStyle = "Default";
    
    public CapeSystem() {
        super("CapeSystem", "Provides custom capes for the player", Category.COSMETICS);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        Logger.log("Cape style: " + capeStyle);
    }
    
    /**
     * Sets the cape style
     * 
     * @param capeStyle The cape style (Default, Custom, Animated)
     */
    public void setCapeStyle(String capeStyle) {
        this.capeStyle = capeStyle;
        if (isEnabled()) {
            Logger.log("Cape style set to: " + capeStyle);
        }
    }
    
    /**
     * Gets the cape style
     * 
     * @return The cape style
     */
    public String getCapeStyle() {
        return capeStyle;
    }
}
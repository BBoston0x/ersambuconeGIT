package com.ersambucone.ui;

import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.modules.ModuleManager;
import com.ersambucone.utils.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the GUI and its interaction with the module system
 */
public class GuiManager {
    private static GuiManager INSTANCE;
    private DynamicHUD dynamicHUD;
    
    /**
     * Creates a new GuiManager
     */
    public GuiManager() {
        Logger.log("Initializing GUI Manager");
        this.dynamicHUD = new DynamicHUD();
    }
    
    /**
     * Updates the GUI components
     * Called every client tick
     */
    public void update() {
        // Update HUD elements if they exist
        if (dynamicHUD != null) {
            dynamicHUD.update();
        }
    }
    
    /**
     * Gets all modules organized by category
     * 
     * @return A map of categories to lists of modules
     */
    public Map<String, List<Module>> getModulesByCategory() {
        Map<String, List<Module>> result = new HashMap<>();
        
        for (Category category : Category.values()) {
            result.put(category.getName(), ModuleManager.getInstance().getModulesByCategory(category));
        }
        
        return result;
    }
    
    /**
     * Toggles a module
     * 
     * @param moduleName The name of the module to toggle
     * @return True if the module was toggled, false if it wasn't found
     */
    public boolean toggleModule(String moduleName) {
        Module module = ModuleManager.getInstance().getModule(moduleName);
        if (module != null) {
            module.toggle();
            return true;
        }
        return false;
    }
    
    /**
     * Sets a module's enabled state
     * 
     * @param moduleName The name of the module
     * @param enabled The new enabled state
     * @return True if the module was found, false otherwise
     */
    public boolean setModuleEnabled(String moduleName, boolean enabled) {
        Module module = ModuleManager.getInstance().getModule(moduleName);
        if (module != null) {
            module.setEnabled(enabled);
            return true;
        }
        return false;
    }
    
    /**
     * Gets the GuiManager instance
     * 
     * @return The GuiManager instance
     */
    public static GuiManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GuiManager();
        }
        return INSTANCE;
    }
}
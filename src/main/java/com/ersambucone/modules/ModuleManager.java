package com.ersambucone.modules;

import com.ersambucone.modules.impl.automation.BuildHelper;
import com.ersambucone.modules.impl.automation.MacroEngine;
import com.ersambucone.modules.impl.automation.SmartClicker;
import com.ersambucone.modules.impl.cosmetics.CapeSystem;
import com.ersambucone.modules.impl.cosmetics.ParticleFX;
import com.ersambucone.modules.impl.monitoring.FarmAnalytics;
import com.ersambucone.modules.impl.monitoring.StaffRadar;
import com.ersambucone.modules.impl.rewards.GiftAutomation;
import com.ersambucone.modules.impl.rewards.RewardTracker;
import com.ersambucone.modules.impl.utility.ChatLogger;
import com.ersambucone.modules.impl.utility.CommandSystem;
import com.ersambucone.modules.impl.utility.InventoryUtils;
import com.ersambucone.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.ersambucone.events.EventBus;
import com.ersambucone.events.EventListener;

/**
 * Manages all modules in the client
 */
public class ModuleManager {
    private static ModuleManager INSTANCE;
    
    private final List<Module> modules = new ArrayList<>();
    private final Map<Category, List<Module>> modulesByCategory = new HashMap<>();
    // Module name cache for faster lookups - using ConcurrentHashMap for thread safety
    private final Map<String, Module> modulesByName = new ConcurrentHashMap<>();
    
    /**
     * Creates a new ModuleManager and registers all modules
     */
    private ModuleManager() {
        // Initialize category lists
        for (Category category : Category.values()) {
            modulesByCategory.put(category, new ArrayList<>());
        }
        
        // Register modules
        registerModules();
        
        Logger.log("Registered " + modules.size() + " modules");
    }
    
    /**
     * Registers all modules
     */
    private void registerModules() {
        Logger.log("Registering modules...");
        
        try {
            // Rewards modules
            register(new RewardTracker());
            register(new GiftAutomation());
            
            // Utility modules
            register(new ChatLogger());
            register(new CommandSystem());
            register(new InventoryUtils());
            
            // Cosmetics modules
            register(new CapeSystem());
            register(new ParticleFX());
            
            // Automation modules
            register(new BuildHelper());
            register(new MacroEngine());
            register(new SmartClicker());
            
            // Monitoring modules
            register(new StaffRadar());
            register(new FarmAnalytics());
            
            // Register any additional modules here
            
            Logger.log("Successfully registered " + modules.size() + " modules");
        } catch (Exception e) {
            Logger.error("Error registering modules: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Registers a module
     * 
     * @param module The module to register
     */
    private void register(Module module) {
        try {
            // Add to main module list
            modules.add(module);
            
            // Add to category map
            modulesByCategory.get(module.getCategory()).add(module);
            
            // Add to name cache for faster lookups
            modulesByName.put(module.getName().toLowerCase(), module);
            
            // Register with EventBus if it's an event listener
            if (module instanceof EventListener) {
                EventBus.register((EventListener) module);
            }
            
            if (Logger.isDebugEnabled()) {
                Logger.debug("Registered module: " + module.getName());
            }
        } catch (Exception e) {
            Logger.error("Failed to register module " + module.getName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Gets a module by name
     * 
     * @param name The name of the module
     * @return The module, or null if not found
     */
    public Module getModule(String name) {
        // Use case-insensitive lookup with cache
        String lowerName = name.toLowerCase();
        
        // Check cache first
        Module cachedModule = modulesByName.get(lowerName);
        if (cachedModule != null) {
            return cachedModule;
        }
        
        // Cache miss, do a full lookup and update cache
        for (Module module : modules) {
            if (module.getName().toLowerCase().equals(lowerName)) {
                // Add to cache for future lookups
                modulesByName.put(lowerName, module);
                return module;
            }
        }
        return null;
    }
    
    /**
     * Gets all modules
     * 
     * @return A list of all modules
     */
    public List<Module> getModules() {
        return modules;
    }
    
    /**
     * Gets all modules in a category
     * 
     * @param category The category
     * @return A list of modules in the category
     */
    public List<Module> getModulesByCategory(Category category) {
        return modulesByCategory.get(category);
    }
    
    /**
     * Gets the ModuleManager instance
     * 
     * @return The ModuleManager instance
     */
    public static ModuleManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModuleManager();
        }
        return INSTANCE;
    }
}
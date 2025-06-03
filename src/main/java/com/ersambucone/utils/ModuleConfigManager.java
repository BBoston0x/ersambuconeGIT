package com.ersambucone.utils;

import com.ersambucone.modules.Module;
import com.ersambucone.modules.ModuleManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Manages module configuration files for the client
 */
public class ModuleConfigManager {
    private static ModuleConfigManager INSTANCE;
    
    private final File configDir;
    private final File modulesFile;
    
    /**
     * Creates a new ModuleConfigManager
     */
    private ModuleConfigManager() {
        // Create config directory in .minecraft/ersambucone
        configDir = new File("ersambucone");
        if (!configDir.exists() && !configDir.mkdirs()) {
            Logger.log("Failed to create config directory");
        }
        
        // Create modules config file
        modulesFile = new File(configDir, "modules.properties");
        if (!modulesFile.exists()) {
            try {
                if (!modulesFile.createNewFile()) {
                    Logger.log("Failed to create modules config file");
                }
            } catch (IOException e) {
                Logger.log("Failed to create modules config file: " + e.getMessage());
            }
        }
        
        Logger.log("Config manager initialized");
    }
    
    /**
     * Saves the current module states to the config file
     */
    public void saveModuleStates() {
        Properties properties = new Properties();
        
        // Save module enabled states
        for (Module module : ModuleManager.getInstance().getModules()) {
            properties.setProperty(module.getName() + ".enabled", String.valueOf(module.isEnabled()));
        }
        
        // Write to file
        try (FileWriter writer = new FileWriter(modulesFile)) {
            properties.store(writer, "Er Sambucone Client Module Configuration");
            Logger.log("Saved module states to config file");
        } catch (IOException e) {
            Logger.log("Failed to save module states: " + e.getMessage());
        }
    }
    
    /**
     * Loads module states from the config file
     */
    public void loadModuleStates() {
        if (!modulesFile.exists()) {
            Logger.log("Modules config file does not exist, skipping load");
            return;
        }
        
        Properties properties = new Properties();
        
        // Read from file
        try (FileReader reader = new FileReader(modulesFile)) {
            properties.load(reader);
            
            // Load module enabled states
            for (Module module : ModuleManager.getInstance().getModules()) {
                String enabledStr = properties.getProperty(module.getName() + ".enabled");
                if (enabledStr != null) {
                    boolean enabled = Boolean.parseBoolean(enabledStr);
                    module.setEnabled(enabled);
                }
            }
            
            Logger.log("Loaded module states from config file");
        } catch (IOException e) {
            Logger.log("Failed to load module states: " + e.getMessage());
        }
    }
    
    /**
     * Gets the ModuleConfigManager instance
     * 
     * @return The ModuleConfigManager instance
     */
    public static ModuleConfigManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModuleConfigManager();
        }
        return INSTANCE;
    }
}
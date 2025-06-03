package com.ersambucone.modules;

import com.ersambucone.events.EventBus;
import com.ersambucone.events.EventListener;
import com.ersambucone.events.ModuleToggleEvent;
import com.ersambucone.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Optimized base class for all modules in the Er Sambucone Client
 * Includes event handling and settings management
 */
public abstract class Module implements EventListener {
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    
    // Settings system
    private final Map<String, Setting<?>> settings = new HashMap<>();
    private final List<String> settingsOrder = new ArrayList<>();
    
    // Performance optimization - avoid excessive logging
    private static final boolean VERBOSE_LOGGING = false;
    
    /**
     * Creates a new module
     * 
     * @param name The name of the module
     * @param description A short description of what the module does
     * @param category The category this module belongs to
     */
    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
    }
    
    /**
     * Called when the module is enabled
     */
    public void onEnable() {
        if (VERBOSE_LOGGING) {
            Logger.log("Module " + name + " enabled");
        }
        
        // Register for events when enabled
        EventBus.register(this);
        
        // Fire module toggle event
        EventBus.fire(new ModuleToggleEvent(this, true));
    }
    
    /**
     * Called when the module is disabled
     */
    public void onDisable() {
        if (VERBOSE_LOGGING) {
            Logger.log("Module " + name + " disabled");
        }
        
        // Unregister from events when disabled to reduce overhead
        EventBus.unregister(this);
        
        // Fire module toggle event
        EventBus.fire(new ModuleToggleEvent(this, false));
    }
    
    /**
     * Toggles the module on or off
     */
    public void toggle() {
        setEnabled(!this.enabled);
    }
    
    /**
     * Sets the enabled state of the module
     * 
     * @param enabled The new enabled state
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }
    
    /**
     * Gets the name of the module
     * 
     * @return The module name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the description of the module
     * 
     * @return The module description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the category of the module
     * 
     * @return The module category
     */
    public Category getCategory() {
        return category;
    }
    
    /**
     * Checks if the module is enabled
     * 
     * @return True if the module is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Handle events - override in subclasses
     */
    @Override
    public void onEvent(Object event) {
        // Default implementation does nothing
    }
    
    /**
     * Add a setting to this module
     * 
     * @param name Setting name
     * @param defaultValue Default value
     * @param <T> Setting type
     * @return The created setting
     */
    protected <T> Setting<T> addSetting(String name, T defaultValue) {
        Setting<T> setting = new Setting<>(name, defaultValue);
        settings.put(name.toLowerCase(), setting);
        settingsOrder.add(name);
        return setting;
    }
    
    /**
     * Get a setting by name
     * 
     * @param name Setting name
     * @param <T> Setting type
     * @return The setting, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> Setting<T> getSetting(String name) {
        return (Setting<T>) settings.get(name.toLowerCase());
    }
    
    /**
     * Get all settings in order
     * 
     * @return List of settings
     */
    public List<Setting<?>> getSettings() {
        if (settings.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        List<Setting<?>> result = new ArrayList<>(settingsOrder.size());
        for (String name : settingsOrder) {
            result.add(settings.get(name.toLowerCase()));
        }
        return result;
    }
    
    /**
     * Check if this module has any settings
     * 
     * @return True if the module has settings, false otherwise
     */
    public boolean hasSettings() {
        return !settings.isEmpty();
    }
    
    /**
     * Setting class for module configuration
     */
    public static class Setting<T> {
        private final String name;
        private T value;
        
        public Setting(String name, T defaultValue) {
            this.name = name;
            this.value = defaultValue;
        }
        
        public String getName() {
            return name;
        }
        
        public T getValue() {
            return value;
        }
        
        public void setValue(T value) {
            this.value = value;
        }
    }
}
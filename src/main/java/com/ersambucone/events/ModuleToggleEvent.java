package com.ersambucone.events;

import com.ersambucone.modules.Module;

/**
 * Event fired when a module is toggled
 */
public class ModuleToggleEvent {
    private final Module module;
    private final boolean enabled;
    
    public ModuleToggleEvent(Module module, boolean enabled) {
        this.module = module;
        this.enabled = enabled;
    }
    
    /**
     * Gets the module that was toggled
     * 
     * @return The module
     */
    public Module getModule() {
        return module;
    }
    
    /**
     * Checks if the module was enabled or disabled
     * 
     * @return True if the module was enabled, false if it was disabled
     */
    public boolean isEnabled() {
        return enabled;
    }
}
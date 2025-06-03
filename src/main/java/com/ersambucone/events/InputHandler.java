package com.ersambucone.events;

import com.ersambucone.config.KeybindManager;
import com.ersambucone.ui.ErSambuconeGUI;
import com.ersambucone.utils.Logger;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

/**
 * Optimized input handler with debouncing and performance improvements
 */
public class InputHandler implements EventListener {
    private boolean initialized = false;
    
    // Debounce timers to prevent key spam
    private long lastMacroToggle = 0;
    private long lastGuiToggle = 0;
    private static final long KEY_DEBOUNCE_MS = 200; // 200ms debounce
    
    // Performance optimization - skip ticks when FPS is low
    private int tickCounter = 0;
    private static final int TICK_INTERVAL = 1; // Process every tick by default
    
    public InputHandler() {
        // Register to client tick events to check key presses
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        Logger.debug("Input handler initialized");
    }
    
    private void onClientTick(MinecraftClient client) {
        // Skip processing on some ticks for better performance
        tickCounter = (tickCounter + 1) % TICK_INTERVAL;
        if (tickCounter != 0) return;
        
        // Skip if player is null or game is paused
        if (client.player == null || client.isPaused()) return;
        
        long currentTime = System.currentTimeMillis();
        
        // Check for macro toggle with debounce
        if (KeybindManager.MACRO_TOGGLE.wasPressed() && 
            currentTime - lastMacroToggle > KEY_DEBOUNCE_MS) {
            
            lastMacroToggle = currentTime;
            Logger.debug("Macro toggle key pressed");
            EventBus.fire(new MacroToggleEvent());
        }
        
        // Check for ClickGui toggle with debounce
        if (KeybindManager.CLICKGUI_TOGGLE.wasPressed() && 
            currentTime - lastGuiToggle > KEY_DEBOUNCE_MS) {
            
            lastGuiToggle = currentTime;
            Logger.debug("ClickGui toggle key pressed");
            
            // Only open GUI if not already open
            if (!(client.currentScreen instanceof ErSambuconeGUI)) {
                client.setScreen(new ErSambuconeGUI());
            }
        }
    }
    
    @Override
    public void onEvent(Object event) {
        // Handle other events here if needed
    }
    
    /**
     * Event fired when the macro toggle key is pressed
     */
    public static class MacroToggleEvent {
        private final long timestamp;
        
        public MacroToggleEvent() {
            this.timestamp = System.currentTimeMillis();
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
}
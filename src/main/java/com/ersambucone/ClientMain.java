package com.ersambucone;

import com.ersambucone.config.KeybindManager;
import com.ersambucone.events.EventBus;
import com.ersambucone.events.EventListener;
import com.ersambucone.events.InputHandler;
import com.ersambucone.events.ModuleToggleEvent;
import com.ersambucone.modules.ModuleManager;
import com.ersambucone.modules.Module;
import com.ersambucone.modules.impl.automation.MacroEngine;
import com.ersambucone.ui.DynamicHUD;
import com.ersambucone.utils.Logger;
import com.ersambucone.utils.ModuleConfigManager;
import com.ersambucone.utils.PerformanceMonitor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main client class with optimized initialization and performance monitoring
 */
public class ClientMain implements ClientModInitializer, EventListener {
    private static ClientMain instance;
    private MacroEngine macroEngine;
    private DynamicHUD dynamicHUD;
    private PerformanceMonitor performanceMonitor;
    
    // Thread pool for background tasks with proper shutdown
    private final Executor backgroundExecutor = Executors.newFixedThreadPool(2);
    
    // Client version
    public static final String VERSION = "1.0.1";

    @Override
    public void onInitializeClient() {
        long startTime = System.currentTimeMillis();
        instance = this;
        
        Logger.log("Initializing Er Sambucone Client v" + VERSION);
        
        // Initialize performance monitor
        performanceMonitor = new PerformanceMonitor();
        
        // Initialize ModuleManager first
        ModuleManager.getInstance();
        
        // Load module configurations
        ModuleConfigManager.getInstance().loadModuleStates();
        
        // Initialize core components
        macroEngine = new MacroEngine();
        dynamicHUD = new DynamicHUD(MinecraftClient.getInstance());

        // Register keybindings
        KeyBindingHelper.registerKeyBinding(KeybindManager.MACRO_TOGGLE);
        KeyBindingHelper.registerKeyBinding(KeybindManager.CLICKGUI_TOGGLE);

        // Register event handlers
        EventBus.register(new InputHandler());
        EventBus.register(this);
        
        // Initialize config directories in background
        CompletableFuture.runAsync(() -> {
            try {
                Files.createDirectories(Path.of("config/macros"));
                // The new MacroEngine loads default macros in its constructor
                Logger.log("Macros directory created successfully");
            } catch (Exception e) {
                Logger.log("Error creating config directories: " + e.getMessage());
            }
        }, backgroundExecutor);
        
        // Register HUD
        dynamicHUD.register();
        
        // Register shutdown hook
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            Logger.log("Shutting down Er Sambucone Client...");
            saveAllConfigs();
        });
        
        long initTime = System.currentTimeMillis() - startTime;
        Logger.log("Client initialized in " + initTime + "ms");
    }
    
    /**
     * Save all configurations on shutdown
     */
    private void saveAllConfigs() {
        try {
            // Save module configurations
            Logger.log("Saving configurations...");
            ModuleConfigManager.getInstance().saveModuleStates();
            
            // Shutdown thread pool gracefully
            if (backgroundExecutor instanceof java.util.concurrent.ExecutorService) {
                java.util.concurrent.ExecutorService executorService = (java.util.concurrent.ExecutorService) backgroundExecutor;
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            Logger.log("Error saving configurations: " + e.getMessage());
        }
    }
    
    /**
     * Event handler for module toggle events
     */
    @Override
    public void onEvent(Object event) {
        if (event instanceof ModuleToggleEvent) {
            ModuleToggleEvent toggleEvent = (ModuleToggleEvent) event;
            Module module = toggleEvent.getModule();
            
            // Update performance monitoring
            if (toggleEvent.isEnabled()) {
                performanceMonitor.trackModuleEnabled(module.getName());
            } else {
                performanceMonitor.trackModuleDisabled(module.getName());
            }
        }
    }

    public static ClientMain getInstance() {
        return instance;
    }

    public MacroEngine getMacroEngine() {
        return macroEngine;
    }

    public DynamicHUD getDynamicHUD() {
        return dynamicHUD;
    }
    
    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }
}
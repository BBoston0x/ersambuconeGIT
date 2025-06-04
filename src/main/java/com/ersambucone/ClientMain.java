package com.ersambucone;

import com.ersambucone.ai.SkyblockLearner;
import com.ersambucone.config.KeybindManager;
import com.ersambucone.events.EventBus;
import com.ersambucone.events.EventListener;
import com.ersambucone.events.InputHandler;
import com.ersambucone.events.ModuleToggleEvent;
import com.ersambucone.modules.ModuleManager;
import com.ersambucone.modules.Module;
import com.ersambucone.modules.impl.automation.MacroEngine;
import com.ersambucone.modules.impl.monitoring.FarmOptimizer;
import com.ersambucone.modules.impl.monitoring.SmartAuctionWatcher;
import com.ersambucone.ui.DynamicHUD;
import com.ersambucone.utils.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;

/**
 * Client main class with full AI integration
 * Version: 2.0.0
 */
public class ClientMain implements ClientModInitializer, EventListener {
    // Singleton instance
    private static ClientMain instance;
    
    // Core systems
    private final Executor backgroundExecutor = Executors.newFixedThreadPool(2);
    private MacroEngine macroEngine;
    private DynamicHUD dynamicHUD;
    private PerformanceMonitor performanceMonitor;
    private SkyblockLearner skyblockLearner; // AI System
    
    // Configuration
    public static final String VERSION = "2.0.0";
    private static final long AI_INIT_DELAY = 30_000; // 30s delay for AI init

    @Override
    public void onInitializeClient() {
        long startTime = System.currentTimeMillis();
        instance = this;
        
        initLoggingSystem();
        initCoreSystems();
        registerEventHandlers();
        scheduleBackgroundTasks();
        
        Logger.log("Client initialized in " + (System.currentTimeMillis() - startTime) + "ms");
    }

    // ==================== INITIALIZATION METHODS ====================

    private void initLoggingSystem() {
        Logger.log("=== Starting ErSambucone Client v" + VERSION + " ===");
        Logger.log("Initializing systems...");
    }

    private void initCoreSystems() {
        // 1. Performance monitoring
        this.performanceMonitor = new PerformanceMonitor();
        
        // 2. Module system
        ModuleManager moduleManager = ModuleManager.getInstance();
        registerAiModules(moduleManager);
        
        // 3. UI and input
        this.dynamicHUD = new DynamicHUD(MinecraftClient.getInstance());
        KeyBindingHelper.registerKeyBinding(KeybindManager.MACRO_TOGGLE);
        KeyBindingHelper.registerKeyBinding(KeybindManager.CLICKGUI_TOGGLE);
        
        // 4. AI System (delayed initialization)
        scheduleAiInitialization();
    }

    private void registerAiModules(ModuleManager moduleManager) {
        moduleManager.registerModule(new SmartAuctionWatcher());
        moduleManager.registerModule(new FarmOptimizer());
        ModuleConfigManager.getInstance().loadModuleStates();
    }

    private void scheduleAiInitialization() {
        backgroundExecutor.execute(() -> {
            try {
                // Wait for other systems to stabilize
                Thread.sleep(AI_INIT_DELAY);
                
                // Initialize AI
                this.skyblockLearner = new SkyblockLearner();
                Files.createDirectories(Path.of("config/ai"));
                skyblockLearner.init();
                
                Logger.log("AI system initialized successfully");
            } catch (Exception e) {
                Logger.log("AI initialization failed: " + e.getMessage());
            }
        });
    }

    private void registerEventHandlers() {
        EventBus.register(new InputHandler());
        EventBus.register(this);
        ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientShutdown);
    }

    private void scheduleBackgroundTasks() {
        // Macro engine initialization
        backgroundExecutor.execute(() -> {
            this.macroEngine = new MacroEngine();
            ensureConfigDirectoriesExist();
        });
        
        // HUD registration
        CompletableFuture.runAsync(dynamicHUD::register);
    }

    private void ensureConfigDirectoriesExist() {
        try {
            Files.createDirectories(Path.of("config/macros"));
            Logger.log("Config directories ready");
        } catch (Exception e) {
            Logger.log("Config directory error: " + e.getMessage());
        }
    }

    // ==================== SHUTDOWN HANDLING ====================

    private void onClientShutdown(MinecraftClient client) {
        Logger.log("=== Shutting down client ===");
        
        // 1. Save all configurations
        saveAllConfigs();
        
        // 2. Shutdown AI properly
        if (skyblockLearner != null) {
            skyblockLearner.shutdown();
        }
        
        // 3. Stop background tasks
        shutdownExecutor();
    }

    private void saveAllConfigs() {
        try {
            ModuleConfigManager.getInstance().saveModuleStates();
            if (skyblockLearner != null) {
                skyblockLearner.saveModel();
            }
            Logger.log("All configurations saved");
        } catch (Exception e) {
            Logger.log("Config save error: " + e.getMessage());
        }
    }

    private void shutdownExecutor() {
        if (backgroundExecutor instanceof ExecutorService) {
            ExecutorService service = (ExecutorService) backgroundExecutor;
            service.shutdown();
            try {
                if (!service.awaitTermination(3, TimeUnit.SECONDS)) {
                    service.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ==================== EVENT HANDLING ====================

    @Override
    public void onEvent(Object event) {
        if (event instanceof ModuleToggleEvent) {
            handleModuleToggle((ModuleToggleEvent) event);
        }
    }

    private void handleModuleToggle(ModuleToggleEvent event) {
        Module module = event.getModule();
        String action = event.isEnabled() ? "Enabled" : "Disabled";
        
        performanceMonitor.trackModuleState(module.getName(), event.isEnabled());
        Logger.log(action + " module: " + module.getName());
        
        // Special handling for AI modules
        if (module instanceof SmartAuctionWatcher && skyblockLearner != null) {
            if (event.isEnabled()) {
                skyblockLearner.enableAuctionScanning();
            } else {
                skyblockLearner.disableAuctionScanning();
            }
        }
    }

    // ==================== GETTERS ====================

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

    public SkyblockLearner getSkyblockLearner() {
        return skyblockLearner;
    }
}
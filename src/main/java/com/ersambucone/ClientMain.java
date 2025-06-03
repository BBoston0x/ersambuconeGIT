package com.ersambucone;

import com.ersambucone.events.EventBus;
import com.ersambucone.modules.automation.MacroEngine;
import com.ersambucone.ui.DynamicHUD;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class ClientMain implements ClientModInitializer {
    private static ClientMain instance;
    private EventBus eventBus;
    private MacroEngine macroEngine;
    private DynamicHUD dynamicHUD;

    @Override
    public void onInitializeClient() {
        instance = this;
        eventBus = new EventBus();
        macroEngine = new MacroEngine();
        dynamicHUD = new DynamicHUD(MinecraftClient.getInstance());

        Logger.log("Client avviato!");
        eventBus.register(new InputHandler());
        macroEngine.loadMacros();
        dynamicHUD.register(); // Esempio di registrazione HUD

        // TODO: Initialize other components and register event listeners
    }

    public static ClientMain getInstance() {
        return instance;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public MacroEngine getMacroEngine() {
        return macroEngine;
    }

    public DynamicHUD getDynamicHUD() {
        return dynamicHUD;
    }
}
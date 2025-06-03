package com.ersambucone.events;

import net.minecraft.client.MinecraftClient;

public class InputHandler {
    public static void handleKeyInput(MinecraftClient client) {
        if (KeybindManager.MACRO_TOGGLE.wasPressed()) {
            EventBus.fire(new MacroToggleEvent());
        }
    }
}
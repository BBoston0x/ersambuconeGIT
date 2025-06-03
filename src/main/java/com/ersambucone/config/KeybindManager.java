package com.ersambucone.config;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KeybindManager {
    public static KeyBinding MACRO_TOGGLE = new KeyBinding(
            "key.ersambucone.macro_toggle",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_R,
            "category.ersambucone"
    );
}
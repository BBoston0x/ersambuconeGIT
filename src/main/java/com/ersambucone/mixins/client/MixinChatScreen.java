package com.ersambucone.mixins.client;

import com.ersambucone.ClientMain;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class MixinChatScreen {
    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String message, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {
        if (message.startsWith("/")) {
            if (ClientMain.getInstance().getCommandSystem().executeCommand(message.substring(1))) {
                cir.setReturnValue(true);
            }
        }
    }
}
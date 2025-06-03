package com.ersambucone.mixins.world;

import net.minecraft.server.network.ServerEntityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntityTracker.class)
public class MixinEntityTracker {

    @Inject(method = "updateTrackedEntity", at = @At("HEAD"))
    private void onEntityUpdate(CallbackInfo ci) {
        // Inserisci qui il tuo codice di mixin
    }
}

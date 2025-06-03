package com.ersambucone.mixins;

@Mixin(EntityTracker.class)
public class MixinEntityTracker {
    @Inject(method = "updateTrackedEntity", at = @At("HEAD"))
    private void onEntityUpdate(CallbackInfo ci) {
        StaffRadar.checkForStaff(); // Integrazione diretta
    }
}
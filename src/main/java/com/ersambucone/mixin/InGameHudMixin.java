package com.ersambucone.mixin;

import com.ersambucone.events.EventBus;
import com.ersambucone.events.RenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Optimized mixin for rendering the HUD
 * Uses frame limiting to reduce performance impact
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    // Last render time for FPS-based limiting
    private long lastRenderTime = 0;
    private static final long MIN_RENDER_INTERVAL_MS = 33; // ~30 FPS for HUD updates (smoother appearance)
    
    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        // Limit by time for consistent HUD updates
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRenderTime < MIN_RENDER_INTERVAL_MS) {
            return;
        }
        lastRenderTime = currentTime;
        
        // Only render HUD if game is not paused
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isPaused()) {
            return;
        }
        
        // Extract MatrixStack from DrawContext for backward compatibility
        MatrixStack matrices = context.getMatrices();
        EventBus.fire(new RenderEvent(matrices));
    }
}
package com.ersambucone.ui;

import com.ersambucone.events.EventBus;
import com.ersambucone.events.EventListener;
import com.ersambucone.events.RenderEvent;
import com.ersambucone.utils.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class DynamicHUD implements EventListener {
    private MinecraftClient client;
    private boolean isRegistered = false;

    public DynamicHUD() {
        this.client = MinecraftClient.getInstance();
        register();
    }
    
    /**
     * Updates the HUD state
     * Called every client tick
     */
    public void update() {
        // This method is called every tick to update any dynamic HUD elements
        // Most of the actual updates happen in the render method with timers
        
        // Make sure we're registered to receive render events
        if (!isRegistered) {
            register();
        }
    }

    // Cache for HUD elements to avoid string creation every frame
    private Text clientVersionText = Text.literal("§6ErSambucone Client §f1.0.0");
    private Text keybindHelpText = Text.literal("§7Press §fR §7to toggle macro | §fRSHIFT §7for GUI");
    private Text coordsText = Text.literal("");
    private Text fpsText = Text.literal("");
    private Text macroText = Text.literal("");
    
    // Update timers for different elements
    private long lastCoordUpdate = 0;
    private long lastFpsUpdate = 0;
    private long lastMacroUpdate = 0;
    
    // Update intervals (milliseconds)
    private static final int COORDS_UPDATE_INTERVAL = 200; // Update coords every 200ms
    private static final int FPS_UPDATE_INTERVAL = 500;    // Update FPS every 500ms
    private static final int MACRO_UPDATE_INTERVAL = 300;  // Update macro status every 300ms
    
    public void render(MatrixStack matrixStack) {
        if (client.player == null) return;
        
        long currentTime = System.currentTimeMillis();
        
        // Create a DrawContext for rendering with the provided MatrixStack
        DrawContext context = new DrawContext(client, client.getBufferBuilders().getEntityVertexConsumers());
        context.getMatrices().push(); // Save the current matrix state
        context.getMatrices().loadIdentity(); // Reset to identity matrix
        context.getMatrices().multiplyPositionMatrix(matrixStack.peek().getPositionMatrix()); // Apply the provided matrix
        
        // Draw semi-transparent background for better readability
        context.fill(2, 2, 200, 50, 0x80000000);
        
        // Display mod name and version (static text)
        context.drawTextWithShadow(client.textRenderer, clientVersionText, 5, 5, 0xFFFFFF);
        
        // Update coordinates periodically instead of every frame
        if (currentTime - lastCoordUpdate > COORDS_UPDATE_INTERVAL) {
            coordsText = Text.literal(String.format("§aX: §f%.1f §aY: §f%.1f §aZ: §f%.1f", 
                    client.player.getX(), client.player.getY(), client.player.getZ()));
            lastCoordUpdate = currentTime;
        }
        context.drawTextWithShadow(client.textRenderer, coordsText, 5, 15, 0xFFFFFF);
        
        // Update FPS periodically
        if (currentTime - lastFpsUpdate > FPS_UPDATE_INTERVAL) {
            try {
                String fpsRaw = MinecraftClient.getInstance().fpsDebugString.split(" ")[0];
                fpsText = Text.literal("§aFPS: §f" + fpsRaw);
            } catch (Exception e) {
                fpsText = Text.literal("§aFPS: §fN/A");
            }
            lastFpsUpdate = currentTime;
        }
        context.drawTextWithShadow(client.textRenderer, fpsText, 5, 25, 0xFFFFFF);
        
        // Update macro status periodically
        if (currentTime - lastMacroUpdate > MACRO_UPDATE_INTERVAL) {
            boolean macroActive = false;
            try {
                macroActive = com.ersambucone.ClientMain.getInstance().getMacroEngine().isActive();
            } catch (Exception e) {
                // Fallback if there's an issue
            }
            macroText = Text.literal(macroActive ? "§aMacro: §fActive" : "§cMacro: §fInactive");
            lastMacroUpdate = currentTime;
        }
        context.drawTextWithShadow(client.textRenderer, macroText, 5, 35, 0xFFFFFF);
        
        // Display keybind help (static text)
        context.drawTextWithShadow(client.textRenderer, keybindHelpText, 5, 45, 0xFFFFFF);
        
        context.getMatrices().pop(); // Restore the matrix state
    }
    
    public void register() {
        if (!isRegistered) {
            EventBus.register(this);
            isRegistered = true;
            Logger.log("HUD registered successfully");
        }
    }
    
    @Override
    public void onEvent(Object event) {
        if (event instanceof RenderEvent) {
            render(((RenderEvent) event).getMatrixStack());
        }
    }
}
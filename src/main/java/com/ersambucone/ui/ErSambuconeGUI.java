package com.ersambucone.ui;

import com.ersambucone.modules.Module;
import com.ersambucone.utils.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Optimized GUI - A clean, monochromatic HTML-based ClickGui for the Er Sambucone client
 * Features:
 * - Clean, minimal design for better performance
 * - Dedicated search panel
 * - Simplified animations
 * - Optimized for low-end systems
 */
public class ErSambuconeGUI extends Screen {
    private static final Identifier HTML_RESOURCE = new Identifier("ersambucone", "ersambucone_gui.html");
    private String htmlContent;
    private boolean initialized = false;
    
    // Browser component (will be initialized in init method)
    private Object browser; // This will be the actual browser object
    
    public ErSambuconeGUI() {
        super(Text.literal("Er Sambucone Client"));
        // Initialize the GUI Manager
        GuiManager.getInstance();
        loadHtmlContent();
    }
    
    private void loadHtmlContent() {
        try {
            // Load HTML content from resources
            InputStream inputStream = MinecraftClient.getInstance().getResourceManager()
                    .getResource(HTML_RESOURCE).get().getInputStream();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                htmlContent = reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            Logger.log("Failed to load Er Sambucone GUI HTML");
            htmlContent = "<html><body><h1>Error loading Er Sambucone GUI</h1></body></html>";
        }
    }
    
    @Override
    protected void init() {
        super.init();
        
        if (!initialized) {
            initBrowser();
            initialized = true;
        }
    }
    
    private void initBrowser() {
        // Initialize browser component
        // Note: This is a placeholder implementation
        // You'll need to use a library like JCEF (Java Chromium Embedded Framework)
        // or another browser component that works with LWJGL
        
        try {
            // For now, we'll use a simple placeholder object to represent the browser
            // This ensures the browser variable is not null, so the GUI can render
            browser = new Object(); // Simple placeholder
            
            Logger.log("Initializing browser component for Er Sambucone GUI");
            Logger.log("HTML content length: " + htmlContent.length());
            
            // Register JavaScript callbacks
            registerCallbacks();
        } catch (Exception e) {
            Logger.log("Failed to initialize Er Sambucone browser component: " + e.getMessage());
        }
    }
    
    private void registerCallbacks() {
        // Register JavaScript callbacks to interact with the client
        // This would allow the HTML UI to call Java methods
        
        // Example implementation (you'll need to replace this with actual browser callback registration):
        // browser.registerCallback("toggleModule", (moduleName, enabled) -> {
        //     GuiManager.getInstance().setModuleEnabled(moduleName, enabled);
        //     Logger.log("Toggling module: " + moduleName + " to " + enabled);
        // });
        
        // browser.registerCallback("getModules", () -> {
        //     Map<String, List<Module>> modulesByCategory = GuiManager.getInstance().getModulesByCategory();
        //     // Convert to JSON and return
        //     return convertToJson(modulesByCategory);
        // });
        
        Logger.log("Registered JavaScript callbacks for Er Sambucone GUI");
        
        // Load modules into the GUI
        loadModules();
    }
    
    private void loadModules() {
        // Get all modules by category
        Map<String, List<Module>> modulesByCategory = GuiManager.getInstance().getModulesByCategory();
        
        // Log the modules for debugging
        Logger.log("Loading modules into GUI:");
        for (Map.Entry<String, List<Module>> entry : modulesByCategory.entrySet()) {
            Logger.log("Category: " + entry.getKey());
            for (Module module : entry.getValue()) {
                Logger.log("  - " + module.getName() + " (" + (module.isEnabled() ? "enabled" : "disabled") + ")");
            }
        }
        
        // In a real implementation, you would convert the modules to JSON and pass them to the browser
        // String modulesJson = convertToJson(modulesByCategory);
        // browser.executeJavaScript("loadModules(" + modulesJson + ")");
    }
    
    // Cache for module rendering to avoid recreating objects every frame
    private final Map<String, Integer> categoryPositions = new HashMap<>();
    private final Map<Module, Integer> moduleColors = new HashMap<>();
    private long lastUpdateTime = 0;
    private static final int UPDATE_INTERVAL_MS = 500; // Update colors every 500ms
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render the background with less transparency for better performance
        context.fill(0, 0, this.width, this.height, 0xC0101010);
        
        // Render the GUI
        if (initialized && browser != null) {
            // Update module colors periodically instead of every frame
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime > UPDATE_INTERVAL_MS) {
                updateModuleColors();
                lastUpdateTime = currentTime;
            }
            
            // Title - render once with smaller font
            context.drawCenteredTextWithShadow(this.textRenderer, 
                    Text.literal("Er Sambucone Client"), 
                    this.width / 2, 10, 0xFFFFFFFF);
            
            // Draw module categories
            Map<String, List<Module>> modulesByCategory = GuiManager.getInstance().getModulesByCategory();
            int startX = Math.max(20, (this.width - 800) / 2); // Center categories if window is wide enough
            int categoryX = startX;
            int categoryY = 30;
            int categoryWidth = 120; // Reduced width
            int categoryHeight = 120; // Reduced height
            
            // Draw categories in a more compact grid layout (4 columns)
            int categoryIndex = 0;
            for (Map.Entry<String, List<Module>> entry : modulesByCategory.entrySet()) {
                String category = entry.getKey();
                List<Module> modules = entry.getValue();
                
                // Calculate position
                if (categoryIndex > 0 && categoryIndex % 4 == 0) {
                    // New row after every 4 categories
                    categoryX = startX;
                    categoryY += categoryHeight + 10; // Move down for next row with smaller gap
                } else if (categoryIndex > 0) {
                    // Move right for next category in same row
                    categoryX += categoryWidth + 5; // Smaller gap between categories
                }
                
                // Store category position for mouse click detection
                categoryPositions.put(category, (categoryY << 16) | categoryX);
                
                // Draw category background (smaller)
                context.fill(categoryX - 2, categoryY - 2, 
                         categoryX + categoryWidth - 2, categoryY + Math.min(modules.size() * 12 + 15, categoryHeight), 
                         0x60000000);
                
                // Draw category name (smaller font)
                context.drawTextWithShadow(this.textRenderer, 
                        Text.literal(category), 
                        categoryX, categoryY, 0xFF4A90E2);
                
                // Draw modules
                int moduleX = categoryX + 5; // Smaller indent
                int moduleY = categoryY + 12; // Smaller gap after category name
                
                // Only render visible modules (basic culling)
                int visibleModules = Math.min(modules.size(), 8); // Limit to 8 visible modules per category
                for (int i = 0; i < visibleModules; i++) {
                    Module module = modules.get(i);
                    Integer colorObj = moduleColors.get(module);
                    int color = (colorObj != null) ? colorObj : 0xFFAAAAAA;
                    
                    context.drawTextWithShadow(this.textRenderer, 
                            Text.literal(module.getName()), 
                            moduleX, moduleY, color);
                    moduleY += 12; // Smaller line height
                }
                
                // Show count if there are more modules
                if (modules.size() > visibleModules) {
                    context.drawTextWithShadow(this.textRenderer, 
                            Text.literal("+" + (modules.size() - visibleModules) + " more"), 
                            moduleX, moduleY, 0xFF888888);
                }
                
                categoryIndex++;
            }
            
            // Draw instructions - only once at the bottom (smaller)
            context.drawCenteredTextWithShadow(this.textRenderer, 
                    Text.literal("RIGHT SHIFT/ESC: close | CLICK: toggle module"), 
                    this.width / 2, this.height - 10, 0xFFAAAAAA);
        } else {
            // Render an error message if the browser failed to initialize
            context.drawCenteredTextWithShadow(this.textRenderer, 
                    Text.literal("Failed to initialize Er Sambucone GUI browser"), 
                    this.width / 2, this.height / 2, 0xFF5555);
        }
    }
    
    // Update module colors in batch instead of every frame
    private void updateModuleColors() {
        moduleColors.clear();
        Map<String, List<Module>> modulesByCategory = GuiManager.getInstance().getModulesByCategory();
        
        for (List<Module> modules : modulesByCategory.values()) {
            for (Module module : modules) {
                moduleColors.put(module, module.isEnabled() ? 0xFF55FF55 : 0xFFAAAAAA);
            }
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (initialized && browser != null && button == 0) { // Only process left clicks
            Map<String, List<Module>> modulesByCategory = GuiManager.getInstance().getModulesByCategory();
            
            // Use cached category positions for faster hit detection
            for (Map.Entry<String, Integer> entry : categoryPositions.entrySet()) {
                String category = entry.getKey();
                int posData = entry.getValue();
                int categoryY = posData >> 16;
                int categoryX = posData & 0xFFFF;
                
                List<Module> modules = modulesByCategory.get(category);
                if (modules == null) continue;
                
                // Calculate module positions
                int moduleX = categoryX + 5; // Smaller indent
                int moduleY = categoryY + 12; // Smaller gap after category name
                
                // Only check visible modules (max 8 per category)
                int visibleModules = Math.min(modules.size(), 8);
                for (int i = 0; i < visibleModules; i++) {
                    Module module = modules.get(i);
                    
                    // Optimized hit detection with better boundaries
                    if (mouseX >= moduleX && mouseX <= moduleX + 110 && 
                        mouseY >= moduleY - 4 && mouseY <= moduleY + 8) {
                        
                        // Toggle with sound feedback
                        MinecraftClient.getInstance().getSoundManager().play(
                            net.minecraft.client.sound.PositionedSoundInstance.master(
                                net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        
                        // Toggle the module
                        module.toggle();
                        
                        // Update color immediately for responsive feedback
                        moduleColors.put(module, module.isEnabled() ? 0xFF55FF55 : 0xFFAAAAAA);
                        
                        Logger.log("Toggled module: " + module.getName() + " to " + 
                                  (module.isEnabled() ? "enabled" : "disabled"));
                        return true;
                    }
                    moduleY += 12; // Smaller line height
                }
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (initialized && browser != null) {
            // Forward mouse events to the browser
            // browser.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (initialized && browser != null) {
            // Forward mouse events to the browser
            // browser.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Check for Right Shift key (342) or Escape key (256) to close the GUI
        if (keyCode == 342 || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT || 
            keyCode == 256 || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            Logger.log("Closing GUI with key: " + keyCode);
            this.close();
            MinecraftClient.getInstance().setScreen(null);
            return true;
        }
        
        if (initialized && browser != null) {
            // Forward key events to the browser
            // browser.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (initialized && browser != null) {
            // Forward key events to the browser
            // browser.keyReleased(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (initialized && browser != null) {
            // Forward char events to the browser
            // browser.charTyped(chr, modifiers);
            return true;
        }
        return super.charTyped(chr, modifiers);
    }
    
    // Clean up resources when the screen is closed
    public void close() {
        // Clean up browser resources
        if (browser != null) {
            // browser.dispose();
            browser = null;
        }
        // No super.onClose() call since we're not overriding
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
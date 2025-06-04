package com.ersambucone.ui.themes;

import java.awt.Color;

public class NeonTheme {
    public static final Color PRIMARY = new Color(0, 255, 255);   // Ciano
    public static final Color SECONDARY = new Color(255, 0, 255); // Magenta
    public static final Color BACKGROUND = new Color(10, 10, 30); // Nero bluastro
    
    public static void applyGlowEffect(Component component) {
        component.setBorder(BorderFactory.createLineBorder(PRIMARY, 2));
        component.setBackground(new Color(BACKGROUND.getRed(), BACKGROUND.getGreen(), BACKGROUND.getBlue(), 200));
    }
}
package com.ersambucone.ui.components;

import javax.swing.*;
import java.awt.*;

public class FarmPanel extends JPanel {
    public FarmPanel() {
        setLayout(new GridLayout(3, 1));
        NeonTheme.applyGlowEffect(this);
        
        add(new JLabel("FARM OPTIMIZATION", SwingConstants.CENTER));
        add(createStatLabel("Current Yield", "0 items/h"));
        add(createStatLabel("Recommended Path", "S-Shape"));
    }

    private JLabel createStatLabel(String title, String value) {
        JLabel label = new JLabel(title + ": " + value, SwingConstants.CENTER);
        label.setForeground(NeonTheme.SECONDARY);
        return label;
    }
}
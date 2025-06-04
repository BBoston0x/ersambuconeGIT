package com.ersambucone.ui.components;

import com.ersambucone.ai.SkyblockLearner;
import com.ersambucone.utils.RenderUtils;
import javax.swing.*;
import java.awt.*;

public class AiStatusPanel extends JPanel {
    private final SkyblockLearner ai;
    
    public AiStatusPanel(SkyblockLearner ai) {
        this.ai = ai;
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        NeonTheme.applyGlowEffect(this);
        
        JLabel title = new JLabel("AI STATUS");
        title.setForeground(NeonTheme.PRIMARY);
        
        JProgressBar scanProgress = new JProgressBar();
        scanProgress.setStringPainted(true);
        
        add(title);
        add(scanProgress);
        
        new Timer(1000, e -> updateData(scanProgress)).start();
    }

    private void updateData(JProgressBar progress) {
        progress.setValue((int)(ai.getScanProgress() * 100));
        progress.setString(String.format("Scan: %.1f%%", ai.getScanProgress() * 100));
        progress.setForeground(ai.isActive() ? NeonTheme.PRIMARY : Color.GRAY);
    }
}
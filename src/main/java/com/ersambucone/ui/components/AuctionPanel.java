package com.ersambucone.ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AuctionPanel extends JScrollPane {
    private final DefaultTableModel model;
    
    public AuctionPanel() {
        this.model = new DefaultTableModel(
            new Object[]{"Item", "Price", "Market Value"}, 0
        );
        
        JTable table = new JTable(model);
        table.setBackground(NeonTheme.BACKGROUND);
        table.setForeground(NeonTheme.PRIMARY);
        
        setViewportView(table);
        NeonTheme.applyGlowEffect(this);
    }

    public void addAuction(String item, double price, double marketValue) {
        model.addRow(new Object[]{
            item,
            String.format("%,.1f", price),
            String.format("%,.1f", marketValue)
        });
    }
}
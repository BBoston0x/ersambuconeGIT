package com.ersambucone.modules.impl.monitoring;

import com.ersambucone.data.Auction;
import com.ersambucone.events.EventBus;
import com.ersambucone.events.EventListener;
import com.ersambucone.events.auction.UnderpricedAlertEvent;
import com.ersambucone.modules.Category;
import com.ersambucone.modules.Module;
import com.ersambucone.ui.notifications.NotificationManager;
import com.ersambucone.utils.Logger;

/**
 * Module for monitoring auctions and alerting on underpriced items
 */
public class SmartAuctionWatcher extends Module {
    private final EventListener eventListener = new EventListener() {
        @Override
        public void onEvent(Object event) {
            if (event instanceof UnderpricedAlertEvent) {
                onAlert((UnderpricedAlertEvent) event);
            }
        }
    };
    
    /**
     * Creates a new SmartAuctionWatcher
     */
    public SmartAuctionWatcher() {
        super("Smart Auctions", "Monitors auctions and alerts on underpriced items", Category.MONITORING);
    }
    
    @Override
    public void onEnable() {
        EventBus.register(eventListener);
        Logger.log("Smart Auction Watcher enabled");
    }
    
    @Override
    public void onDisable() {
        EventBus.unregister(eventListener);
        Logger.log("Smart Auction Watcher disabled");
    }

    /**
     * Handles underpriced alert events
     * 
     * @param event The event
     */
    private void onAlert(UnderpricedAlertEvent event) {
        try {
            Auction auction = event.getAuction();
            double priceDiffPercent = event.getPriceDifferencePercentage();
            
            String message = String.format(
                "⚠️ %s at %.1fM (Value: %.1fM, %.1f%% profit)", 
                auction.getItemName(), 
                auction.getCurrentPrice() / 1_000_000.0,
                event.getEstimatedValue() / 1_000_000.0,
                priceDiffPercent
            );
            
            // Use different notification types based on profit potential
            NotificationManager.NotificationType type = NotificationManager.NotificationType.INFO;
            if (priceDiffPercent > 50) {
                type = NotificationManager.NotificationType.SUCCESS;
            } else if (priceDiffPercent > 20) {
                type = NotificationManager.NotificationType.WARNING;
            }
            
            NotificationManager.getInstance().show(
                "UNDERPRICED ITEM", 
                message,
                type
            );
            
            Logger.log("Underpriced item alert: " + message);
        } catch (Exception e) {
            Logger.error("Error handling underpriced alert: " + e.getMessage());
        }
    }
}
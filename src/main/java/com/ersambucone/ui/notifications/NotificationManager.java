package com.ersambucone.ui.notifications;

import com.ersambucone.utils.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages notifications
 */
public class NotificationManager {
    private static final NotificationManager INSTANCE = new NotificationManager();
    private final List<Notification> notifications = new ArrayList<>();
    private final int MAX_NOTIFICATIONS = 5;
    private final long NOTIFICATION_DURATION = 5000; // 5 seconds
    
    /**
     * Creates a new NotificationManager
     */
    private NotificationManager() {
        Logger.log("Initializing Notification Manager");
    }
    
    /**
     * Gets the NotificationManager instance
     * 
     * @return The NotificationManager instance
     */
    public static NotificationManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Shows a notification
     * 
     * @param title The title
     * @param message The message
     * @param type The type
     */
    public void show(String title, String message, NotificationType type) {
        Notification notification = new Notification(title, message, type);
        
        // Remove oldest notification if we have too many
        if (notifications.size() >= MAX_NOTIFICATIONS) {
            notifications.remove(0);
        }
        
        notifications.add(notification);
        
        // Play sound based on notification type
        if (type == NotificationType.ERROR || type == NotificationType.WARNING) {
            MinecraftClient.getInstance().getSoundManager().play(
                PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING, 1.0f)
            );
        }
        
        // Show in chat if it's important
        if (type == NotificationType.ERROR) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                Text.literal("§c[ERROR] §f" + title + ": " + message)
            );
        }
        
        Logger.log(type.name() + ": " + title + " - " + message);
    }
    
    /**
     * Shows an info notification
     * 
     * @param title The title
     * @param message The message
     */
    public void info(String title, String message) {
        show(title, message, NotificationType.INFO);
    }
    
    /**
     * Shows a success notification
     * 
     * @param title The title
     * @param message The message
     */
    public void success(String title, String message) {
        show(title, message, NotificationType.SUCCESS);
    }
    
    /**
     * Shows a warning notification
     * 
     * @param title The title
     * @param message The message
     */
    public void warning(String title, String message) {
        show(title, message, NotificationType.WARNING);
    }
    
    /**
     * Shows an error notification
     * 
     * @param title The title
     * @param message The message
     */
    public void error(String title, String message) {
        show(title, message, NotificationType.ERROR);
    }
    
    /**
     * Updates the notifications
     */
    public void update() {
        long currentTime = System.currentTimeMillis();
        
        // Remove expired notifications
        Iterator<Notification> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            if (currentTime - notification.getTimestamp() > NOTIFICATION_DURATION) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Gets the notifications
     * 
     * @return The notifications
     */
    public List<Notification> getNotifications() {
        return notifications;
    }
    
    /**
     * Notification type
     */
    public enum NotificationType {
        INFO,
        SUCCESS,
        WARNING,
        ERROR
    }
    
    /**
     * Notification
     */
    public static class Notification {
        private final String title;
        private final String message;
        private final NotificationType type;
        private final long timestamp;
        
        /**
         * Creates a new Notification
         * 
         * @param title The title
         * @param message The message
         * @param type The type
         */
        public Notification(String title, String message, NotificationType type) {
            this.title = title;
            this.message = message;
            this.type = type;
            this.timestamp = System.currentTimeMillis();
        }
        
        /**
         * Gets the title
         * 
         * @return The title
         */
        public String getTitle() {
            return title;
        }
        
        /**
         * Gets the message
         * 
         * @return The message
         */
        public String getMessage() {
            return message;
        }
        
        /**
         * Gets the type
         * 
         * @return The type
         */
        public NotificationType getType() {
            return type;
        }
        
        /**
         * Gets the timestamp
         * 
         * @return The timestamp
         */
        public long getTimestamp() {
            return timestamp;
        }
    }
}
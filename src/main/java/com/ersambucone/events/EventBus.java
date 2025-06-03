package com.ersambucone.events;

import com.ersambucone.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Optimized EventBus implementation with event type filtering
 * for better performance and reduced lag
 */
public class EventBus {
    // Use CopyOnWriteArrayList for thread safety without synchronization overhead
    private static final Map<Class<?>, List<EventListener>> listenersByType = new HashMap<>();
    private static final List<EventListener> globalListeners = new CopyOnWriteArrayList<>();
    
    // Event type cache for faster lookups
    private static final Map<Class<?>, List<Class<?>>> eventHierarchyCache = new HashMap<>();
    
    /**
     * Register a listener for all events
     */
    public static void register(EventListener listener) {
        if (!globalListeners.contains(listener)) {
            globalListeners.add(listener);
        }
    }
    
    /**
     * Register a listener for a specific event type
     * This is more efficient than registering for all events
     */
    public static void register(EventListener listener, Class<?> eventType) {
        listenersByType.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }
    
    /**
     * Unregister a listener
     */
    public static void unregister(EventListener listener) {
        globalListeners.remove(listener);
        
        // Remove from specific event types
        for (List<EventListener> listeners : listenersByType.values()) {
            listeners.remove(listener);
        }
    }

    /**
     * Fire an event to all registered listeners
     * Uses type-based filtering for better performance
     */
    public static void fire(Object event) {
        if (event == null) return;
        
        Class<?> eventClass = event.getClass();
        
        try {
            // First notify type-specific listeners
            for (Class<?> type : getEventHierarchy(eventClass)) {
                List<EventListener> typeListeners = listenersByType.get(type);
                if (typeListeners != null) {
                    for (EventListener listener : typeListeners) {
                        listener.onEvent(event);
                    }
                }
            }
            
            // Then notify global listeners
            for (EventListener listener : globalListeners) {
                listener.onEvent(event);
            }
        } catch (Exception e) {
            Logger.log("Error firing event: " + e.getMessage());
        }
    }
    
    /**
     * Get all superclasses and interfaces of an event type
     * Uses caching for better performance
     */
    private static List<Class<?>> getEventHierarchy(Class<?> eventClass) {
        // Check cache first
        List<Class<?>> hierarchy = eventHierarchyCache.get(eventClass);
        if (hierarchy != null) {
            return hierarchy;
        }
        
        // Build hierarchy
        hierarchy = new ArrayList<>();
        hierarchy.add(eventClass);
        
        // Add superclasses
        Class<?> superClass = eventClass.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            hierarchy.add(superClass);
            superClass = superClass.getSuperclass();
        }
        
        // Cache result
        eventHierarchyCache.put(eventClass, hierarchy);
        return hierarchy;
    }
}
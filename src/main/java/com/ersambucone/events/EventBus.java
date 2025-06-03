package com.ersambucone.events;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    private static final List<EventListener> listeners = new ArrayList<>();

    public static void register(EventListener listener) {
        listeners.add(listener);
    }

    public static void fire(Event event) {
        listeners.forEach(l -> l.onEvent(event));
    }
}
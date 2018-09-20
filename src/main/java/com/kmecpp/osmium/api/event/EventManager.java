package com.kmecpp.osmium.api.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.kmecpp.osmium.api.logging.OsmiumLogger;

public class EventManager {

	//Only for Osmium events
	private final HashMap<Class<? extends Event>, ArrayList<RegisteredListener>> events = new HashMap<>();

	public void registerListener(Class<? extends Event> eventClass, Object listenerInstance, Method listener) {
		listener.setAccessible(true);
		if (listener.getParameterTypes().length != 1 || !eventClass.isAssignableFrom(listener.getParameterTypes()[0])) {
			OsmiumLogger.warn("Failed to register listener: " + listener.toString() + " in class: " + listener.getDeclaringClass());
			return;
		} else if (listenerInstance == null || listenerInstance.getClass().isAssignableFrom(listener.getParameterTypes()[0])) {
			OsmiumLogger.warn("Invalid listener instance: " + listenerInstance);
			return;
		}

		ArrayList<RegisteredListener> listeners = events.get(eventClass);
		if (listeners == null) {
			listeners = new ArrayList<>();
			events.put(eventClass, listeners);
		}
		listeners.add(new RegisteredListener(listenerInstance, listener));
	}

	public void callEvent(Event event) {
		ArrayList<RegisteredListener> listeners = events.get(event.getClass());
		if (listeners == null) {
			return;
		}

		for (RegisteredListener listener : listeners) {
			listener.call(event);
		}
	}

}

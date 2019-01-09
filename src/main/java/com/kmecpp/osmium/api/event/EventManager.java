package com.kmecpp.osmium.api.event;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Consumer;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;

public class EventManager {

	//Event key is combination of event class and order 
	private final HashMap<EventKey, ArrayList<Method>> listeners = new HashMap<>();

	//Only for Osmium events
	private final HashMap<Class<? extends Event>, ArrayList<RegisteredListener>> events = new HashMap<>();

	public void registerListener(Class<? extends Event> eventClass, Order order, Object listenerInstance, Method listener) {
		listener.setAccessible(true);

		if (listener.getParameterTypes().length != 1 || !listener.getParameterTypes()[0].isAssignableFrom(eventClass)) {
			fail(eventClass, listener, "Invalid listener parameters!");
			return;
		} else if (listenerInstance == null) { // || listenerInstance.getClass().isAssignableFrom(listener.getParameterTypes()[0])) {
			fail(eventClass, listener, "Osmium has no listener instance!");
			return;
		}

		ArrayList<RegisteredListener> listeners = events.get(eventClass);
		if (listeners == null) {
			events.put(eventClass, (listeners = new ArrayList<>()));
		}
		listeners.add(new RegisteredListener(listenerInstance, listener, order));
	}

	private static void fail(Class<? extends Event> eventClass, Method listener, String message) {
		OsmiumLogger.warn("Failed to register listener for " + eventClass.getName() + "!");
		OsmiumLogger.warn("Listener: " + listener.getClass().getName() + "." + listener);
		OsmiumLogger.warn(message);
	}

	public void callEvent(Event event) {
		ArrayList<RegisteredListener> listeners = events.get(event.getClass());
		if (listeners == null) {
			return;
		}

		listeners.sort(Comparator.comparing(RegisteredListener::getOrder));
		for (RegisteredListener listener : listeners) {
			listener.call(event);
		}
	}

	public void registerListener(OsmiumPlugin plugin, EventInfo eventInfo, Order order, Method method, Object listenerInstance) {
		Class<?> osmiumImplementation = eventInfo.getOsmiumImplementation();
		Class<?>[] nestedClasses = osmiumImplementation.getClass().getDeclaredClasses();

		for (Class<?> nestedClass : nestedClasses) {
			if (Event.class.isAssignableFrom(nestedClass)) {
				registerSourceListener(plugin, EventInfo.get(Reflection.cast(nestedClass)), order, method, listenerInstance);
				return;
			}
		}

		registerSourceListener(plugin, eventInfo, order, method, listenerInstance);
	}

	private void registerSourceListener(OsmiumPlugin plugin, EventInfo eventInfo, Order order, Method method, Object listenerInstance) {
		Class<?> sourceEventClass = eventInfo.getSource();
		EventKey key = new EventKey(sourceEventClass, order);

		boolean alreadyRegistered = this.listeners.containsKey(key);
		ArrayList<Method> listeners = alreadyRegistered ? this.listeners.get(key) : new ArrayList<>();
		if (!alreadyRegistered) {
			this.listeners.put(key, listeners);
		}

		listeners.add(method);

		if (!alreadyRegistered) {
			//Register source event once for each sourceEventClass/order combination 
			final Constructor<? extends Event> eventWrapper;
			try {
				eventWrapper = eventInfo.getOsmiumImplementation().getConstructor(sourceEventClass);
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				return;
			}
			Consumer<Object> consumer = (sourceEventInstance) -> {
				if (sourceEventClass.isAssignableFrom(sourceEventInstance.getClass())) {
					try {
						Event event = eventWrapper.newInstance(sourceEventInstance);
						if (!event.shouldFire()) {
							return;
						}

						for (Method listener : listeners) {
							try {
								listener.invoke(listenerInstance, event);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			};

			if (Platform.isBukkit()) {
				BukkitAccess.registerListener(plugin, eventInfo, order, method, listenerInstance, consumer);
			} else if (Platform.isSponge()) {
				SpongeAccess.registerListener(plugin, eventInfo, order, method, listenerInstance, consumer);
			}
		}
	}

}

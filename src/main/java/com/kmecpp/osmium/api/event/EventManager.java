package com.kmecpp.osmium.api.event;

import java.util.ArrayList;
import java.util.HashMap;

import com.kmecpp.osmium.OsmiumLogger;

public class EventManager {

	private HashMap<Class<? extends Event>, ArrayList<RegisteredListener>> listeners = new HashMap<>();

	public void registerEvents(Object listener) {

	}

	public void unregister(Object listener) {

	}

	public boolean callEvent(Event event) {
		ArrayList<RegisteredListener> eventListeners = this.listeners.get(event.getClass());
		boolean success = true;
		if (eventListeners != null) {
			for (RegisteredListener listener : eventListeners) {
				try {
					listener.callEvent(event);
				} catch (Throwable e) {
					success = false;
					OsmiumLogger.warn("An error occurred while calling a plugin event!");
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	public HashMap<Class<? extends Event>, ArrayList<RegisteredListener>> getListeners() {
		return listeners;
	}

	//	private HashMap<Class<? extends PluginEvent>, ArrayList<RegisteredListener>> listeners = new HashMap<Class<? extends PluginEvent>, ArrayList<RegisteredListener>>();
	//
	//	public void unregisterEvents(Listener listener) {
	//		HandlerList.unregisterAll(listener);
	//		for (Entry<Class<? extends PluginEvent>, ArrayList<RegisteredListener>> entry : listeners.entrySet()) {
	//			Iterator<RegisteredListener> iterator = entry.getValue().listIterator();
	//			while (iterator.hasNext()) {
	//				if (iterator.next().getClass().equals(listener.getClass())) {
	//					iterator.remove();
	//				}
	//			}
	//		}
	//	}
	//
	//	public void registerEvents(Listener listener) {
	//		registerEvents(listener, true);
	//	}
	//
	//	public void registerEvents(Listener listener, boolean bukkitEvents) {
	//		//Register Bukkit listener
	//		if (bukkitEvents) {
	//			Bukkit.getPluginManager().registerEvents(listener, VoidFlameCore.getPlugin());
	//		}
	//
	//		//Loop through methods
	//		for (final Method method : listener.getClass().getMethods()) {
	//			if (method.getAnnotation(PluginEventHandler.class) == null
	//					|| method.getParameterTypes().length != 1
	//					|| !PluginEvent.class.isAssignableFrom(method.getParameterTypes()[0])) {
	//				continue;
	//			}
	//			method.setAccessible(true);
	//
	//			Class<? extends PluginEvent> eventClass = method.getParameterTypes()[0].asSubclass(PluginEvent.class);
	//
	//			ArrayList<RegisteredListener> currentListeners = listeners.containsKey(eventClass) ? listeners.get(eventClass) : new ArrayList<RegisteredListener>();
	//			currentListeners.add(new RegisteredListener(listener, new EventExecutor() {
	//
	//				@Override
	//				public void execute(Listener listener, PluginEvent event) throws Throwable {
	//					method.invoke(listener, event);
	//				}
	//
	//			}));
	//
	//			listeners.put(eventClass, currentListeners);
	//			CoreLogger.debug(listener.getClass().getSimpleName() + " class registered as a listener for " + eventClass.getSimpleName());
	//		}
	//	}
	//
	//	public boolean callEvent(PluginEvent event) {
	//		ArrayList<RegisteredListener> listeners = EventManager.listeners.get(event.getClass());
	//		boolean success = true;
	//		if (listeners != null) {
	//			for (RegisteredListener listener : listeners) {
	//				try {
	//					listener.callEvent(event);
	//				} catch (Throwable e) {
	//					success = false;
	//					Logger.warn("An error occurred while calling a plugin event!");
	//					e.printStackTrace();
	//				}
	//			}
	//		}
	//		return success;
	//	}

}

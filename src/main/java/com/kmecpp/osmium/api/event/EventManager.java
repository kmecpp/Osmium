package com.kmecpp.osmium.api.event;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.BungeeAccess;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;

public class EventManager {

	//Event key is combination of event class and order 
	//Order doesn't have to be maintained here because we're simply passing the source order to the platform specific registerListener method
	private final HashMap<EventKey, HashMap<Object, ArrayList<Method>>> proxyListeners = new HashMap<>();

	//Only for Osmium events
	//The Listener event list must be kept in sorted order (by priority)
	private final HashMap<Class<? extends EventAbstraction>, ArrayList<RegisteredListener>> events = new HashMap<>();

	public void registerListener(Class<? extends EventAbstraction> eventClass, Order order, Object listenerInstance, Method listener) {
		listener.setAccessible(true);

		if (listener.getParameterTypes().length != 1 || !listener.getParameterTypes()[0].isAssignableFrom(eventClass)) {
			fail(eventClass, listener, "Invalid listener parameters!");
			return;
		} else if (listenerInstance == null) { // || listenerInstance.getClass().isAssignableFrom(listener.getParameterTypes()[0])) {
			fail(eventClass, listener, "Osmium has no listener instance!");
			return;
		}

		//		OsmiumLogger.error("REGISTER LISTENER: " + eventClass + " :: " + listenerInstance);
		ArrayList<RegisteredListener> listeners = events.computeIfAbsent(eventClass, k -> new ArrayList<>());
		//		ArrayList<RegisteredListener> listeners = events.get(eventClass);
		//		if (listeners == null) {
		//			events.put(eventClass, (listeners = new ArrayList<>()));
		//		}

		RegisteredListener registeredListener = new RegisteredListener(listenerInstance, listener, order);
		int index = Collections.binarySearch(listeners, registeredListener);
		if (index < 0) {
			index = -index - 1;
		}
		listeners.add(index, registeredListener);
		//		System.out.println(listeners);
	}

	private static void fail(Class<? extends EventAbstraction> eventClass, Method listener, String message) {
		OsmiumLogger.warn("Failed to register listener for " + eventClass.getName() + "!");
		OsmiumLogger.warn("Listener: " + listener.getClass().getName() + "." + listener);
		OsmiumLogger.warn(message);
	}

	public void callEvent(Event event) {
		ArrayList<RegisteredListener> listeners = events.get(event.getClass());
		//		OsmiumLogger.warn("CALLING: " + event + " :: " + listeners);
		if (listeners == null) {
			return;
		}

		for (RegisteredListener listener : listeners) { //Listeners should be kept in priority order
			listener.call(event);
		}
	}

	public void registerListener(OsmiumPlugin plugin, EventInfo eventInfo, Order order, Method method, Object listenerInstance) {
		Class<? extends Event> osmiumEventInterface = eventInfo.getEventWrapperClass(); //getOsmiumImplementation();
		Class<?>[] nestedClasses = osmiumEventInterface.getClass().getDeclaredClasses();

		//Register event class with children. Ex: void on(BlockEvent)
		boolean registered = false;
		for (Class<?> nestedClass : nestedClasses) {
			if (EventAbstraction.class.isAssignableFrom(nestedClass) && nestedClass.isInterface()) {
				registerSourceListener(plugin, EventInfo.get(Reflection.cast(nestedClass)), order, method, listenerInstance);
				registered = true;
			}
		}

		//Register single event class
		if (!registered) {
			registerSourceListener(plugin, eventInfo, order, method, listenerInstance);
		}
	}

	private void registerSourceListener(OsmiumPlugin plugin, EventInfo eventInfo, Order order, Method method, Object listenerInstance) {
		//		OsmiumLogger.debug("Registering source listener for " + plugin.getName() + ": "
		//				+ listenerInstance.getClass().getName() + "." + method.getName() + "(" + eventInfo.getEventName() + ")");
		//		OsmiumLogger.debug(Chat.YELLOW + "    SOURCE CLASSES: " + eventInfo.getSourceClasses());
		EventKey key = new EventKey(eventInfo.getEventWrapperClass(), order); //Every Osmium event wrapper class has exactly one global handler
		for (Class<?> sourceEventClass : eventInfo.getSourceClasses()) {
			//			EventKey key = new EventKey(sourceEventClass, order);

			boolean firstEventRegistration = !this.proxyListeners.containsKey(key);
			HashMap<Object, ArrayList<Method>> listenerInstances = this.proxyListeners.computeIfAbsent(key, k -> new HashMap<>());

			listenerInstances.computeIfAbsent(listenerInstance, k -> new ArrayList<>())
					.add(method);

			//			System.out.println("FIRST REGISTRATION: " + firstEventRegistration);

			if (firstEventRegistration) {
				//Register source event once for each sourceEventClass/order combination 
				final Constructor<? extends EventAbstraction> eventWrapperConstructor = getConstructor(eventInfo, sourceEventClass);
				//				System.out.println("EVENT WRAPPER CONSTRUCTOR FOR " + sourceEventClass + " == " + eventWrapperConstructor);

				Consumer<Object> globalOsmiumHandlerForEvent = (sourceEventInstance) -> {
					//					System.out.println("SOURCE EVENT INSTANCE: " + sourceEventInstance);
					//					if (!sourceEventClass.isAssignableFrom(sourceEventInstance.getClass())) {
					//						return; //Is this necessary?
					//					}
					try {
						EventAbstraction event = eventWrapperConstructor.newInstance(sourceEventInstance); //Construct the Osmium Event wrapper with the source event instance
						if (!event.shouldFire()) {
							return;
						}

						for (Entry<Object, ArrayList<Method>> entry : listenerInstances.entrySet()) {
							Object currentListenerInstance = entry.getKey();
							for (Method listenerMethod : entry.getValue()) {
								try {
									//									System.out.println("Invoking: " + listenerMethod + ", " + currentListenerInstance + ", " + event);
									listenerMethod.invoke(currentListenerInstance, event);
								} catch (Exception ex) {
									//								System.out.println(listenerInstance.getClass().getName());
									OsmiumLogger.warn("An error occurred while firing " + sourceEventClass.getSimpleName() + " for " + currentListenerInstance.getClass().getName());
									ex.printStackTrace();
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				};

				//				OsmiumLogger.info(Chat.RED + "REGISTERED LISTENER FOR " + plugin.getName() + ": " + eventInfo.getEventName());

				//Register a listener on the source platform for the source event that invokes our global event handler
				if (Platform.isBukkit()) {
					BukkitAccess.registerOsmiumListener(plugin, Reflection.cast(sourceEventClass), order, method, listenerInstance, Reflection.cast(globalOsmiumHandlerForEvent));
				} else if (Platform.isSponge()) {
					SpongeAccess.registerOsmiumListener(plugin, Reflection.cast(sourceEventClass), order, method, listenerInstance, Reflection.cast(globalOsmiumHandlerForEvent));
				} else if (Platform.isProxy()) {
					BungeeAccess.registerOsmiumListener(plugin, Reflection.cast(sourceEventClass), order, method, listenerInstance, Reflection.cast(globalOsmiumHandlerForEvent));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends EventAbstraction> Constructor<T> getConstructor(EventInfo eventInfo, Class<?> sourceEventClass) {
		for (Constructor<?> constructor : eventInfo.getOsmiumImplementation().getConstructors()) {
			if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].isAssignableFrom(sourceEventClass)) {
				return (Constructor<T>) constructor;
			}
		}
		throw new RuntimeException("Failed to extract event wrapper constructor for " + sourceEventClass.getName());
	}

	//	/**
	//	 * NEED THIS FOR RUNTIME GENERATION OF BUNGEE HANDLERS
	//	 * 
	//	 * @param sourceEventClass
	//	 *            the Event class provided by
	//	 * @return the global Osmium handler for the given event
	//	 */
	//	public Consumer<Object> getOsmiumSourceEventConsumer(Class<?> sourceEventClass) {
	//		return osmiumSourceEventConsumers.get(sourceEventClass);
	//	}

}

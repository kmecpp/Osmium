package com.kmecpp.osmium.platform.bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.jlib.reflection.Reflection;
import com.kmecpp.osmium.OsmiumData;
import com.kmecpp.osmium.OsmiumLogger;
import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.EventInfo;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class BukkitPlugin extends JavaPlugin implements org.bukkit.event.Listener {

	private static final OsmiumPlugin plugin = OsmiumData.constructPlugin();

	private static BukkitPlugin instance;

	public BukkitPlugin() {
		if (instance != null) {
			throw new RuntimeException("Plugin already constructed!");
		}
		instance = this;
	}

	public static BukkitPlugin getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		plugin.onLoad();
	}

	@Override
	public void onEnable() {
		plugin.preInit();

		for (Class<?> listener : plugin.getPluginClasses()) {
			if (!Reflection.isConcrete(listener)) {
				continue;
			}

			for (Method method : listener.getMethods()) {
				Listener annotation = method.getAnnotation(Listener.class);
				if (annotation != null) {
					if (method.getParameterCount() != 1) {
						plugin.error("Invalid listener method with signature: '" + method + "'");
					} else if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
						plugin.error("Invalid listener method with signature: '" + method + "'");
					} else {
						Class<Event> eventClass = Reflection.cast(method.getParameterTypes()[0]);
						EventPriority priority;
						switch (annotation.order()) {
						case PRE:
							priority = EventPriority.LOWEST;
							break;
						case FIRST:
							priority = EventPriority.LOWEST;
							break;
						case EARLY:
							priority = EventPriority.LOW;
							break;
						case DEFAULT:
							priority = EventPriority.NORMAL;
							break;
						case LATE:
							priority = EventPriority.HIGH;
							break;
						case LAST:
							priority = EventPriority.HIGHEST;
							break;
						case POST:
							priority = EventPriority.MONITOR;
						default:
							continue;
						}

						//						Class<? extends Event> implementationClass = Reflection.cast(eventClass.getAnnotation(EventInfo.class).bukkit());
						//						System.out.println(eventClass);
						EventInfo eventInfo = EventInfo.get(eventClass);
						Class<? extends org.bukkit.event.Event> bukkitEventClass = eventInfo.getBukkitClass();

						Object listenerInstance;
						try {
							boolean contains = plugin.getListeners().containsKey(listener);
							listenerInstance = contains ? plugin.getListeners().get(listener) : listener.newInstance();
							if (!contains) {
								plugin.getListeners().put(listener, listenerInstance);
							}
						} catch (Exception e) {
							OsmiumLogger.error("Cannot instantiate " + listener.getName() + "! Listener classes without a default constructor must be enabled with: plugin.enableEvents(listener)");
							e.printStackTrace();
							break;
						}

						try {
							Constructor<?> eventWrapper = eventInfo.getBukkitImplementation().getConstructor(bukkitEventClass);
							Bukkit.getPluginManager().registerEvent(bukkitEventClass, this, priority, (l, e) -> {
								if (bukkitEventClass.isAssignableFrom(e.getClass())) {
									try {
										method.invoke(listenerInstance, eventWrapper.newInstance(e));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}, this, true);
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
					}
				}
			}
		}

		plugin.init();
		plugin.postInit();
	}

}

package com.kmecpp.osmium.api.event;

import java.lang.reflect.Method;

import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class RegisteredListener implements Comparable<RegisteredListener> {

	private final OsmiumPlugin plugin;
	private final Object instance;
	private final Method method;
	private final Order order;

	public RegisteredListener(OsmiumPlugin plugin, Object instance, Method method, Order order) {
		this.plugin = plugin;
		this.instance = instance;
		this.method = method;
		this.order = order;
	}

	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	public Object getInstance() {
		return instance;
	}

	public Method getMethod() {
		return method;
	}

	public Order getOrder() {
		return order;
	}

	public void call(Event event) {
		try {
			method.invoke(instance, event);
		} catch (Throwable e) {
			OsmiumLogger.error("Error occurred while executing event!");
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(RegisteredListener l) {
		return order.ordinal() - l.order.ordinal();
	}

	@Override
	public int hashCode() {
		return method.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RegisteredListener) {
			return method.equals(((RegisteredListener) obj).method);
		}
		return false;
	}

	@Override
	public String toString() {
		return instance.getClass().getName() + "." + method.getName();
	}

}

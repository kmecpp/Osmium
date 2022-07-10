package com.kmecpp.osmium.api.event;

import java.lang.reflect.Method;

import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class RegisteredProxyListener {

	private final OsmiumPlugin plugin;
	private final Object instance;
	private final Method method;

	public RegisteredProxyListener(OsmiumPlugin plugin, Object instance, Method method) {
		this.plugin = plugin;
		this.instance = instance;
		this.method = method;
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

	@Override
	public String toString() {
		return instance.getClass().getName() + "." + method.getName();
	}

}

package com.kmecpp.osmium;

import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

public class Hook<T> {

	private T hook;

	private Hook(T hook) {
		this.hook = hook;
	}

	public static <T> Hook<T> of(T inst) {
		return new Hook<T>(inst);
	}

	public static <T> Hook<T> empty() {
		return new Hook<>(null);
	}

	public boolean isLoaded() {
		return hook != null;
	}

	public T get() {
		return hook;
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(String pluginName) {
		Object result = null;
		if (Platform.isBukkit()) {
			result = Bukkit.getPluginManager().getPlugin(pluginName);
		} else if (Platform.isSponge()) {
			for (PluginContainer pluginContainer : Sponge.getPluginManager().getPlugins()) {
				if (pluginContainer.getName().equalsIgnoreCase(pluginName) || pluginContainer.getId().equalsIgnoreCase(pluginName)) {
					result = pluginContainer.getInstance().orElse(null);
				}
			}
		}
		return (T) new Hook<>(result);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFrom(String method) {
		String[] parts = method.split("\\:");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Expected: class:method");
		}
		String className = parts[0];
		String methodName = parts[1];

		try {
			return (T) new Hook<>(Class.forName(className).getMethod(methodName).invoke(null));
		} catch (Exception e) {
			//			e.printStackTrace();
			return (T) new Hook<>(null);
		}
	}

	public static <T> Hook<T> get(Callable<T> callable) {
		System.out.println("GETTING!!!");
		try {
			return new Hook<T>(callable.call());
		} catch (Throwable t) {
			return new Hook<T>(null);
		}
	}

}

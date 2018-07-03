package com.kmecpp.osmium.api.plugin;

import java.util.concurrent.Callable;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.osmium.Osmium;

/**
 * Plugin's main Bukkit class. Osmium plugins will automatically subclass this
 * class as their entry point for Bukkit
 */
public class BukkitPlugin extends JavaPlugin implements Listener {

	private final OsmiumPlugin plugin = Osmium.loadPlugin(this); //OsmiumData.constructPlugin();

	private BukkitPlugin bukkitInstance;

	public BukkitPlugin() {
		if (bukkitInstance != null) {
			throw new RuntimeException("Plugin already constructed!");
		}
		this.bukkitInstance = this;
	}

	public OsmiumPlugin execute(Callable<OsmiumPlugin> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public BukkitPlugin getBukkitInstance() {
		return bukkitInstance;
	}

	@Override
	public void onLoad() {
		plugin.onLoad();
	}

	@Override
	public void onEnable() {
		plugin.preInit();
		plugin.getClassManager().initializeHooks();
		plugin.init();
		plugin.postInit();
	}

}

package com.kmecpp.osmium.api.plugin;

import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.osmium.Osmium;

/**
 * Plugin's main Bukkit class. Osmium plugins will automatically subclass this
 * class as their entry point for Bukkit
 */
public abstract class BukkitPlugin extends JavaPlugin implements Listener {

	private OsmiumPlugin plugin = Osmium.getPluginLoader().load(this); //OsmiumData.constructPlugin();

	public OsmiumPlugin execute(Callable<OsmiumPlugin> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onLoad() {
		if (plugin == null) {
			Bukkit.getPluginManager().disablePlugin(this);
		} else {
			plugin.onLoad();

		}
	}

	@Override
	public void onEnable() {
		if (plugin != null) {
			plugin.onPreInit();
			Bukkit.getPluginManager().registerEvents(this, this);
			plugin.getClassProcessor().initializeClasses();
			plugin.onInit();
			plugin.onPostInit();
		}
	}

	@Override
	public void onDisable() {
		if (plugin != null) {
			plugin.saveData();
			plugin.onDisable();
		}
	}

}

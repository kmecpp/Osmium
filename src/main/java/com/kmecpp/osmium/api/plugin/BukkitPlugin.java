package com.kmecpp.osmium.api.plugin;

import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.event.events.osmium.PluginRefreshEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumPluginRefreshEvent;

/**
 * Plugin's main Bukkit class. Osmium plugins will automatically subclass this
 * class as their entry point for Bukkit
 */
public abstract class BukkitPlugin extends JavaPlugin implements Listener {

	private final OsmiumPlugin plugin = Osmium.getPluginLoader().load(this); //OsmiumData.constructPlugin();

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
			try {
				plugin.getClassProcessor().loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
			plugin.onLoad();
		}
	}

	@Override
	public void onEnable() {
		if (plugin != null) {
			try {
				plugin.onPreInit();
			} catch (Throwable t) {
				catchError(t);
			}
			Bukkit.getPluginManager().registerEvents(this, this);
			try {
				plugin.getClassProcessor().initializeClasses();
			} catch (Throwable t) {
				catchError(t);
			}
			try {
				plugin.onInit();
			} catch (Throwable t) {
				catchError(t);
			}
			try {
				plugin.onPostInit();
			} catch (Throwable t) {
				catchError(t);
			}
			PluginRefreshEvent refreshEvent = new OsmiumPluginRefreshEvent(plugin, true);
			plugin.onRefresh(refreshEvent);
			Osmium.getEventManager().callEvent(refreshEvent);
			plugin.startComplete = true;
		}
	}

	@Override
	public void onDisable() {
		if (plugin != null) {
			plugin.saveData();
			plugin.onDisable();
		}
	}

	private void catchError(Throwable t) {
		t.printStackTrace();
		plugin.startError = true;
	}

}

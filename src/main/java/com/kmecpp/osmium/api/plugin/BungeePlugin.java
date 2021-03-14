package com.kmecpp.osmium.api.plugin;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.event.events.osmium.PluginRefreshEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumPluginRefreshEvent;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Parent class of every Osmium BungeeCord plugin. Osmium plugins will
 * automatically subclass this class as their entry point for BungeeCord
 */
public abstract class BungeePlugin extends Plugin implements Listener {

	private final OsmiumPlugin plugin = Osmium.getPluginLoader().load(this); //OsmiumData.constructPlugin();

	@Override
	public void onLoad() {
		if (plugin != null) {
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

			BungeeCord.getInstance().getPluginManager().registerListener(this, this);

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
				plugin.getClassProcessor().createDatabaseTables();
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

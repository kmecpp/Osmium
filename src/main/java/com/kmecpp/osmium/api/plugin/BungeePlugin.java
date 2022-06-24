package com.kmecpp.osmium.api.plugin;

import com.kmecpp.osmium.Osmium;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Parent class of every Osmium BungeeCord plugin. Osmium plugins will
 * automatically subclass this class as their entry point for BungeeCord
 */
public abstract class BungeePlugin extends Plugin implements Listener {

	private final OsmiumPlugin plugin = Osmium.getPluginLoader().createOsmiumPlugin(this); //OsmiumData.constructPlugin();

	@Override
	public void onLoad() {
		Osmium.getPluginLoader().onLoad(plugin);
	}

	@Override
	public void onEnable() {
		Osmium.getPluginLoader().onPreInit(plugin);
		Osmium.getPluginLoader().onInit(plugin);
		Osmium.getPluginLoader().onPostInit(plugin);
	}

	@Override
	public void onDisable() {
		Osmium.getPluginLoader().onDisable(plugin);
	}

}

package com.kmecpp.osmium.api.plugin;

import com.kmecpp.osmium.Osmium;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * Parent class of every Osmium BungeeCord plugin. Osmium plugins will
 * automatically subclass this class as their entry point for BungeeCord
 */
public abstract class BungeePlugin extends Plugin {

	private final OsmiumPlugin osmiumPlugin = Osmium.getPluginLoader().createOsmiumPlugin(this); //OsmiumData.constructPlugin();

	public OsmiumPlugin getOsmiumPlugin() {
		return osmiumPlugin;
	}

	@Override
	public void onLoad() {
		Osmium.getPluginLoader().onLoad(osmiumPlugin);
	}

	@Override
	public void onEnable() {
		Osmium.getPluginLoader().onPreInit(osmiumPlugin);
		Osmium.getPluginLoader().onInit(osmiumPlugin);
		Osmium.getPluginLoader().onPostInit(osmiumPlugin);
	}

	@Override
	public void onDisable() {
		Osmium.getPluginLoader().onDisable(osmiumPlugin);
	}

}

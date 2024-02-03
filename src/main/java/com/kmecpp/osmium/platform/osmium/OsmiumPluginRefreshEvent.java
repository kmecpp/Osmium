package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.event.events.osmium.PluginRefreshEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class OsmiumPluginRefreshEvent implements PluginRefreshEvent {

	private OsmiumPlugin plugin;
	private boolean database;

	public OsmiumPluginRefreshEvent(OsmiumPlugin plugin, boolean full) {
		this.plugin = plugin;
		this.database = full;
	}

	@Override
	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	public boolean isDatabase() {
		return database;
	}

}

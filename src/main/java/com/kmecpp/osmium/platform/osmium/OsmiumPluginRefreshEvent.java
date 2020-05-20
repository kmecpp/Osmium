package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.event.events.osmium.PluginRefreshEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class OsmiumPluginRefreshEvent implements PluginRefreshEvent {

	private OsmiumPlugin plugin;
	private boolean database;

	public OsmiumPluginRefreshEvent(OsmiumPlugin plugin, boolean database) {
		this.plugin = plugin;
		this.database = database;
	}

	@Override
	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	public boolean isDatabase() {
		return database;
	}

}

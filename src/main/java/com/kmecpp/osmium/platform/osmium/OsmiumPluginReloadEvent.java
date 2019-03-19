package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.event.events.PluginReloadEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class OsmiumPluginReloadEvent implements PluginReloadEvent {

	private OsmiumPlugin plugin;

	public OsmiumPluginReloadEvent(OsmiumPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public OsmiumPlugin getPlugin() {
		return plugin;
	}

}

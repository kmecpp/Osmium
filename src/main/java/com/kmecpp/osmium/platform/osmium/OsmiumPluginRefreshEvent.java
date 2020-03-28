package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.event.events.osmium.PluginRefreshEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class OsmiumPluginRefreshEvent implements PluginRefreshEvent {

	private OsmiumPlugin plugin;

	public OsmiumPluginRefreshEvent(OsmiumPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public OsmiumPlugin getPlugin() {
		return plugin;
	}

}

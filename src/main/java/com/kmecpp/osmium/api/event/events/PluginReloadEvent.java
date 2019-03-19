package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public interface PluginReloadEvent extends Event {

	OsmiumPlugin getPlugin();

}

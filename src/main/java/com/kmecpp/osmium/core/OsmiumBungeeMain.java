package com.kmecpp.osmium.core;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.plugin.BungeePlugin;

public class OsmiumBungeeMain extends BungeePlugin {

	@Override
	public void onDisable() {
		super.onDisable();
		Osmium.shutdown();
	}

}

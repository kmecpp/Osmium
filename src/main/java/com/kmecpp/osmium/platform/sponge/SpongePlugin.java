package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;

import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

// @Plugin annotation added at compile time
public abstract class SpongePlugin {

	private static final OsmiumPlugin plugin = OsmiumPlugin.getPlugin();

	@Listener
	public void onGameInitialization(GamePreInitializationEvent e) {
		plugin.preInit();
	}

	@Listener
	public void onGameInitialization(GameInitializationEvent e) {
		plugin.init();
	}

	@Listener
	public void onGameInitialization(GamePostInitializationEvent e) {
		plugin.postInit();
	}

}

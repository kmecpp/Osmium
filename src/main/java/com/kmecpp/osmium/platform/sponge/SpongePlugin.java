package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;

import com.kmecpp.osmium.OsmiumData;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

// @Plugin added by Osmium annotation processor
public abstract class SpongePlugin {

	private static final OsmiumPlugin PLUGIN = OsmiumData.constructPlugin();

	private static SpongePlugin instance;

	public SpongePlugin() {
		if (instance != null) {
			throw new RuntimeException("Plugin already constructed!");
		}
		SpongePlugin.instance = this;
	}

	public static SpongePlugin getInstance() {
		return instance;
	}

	@Listener
	public void onGameInitialization(GameConstructionEvent e) {
		PLUGIN.onLoad();
	}

	@Listener
	public void onGamePreInitialization(GamePreInitializationEvent e) {
		PLUGIN.preInit();
	}

	@Listener
	public void onGameInitialization(GameInitializationEvent e) {
		PLUGIN.init();
	}

	@Listener
	public void onGamePostInitialization(GamePostInitializationEvent e) {
		PLUGIN.postInit();
	}

}

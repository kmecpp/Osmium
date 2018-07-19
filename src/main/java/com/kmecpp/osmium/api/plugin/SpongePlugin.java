 package com.kmecpp.osmium.api.plugin;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;

import com.kmecpp.osmium.Osmium;

// @Plugin added by Osmium annotation processor
public abstract class SpongePlugin {

	private final OsmiumPlugin plugin = Osmium.loadPlugin(this);

	private SpongePlugin spongeInstance;

	public SpongePlugin() {
		if (spongeInstance != null) {
			throw new RuntimeException("Plugin already constructed!");
		}
		this.spongeInstance = this;
	}

	public SpongePlugin getSpongeInstance() {
		return spongeInstance;
	}

	@Listener
	public void onGameInitialization(GameConstructionEvent e) {
		plugin.onLoad();
	}

	@Listener
	public void onGamePreInitialization(GamePreInitializationEvent e) {
		plugin.preInit();
	}

	@Listener
	public void onGameInitialization(GameInitializationEvent e) {
		plugin.getClassManager().initializeHooks();
		plugin.init();
	}

	@Listener
	public void onGamePostInitialization(GamePostInitializationEvent e) {
		plugin.postInit();
	}

	@Listener
	public void onGamePostInitialization(GameStoppingEvent e) {
		plugin.onDisable();
	}

}

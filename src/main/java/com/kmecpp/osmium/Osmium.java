package com.kmecpp.osmium;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;

import com.kmecpp.osmium.api.command.OsmiumCommand;
import com.kmecpp.osmium.api.event.OsmiumEvent;
import com.kmecpp.osmium.api.event.OsmiumListener;
import com.kmecpp.osmium.api.plugin.ConfigManager;

public final class Osmium {

	public static final String OSMIUM = "Osmium";

	private static ConfigManager configManager;
	private static Logger logger;

	private Osmium() {
	}

	public static ConfigManager getConfigManager() {
		return configManager;
	}

	@Listener
	public void onGameConstruction(GameConstructionEvent e) {
		logger = LoggerFactory.getLogger(this.getClass().getName());
		configManager = new ConfigManager(Sponge.getGame().getConfigManager().getPluginConfig(this));

		//		initializer = getInitializer();
	}

	//COMMANDS
	public static final void registerCommand(OsmiumCommand command) {
		//		Sponge.getCommandManager().register(plugin, command.getSpec(), command.getAliases());
	}

	//EVENTS
	public static final void registerListener(OsmiumListener listener) {
		//		Sponge.getEventManager().registerListeners(plugin, listener);
	}

	public static final void unregisterListener(OsmiumListener listener) {
		//		Sponge.getEventManager().unregisterListeners(listener);
	}

	public static final boolean postEvent(OsmiumEvent e) {
		//		return Sponge.getEventManager().post(e);
		return false;
	}

	//LOGGING
	public static final Logger getLogger() {
		return logger;
	}

	public static final void log(String message) {
		logger.info(message);
	}

	public static final void logCore(String message) {
		logger.info(MarkerFactory.getMarker(OSMIUM), message);
	}

	public static final Platform getPlatform() {
		return Platform.getPlatform();
	}

}

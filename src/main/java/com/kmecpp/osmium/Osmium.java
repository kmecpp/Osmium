package com.kmecpp.osmium;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;

import com.kmecpp.osmium.api.OsmiumPlugin;
import com.kmecpp.osmium.api.command.OsmiumCommand;
import com.kmecpp.osmium.api.event.OsmiumEvent;
import com.kmecpp.osmium.api.event.OsmiumListener;

public final class Osmium {

	public static final String OSMIUM = "Osmium";

	private static Logger logger = LoggerFactory.getLogger(OSMIUM);

	private Osmium() {
	}

	@Listener
	public void onGameConstruction(GameConstructionEvent e) {
		//		logger = LoggerFactory.getLogger(this.getClass().getName());
		//		configManager = new ConfigManager(Sponge.getGame().getConfigManager().getPluginConfig(this));
	}

	//COMMANDS
	public static final void registerCommand(OsmiumCommand command) {
		Platform.execute(() -> {
			Sponge.getCommandManager().register(OsmiumPlugin.getPlugin(), null, "erg");
		}, null);
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

	public static final void log(String message) {
		logger.info(message);
	}

	public static final void debug(String message) {
		logger.debug(message);
	}

	public static final void info(String message) {
		logger.info(message);
	}

	public static final void warn(String message) {
		logger.warn(message);
	}

	public static final void error(String message) {
		logger.error(message);
	}

	public static final Platform getPlatform() {
		return Platform.getPlatform();
	}

}

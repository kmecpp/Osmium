package com.kmecpp.osmium.api.plugin;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.event.events.osmium.PluginRefreshEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumPluginRefreshEvent;

// @Plugin added by to subclass by Osmium annotation processor
public abstract class SpongePlugin {

	private final OsmiumPlugin plugin = Osmium.getPluginLoader().createOsmiumPlugin(this);
	private PluginContainer pluginContainer;

	public PluginContainer getPluginContainer() {
		return pluginContainer;
	}

	@Listener
	public void on(GameConstructionEvent e) {
		if (plugin == null) {
			disable();
			return;
		}

		pluginContainer = Sponge.getPluginManager().getPlugin(plugin.getId()).get();
		try {
			plugin.getClassProcessor().loadAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		plugin.onLoad();
	}

	@Listener
	public void on(GamePreInitializationEvent e) {
		try {
			plugin.onPreInit();
		} catch (Throwable t) {
			catchError(t);
		}
	}

	@Listener
	public void on(GameInitializationEvent e) {
		try {
			plugin.getClassProcessor().initializeClasses();
		} catch (Throwable t) {
			catchError(t);
		}
		try {
			plugin.onInit();
		} catch (Throwable t) {
			catchError(t);
		}
	}

	@Listener
	public void on(GamePostInitializationEvent e) {
		try {
			plugin.getClassProcessor().createDatabaseTables();
			plugin.onPostInit();
		} catch (Throwable t) {
			catchError(t);
		}
		PluginRefreshEvent refreshEvent = new OsmiumPluginRefreshEvent(plugin, true);
		plugin.onRefresh(refreshEvent);
		Osmium.getEventManager().callEvent(refreshEvent);
		plugin.startComplete = true;
	}

	@Listener
	public void on(GameStoppingEvent e) {
		plugin.savePersistentData();
		plugin.onDisable();
	}

	private void disable() {
		Sponge.getEventManager().unregisterPluginListeners(this);
		Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
		Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
	}

	private void catchError(Throwable t) {
		t.printStackTrace();
		plugin.startError = true;
	}

}

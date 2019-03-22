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

// @Plugin added by Osmium annotation processor
public abstract class SpongePlugin {

	private final OsmiumPlugin plugin = Osmium.getPluginLoader().load(this);
	private PluginContainer pluginContainer;

	public PluginContainer getPluginContainer() {
		return pluginContainer;
	}

	@Listener
	public void onGameConstructionEvent(GameConstructionEvent e) {
		if (plugin == null) {
			disable();
			return;
		}

		pluginContainer = Sponge.getPluginManager().getPlugin(plugin.getId()).get();
		plugin.onLoad();
	}

	@Listener
	public void onGamePreInitialization(GamePreInitializationEvent e) {
		plugin.onPreInit();
	}

	@Listener
	public void onGameInitialization(GameInitializationEvent e) {
		plugin.onInit();
		plugin.getClassProcessor().initializeClasses();
	}

	@Listener
	public void onGamePostInitialization(GamePostInitializationEvent e) {
		plugin.onPostInit();
	}

	@Listener
	public void onGamePostInitialization(GameStoppingEvent e) {
		plugin.saveData();
		plugin.onDisable();
	}

	private void disable() {
		Sponge.getEventManager().unregisterPluginListeners(this);
		Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
		Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
	}

}

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

// @Plugin added by to subclass by Osmium annotation processor
public abstract class SpongePlugin {

	private final OsmiumPlugin plugin = Osmium.getPluginLoader().createOsmiumPlugin(this);
	private PluginContainer pluginContainer;

	public PluginContainer getPluginContainer() {
		return pluginContainer;
	}

	@Listener
	public void on(GameConstructionEvent e) {
		pluginContainer = Sponge.getPluginManager().getPlugin(plugin.getId()).get();
		Osmium.getPluginLoader().onLoad(plugin);
	}

	@Listener
	public void on(GamePreInitializationEvent e) {
		Osmium.getPluginLoader().onPreInit(plugin);
	}

	@Listener
	public void on(GameInitializationEvent e) {
		Osmium.getPluginLoader().onInit(plugin);
	}

	@Listener
	public void on(GamePostInitializationEvent e) {
		Osmium.getPluginLoader().onPostInit(plugin);
	}

	@Listener
	public void on(GameStoppingEvent e) {
		Osmium.getPluginLoader().onDisable(plugin);

		Sponge.getEventManager().unregisterPluginListeners(this);
		Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
		Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
	}

}

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

	private final OsmiumPlugin osmiumPlugin = Osmium.getPluginLoader().createOsmiumPlugin(this);
	private PluginContainer pluginContainer;

	public OsmiumPlugin getOsmiumPlugin() {
		return osmiumPlugin;
	}

	public PluginContainer getPluginContainer() {
		return pluginContainer;
	}

	@Listener
	public void on(GameConstructionEvent e) {
		pluginContainer = Sponge.getPluginManager().getPlugin(osmiumPlugin.getId()).get();
		Osmium.getPluginLoader().onLoad(osmiumPlugin);
	}

	@Listener
	public void on(GamePreInitializationEvent e) {
		Osmium.getPluginLoader().onPreInit(osmiumPlugin);
	}

	@Listener
	public void on(GameInitializationEvent e) {
		Osmium.getPluginLoader().onInit(osmiumPlugin);
	}

	@Listener
	public void on(GamePostInitializationEvent e) {
		Osmium.getPluginLoader().onPostInit(osmiumPlugin);
	}

	@Listener
	public void on(GameStoppingEvent e) {
		Osmium.getPluginLoader().onDisable(osmiumPlugin);

		Sponge.getEventManager().unregisterPluginListeners(this);
		Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
		Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
	}

}

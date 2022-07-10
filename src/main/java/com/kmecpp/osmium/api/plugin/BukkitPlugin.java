package com.kmecpp.osmium.api.plugin;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.Reflection;

/**
 * Osmium plugins automatically subclass this class as their entry point for Bukkit
 */
public abstract class BukkitPlugin extends JavaPlugin {

	private final OsmiumPlugin osmiumPlugin = Osmium.getPluginLoader().createOsmiumPlugin(this); //OsmiumData.constructPlugin();

	public OsmiumPlugin getOsmiumPlugin() {
		return osmiumPlugin;
	}

	@Override
	public void onLoad() {
		Osmium.getPluginLoader().onLoad(osmiumPlugin);
	}

	@Override
	public void onEnable() {
		Osmium.getPluginLoader().onPreInit(osmiumPlugin);
		Osmium.getPluginLoader().onInit(osmiumPlugin);
		injectCommandMeta();
		Osmium.getPluginLoader().onPostInit(osmiumPlugin);
	}

	@Override
	public void onDisable() {
		Osmium.getPluginLoader().onDisable(osmiumPlugin);
	}

	private void injectCommandMeta() {
		try {
			Map<String, Map<String, Object>> originalCommandMap = this.getDescription().getCommands();
			Map<String, Map<String, Object>> commands = originalCommandMap != null ? new HashMap<>(originalCommandMap) : new HashMap<>();
			for (Command command : Osmium.getCommandManager().getCommands(osmiumPlugin)) {
				HashMap<String, Object> commandProperties = new HashMap<>();
				commandProperties.put("aliases", command.getAliases());
				commandProperties.put("description", command.getDescription());
				commandProperties.put("permission", command.getPermission());
				commandProperties.put("usage", command.getUsage());
				commands.put(command.getPrimaryAlias(), commandProperties);
			}
			Reflection.setField(this.getDescription(), "commands", commands);
		} catch (Throwable t) {
			OsmiumLogger.error("Failed to update command meta at runtime for plugin: " + this.getName());
			t.printStackTrace();
		}
	}

}

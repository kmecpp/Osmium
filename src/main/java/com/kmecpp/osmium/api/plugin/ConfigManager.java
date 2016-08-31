package com.kmecpp.osmium.api.plugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.spongepowered.api.config.ConfigRoot;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.plugin.ConfigurationSpec.IConfigSpec;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private HashMap<CommentedConfigurationNode, Object> defaultNodes = new HashMap<CommentedConfigurationNode, Object>();

	private ConfigurationLoader<CommentedConfigurationNode> loader;
	private CommentedConfigurationNode root;

	public ConfigManager(ConfigRoot configRoot) {
		loader = configRoot.getConfig();
		try {
			ConfigurationSpec configSpec = OsmiumPlugin.getPlugin().getConfigurationSpec();
			//			if (configSpec == null) {
			//				configSpec = new ConfigurationSpec() {
			//
			//					@Override
			//					public String getHeader() {
			//						return SpongeCore.getPlugin().getPluginName() + " configuration file.";
			//					}
			//
			//					@Override
			//					public void populate(CommentedConfigurationNode root) {
			//					}
			//
			//				};
			//			}
			root = loader.load(ConfigurationOptions.defaults().setHeader(configSpec.getHeader()));

			//			CommentedConfigurationNode defaults = loader.createEmptyNode();

			IConfigSpec spec = (path) -> { //Implements setDefault
				CommentedConfigurationNode node = root.getNode(path);
				return node.isVirtual() ? node : loader.createEmptyNode();
				//				defaultNodes.put(node, node.getValue()); //TODO is modifiable in list?
				//				return node;
			};
			configSpec.populate(spec);

			defaultNodes.forEach((node, value) -> {
				Osmium.log(Arrays.toString(node.getPath()) + ": " + value);
				if (node.isVirtual()) {
				}
			});

			save(); //If loaded successfully
		} catch (IOException e) {
			Osmium.getLogger().error("Unable to load configuration file!");
			e.printStackTrace();
		}
		//		loader = HoconConfigurationLoader.builder().setPath(path).build();
		//		if (Files.exists(path)) {
		//			this.root = loader.load();
		//		} else {
		//			root = loader.createEmptyNode(ConfigurationOptions.defaults());
		//			getMaxClients().setValue(100);
		//		}
	}

	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getRoot() {
		return root;
	}

	public CommentedConfigurationNode getNode(Object... path) {
		return root.getNode(path);
	}

	/**
	 * Saves the configuration file
	 */
	public void save() {
		try {
			loader.save(root);
		} catch (IOException e) {
			Osmium.getLogger().error("Unable to save the configuration file!", e);
		}
	}

}

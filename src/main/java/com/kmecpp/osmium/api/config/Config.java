package com.kmecpp.osmium.api.config;

import java.io.File;
import java.io.IOException;

import com.kmecpp.osmium.api.logging.OsmiumLogger;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Config {

	private File file;

	private ConfigurationLoader<CommentedConfigurationNode> loader;
	private CommentedConfigurationNode root;

	public Config(String path) {
		this(new File(path));
	}

	public Config(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	/**
	 * Reloads the configuration file
	 */
	public void reload() {
		//		try {
		//			loader = HoconConfigurationLoader.builder()
		//					.setPath(Platform.getPluginFolder().resolve("config.conf"))
		//					.build();
		//
		//			ConfigProperties spongeConfig = plugin.getConfig().getAnnotation(ConfigProperties.class);
		//			root = spongeConfig != null
		//					? loader.load(ConfigurationOptions.defaults().setHeader(spongeConfig.header()))
		//					: loader.load();
		//
		//			keys = Arrays.stream(plugin.getConfig().getDeclaredFields())
		//					.filter((field) -> Modifier.isStatic(field.getModifiers()) && field.getType() == ConfigKey.class)
		//					.map((field) -> {
		//						try {
		//							field.setAccessible(true);
		//							ConfigKey key = (ConfigKey) field.get(null);
		//
		//							//Load defaults
		//							CommentedConfigurationNode node = root.getNode(key.getKey());
		//							if (node.isVirtual()) {
		//								node.setValue(key.getDefaultValue()); //Set default value if the node does not exist
		//							}
		//							key.setValue(node.getValue()); //Load stored value into the ConfigKey
		//							if (!key.getComment().equals("")) {
		//								node.setComment(key.getComment());//Overwrite comment after value has been set
		//							}
		//							return key;
		//						} catch (IllegalArgumentException | IllegalAccessException e) {
		//							throw new RuntimeException(e);
		//						}
		//					})
		//					.collect(Collectors.toList());
		//			save(plugin);
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
	}

	//	/**
	//	 * Gets the value for the given configuration key
	//	 * 
	//	 * @param e
	//	 *            the key to get
	//	 * @return the value for the key
	//	 */
	//	public static ObjectValue getValue(Enum<?> e) {
	//		validateLoaded();
	//		for (ConfigKey key : keys) {
	//			if (key.getKey().equals(e.name())) {
	//				return ObjectValue.of(key);
	//			}
	//		}
	//		throw new RuntimeException("Config key '" + e.name() + "'does not exist!");
	//	}
	//
	//	/**
	//	 * Sets the value for the given configuration keys
	//	 * 
	//	 * @param key
	//	 *            the key to update
	//	 * @param value
	//	 *            the new value for the key
	//	 */
	//	public static void setValue(ConfigKey key, Object value) {
	//		validateLoaded();
	//		root.getNode(key.getPath()).setValue(value);
	//	}

	/**
	 * Saves the configuration file
	 */
	public void save() {
		validateLoaded();
		try {
			loader.save(root);
		} catch (IOException e) {
			OsmiumLogger.error("Unable to save the configuration file!");
			e.printStackTrace();
		}
	}

	/**
	 * Validates that the {@link Config} has been loaded
	 * successfully
	 */
	private void validateLoaded() {
		if (loader == null) {
			throw new RuntimeException("ConfigManager not yet initialized! Must call reload() to load the manager!");
		}
	}

}

package com.kmecpp.osmium.api.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map.Entry;

import com.kmecpp.osmium.api.logging.OsmiumLogger;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigData extends ConfigClassData {

	protected final Path path;
	private ConfigurationLoader<CommentedConfigurationNode> loader;
	private CommentedConfigurationNode root;

	ConfigData(PluginConfigTypeData pluginData, Class<?> configClass, ConfigClass configProperties, Path path, HashMap<String, FieldData> fieldData) {
		super(pluginData, configClass, configProperties, fieldData);
		this.path = path;
	}

	public void load() throws IOException {
		loader = HoconConfigurationLoader.builder().setPath(path).build();

		if (!Files.exists(path)) {
			Files.createDirectories(path.getParent());
			root = loader.createEmptyNode();
			save();
		} else {
			try {
				root = loader.load();
			} catch (IOException e) {
				OsmiumLogger.warn("An error occurred while loading config for: " + configClass.getName());
				throw new IOException(e);
			}
		}

		boolean mustSave = false;
		for (Entry<String, FieldData> entry : fieldData.entrySet()) {
			Object[] virtualPath = entry.getKey().split("\\.");

			FieldData fieldData = entry.getValue();
			FieldTypeData typeData = fieldData.getTypeData();

			//			System.out.println("LOADING PATH: " + entry.getKey());

			CommentedConfigurationNode node = root.getNode(virtualPath);

			try {
				if (node.isVirtual() && !fieldData.isDeletable()) {
					Object defaultValue = fieldData.getFieldValue();
					if (defaultValue == null) {
						defaultValue = fieldData.getFieldValue();
						fieldData.setValue(defaultValue);
					}
					if (defaultValue != null) {
						mustSave = true;
					}
					fieldData.setValue(defaultValue);
				} else {
					fieldData.setValue(typeData.convertToActualType(node.getValue(), pluginData));
				}
			} catch (Exception e) {
				OsmiumLogger.error("An error occurred while loading config: " + configClass.getName());
				e.printStackTrace();
			}
		}

		if (mustSave) {
			save();
		}
	}

	public void save() throws IOException {
		if (root == null) {
			load();
		}

		for (Entry<String, FieldData> entry : fieldData.entrySet()) {
			Object[] virtualPath = entry.getKey().split("\\.");
			FieldData fieldData = entry.getValue();

			Object value = fieldData.getFieldValue();
			if (value == null) {
				value = ConfigSerialization.getDefaultFor(fieldData.getType());
			}
			CommentedConfigurationNode node = root.getNode(virtualPath);

			FieldTypeData typeData = fieldData.getTypeData();
			node.setComment(fieldData.getComment());
			node.setValue(typeData.convertToConfigurateType(value));

			//			Object value = typeData.convertToActualType(node.getValue());
			//			fieldData.setValue(value);
			//			if (value == null || value.getClass().getPackage().getName().startsWith("java.lang")
			//					|| value instanceof Map || value instanceof Collection) {
			//				node.setValue(value);
			//			} else {
			//				node.setValue(ObjectMapSerialization.serialize(value));
			//			}
		}
		loader.save(root);
	}

}

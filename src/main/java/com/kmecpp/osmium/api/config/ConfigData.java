package com.kmecpp.osmium.api.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map.Entry;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigData extends ConfigClassData {

	private ConfigurationLoader<CommentedConfigurationNode> loader;
	private CommentedConfigurationNode root;

	ConfigData(Class<?> configClass, ConfigClass configProperties, Path path, HashMap<String, FieldData> fieldData) {
		super(configClass, configProperties, path, fieldData);
	}

	public void load() throws IOException {
		loader = HoconConfigurationLoader.builder().setPath(path).build();

		if (!Files.exists(path)) {
			Files.createDirectories(path.getParent());
			root = loader.createEmptyNode();
			save();
		} else {
			root = loader.load();
		}

		for (Entry<String, FieldData> entry : fieldData.entrySet()) {
			Object[] virtualPath = entry.getKey().split("\\.");

			FieldData fieldData = entry.getValue();
			TypeData typeData = fieldData.getTypeData();

			System.out.println("LOADING PATH: " + entry.getKey());

			CommentedConfigurationNode node = root.getNode(virtualPath);

			try {
				fieldData.setValue(typeData.convertToActualType(node.getValue()));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
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

			TypeData typeData = fieldData.getTypeData();
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

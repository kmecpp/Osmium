package com.kmecpp.osmium.api.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.typesafe.config.ConfigRenderOptions;

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
		Files.createDirectories(path.getParent());
		loader = HoconConfigurationLoader.builder().setRenderOptions(ConfigRenderOptions.defaults()).setPath(path).build();

		if (!Files.exists(path)) {
			root = loader.createEmptyNode();
			save();
		} else {
			root = loader.load();
		}

		for (Entry<String, FieldData> entry : fieldData.entrySet()) {
			Object[] virtualPath = entry.getKey().split("\\.");

			FieldData data = entry.getValue();
			TypeData typeData = data.getTypeData();

			CommentedConfigurationNode node = root.getNode(virtualPath);

			try {
				Object value = typeData.convert(node.getValue());
				data.setValue(value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public void save() throws IOException {
		for (Entry<String, FieldData> entry : fieldData.entrySet()) {
			Object[] virtualPath = entry.getKey().split("\\.");
			FieldData data = entry.getValue();

			Object value = data.getFieldValue();
			if (value == null) {
				value = ConfigSerialization.getDefaultFor(data.getType());
			}
			CommentedConfigurationNode node = root.getNode(virtualPath);

			if (value == null || value.getClass().getPackage().getName().startsWith("java.lang") || value instanceof Map || value instanceof Collection) {
				node.setValue(value);
			} else {
				node.setValue(ObjectSerialization.serialize(value));
			}
		}
		loader.save(root);
	}

}

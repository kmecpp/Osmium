package com.kmecpp.osmium.api.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.HashMap;

import com.kmecpp.osmium.api.logging.OsmiumLogger;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class ConfigManager {

	private final HashMap<Class<?>, ConfigData> configs = new HashMap<>();

	public VirtualConfig load(Path path) throws IOException {
		HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
				.setDefaultOptions(ConfigurationOptions.defaults())
				.setPath(path)
				.build();

		return new VirtualConfig(path, loader, loader.load());
	}

	public void load(Class<?> config) throws IOException {
		ConfigData data = getConfigData(config);
		File file = new File(data.getProperties().path());
		new ConfigParser(data, file).load();
	}

	public void save(Class<?> config) throws IOException {
		ConfigData data = getConfigData(config);
		File file = new File(data.getProperties().path());
		new ConfigWriter(data, file).write();
	}

	private ConfigData getConfigData(Class<?> config) {
		ConfigData data = configs.get(config);
		if (data == null) {
			ConfigProperties properties = config.getAnnotation(ConfigProperties.class);
			if (properties == null) {
				throw new IllegalArgumentException("Configuration class must be annotated with @" + ConfigProperties.class.getSimpleName());
			}
			data = new ConfigData(config, properties);
			loadFields(config, data.getFields(), data.getRoot());
		}
		return data;
	}

	private void loadFields(Class<?> cls, HashMap<String, ConfigField> fields, Block block) {
		for (Field field : cls.getDeclaredFields()) {
			field.setAccessible(true);
			Setting setting = field.getAnnotation(Setting.class);
			if (setting == null) {
				continue;
			}
			if (!Modifier.isStatic(field.getModifiers())) {
				OsmiumLogger.warn("Invalid configuration setting! Must be declared static: " + field);
				continue;
			}

			ConfigField configField = new ConfigField(field, setting);
			String key = block.getPath().isEmpty() ? configField.getName() : block.getPath() + "." + configField.getName();
			fields.put(key, configField);
			block.addField(configField);
		}

		for (Class<?> nested : cls.getDeclaredClasses()) {
			loadFields(nested, fields, block.createChild(nested.getSimpleName()));
		}
	}

}

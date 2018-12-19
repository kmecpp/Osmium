package com.kmecpp.osmium.api.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.HashMap;

import com.kmecpp.osmium.Osmium;
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
		File file = Osmium.getPlugin(config).getFolder().resolve(data.getProperties().path()).toFile();
		if (!new ConfigParser(data, file).load()) {
			//If the file is missing settings
			if (!data.getProperties().allowKeyRemoval()) {
				new ConfigWriter(data, file).write();
			}
		}
	}

	public void save(Class<?> config) throws IOException {
		ConfigData data = getConfigData(config);
		File file = Osmium.getPlugin(config).getFolder().resolve(data.getProperties().path()).toFile();
		new ConfigWriter(data, file).write(); //File handling is done by the writer
	}

	public ConfigData getConfigData(Class<?> config) {
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
		StringBuilder sb = new StringBuilder();
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
			String rawKey = block.getPath().isEmpty() ? configField.getName() : block.getPath() + "." + configField.getName();

			sb.setLength(0);
			writeKey(sb, rawKey);
			fields.put(sb.toString(), configField);
			block.addField(configField);
		}

		for (Class<?> nested : cls.getDeclaredClasses()) {
			sb.setLength(0);
			writeKey(sb, nested.getSimpleName());
			loadFields(nested, fields, block.createChild(sb.toString()));
		}
	}

	public static void writeKey(StringBuilder sb, String key) {
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i > 0) {
					sb.append('-');
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
	}

}

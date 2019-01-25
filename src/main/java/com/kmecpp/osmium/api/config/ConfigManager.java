package com.kmecpp.osmium.api.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.bukkit.configuration.InvalidConfigurationException;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.config.ConfigFormatWriter.ConfigFormat;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.persistence.Deserializer;
import com.kmecpp.osmium.api.persistence.Serializer;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.core.CoreOsmiumConfig;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

public class ConfigManager {

	private final HashMap<OsmiumPlugin, HashSet<Class<?>>> plugins = new HashMap<>();
	private final HashMap<Class<?>, ConfigData> configs = new HashMap<>();

	public static void main(String[] args) throws IOException, InvalidConfigurationException {
		ConfigManager m = new ConfigManager();

		ConfigData data = m.getConfigData(CoreOsmiumConfig.class);
		//		new ConfigWriter(data, new File("config.yml")).write(); //File handling is done by the writer
		new ConfigFormatWriter(data, new File("config.yml"), ConfigFormats.HOCON).write();
		long start = System.currentTimeMillis();
		//		YamlConfiguration yml = new YamlConfiguration();
		//		yml.load(new File("config.yml"));
		System.out.println(data.getRoot().getBlocks());
		//		yml.save(new File("config.yml"));

		HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
				.setPath(Paths.get("config.yml"))
				.build();

		loader.load();
		for (Entry<String, ConfigField> entry : data.getFields().entrySet()) {
			//			entry.getValue().setValue(yml.get(entry.getKey()));
		}
		//		m.load(Paths.get("config.yml"));
		//		new ConfigParser(data, new File("config.conf")).load();
		System.out.println("TIME: " + (System.currentTimeMillis() - start) + "ms");
	}

	public void registerConfig(OsmiumPlugin plugin, Class<?> config) {
		plugins.putIfAbsent(plugin, new HashSet<>());
		plugins.get(plugin).add(config);
	}

	public HashSet<Class<?>> getPluginConfigs(OsmiumPlugin plugin) {
		return plugins.getOrDefault(plugin, new HashSet<>());
	}

	public VirtualConfig load(Path path) throws IOException {
		HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
				.setPath(path)
				.build();

		return new VirtualConfig(path, loader, loader.load());
	}

	public ConfigurationNode loadYaml(Path path) throws IOException {
		YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder()
				.setPath(path)
				.build();

		return loader.load();
		//		return new VirtualConfig(path, loader, loader.load());
	}

	public void load(Class<?> config) throws IOException {
		ConfigData data = getConfigData(config);
		OsmiumPlugin plugin = Osmium.getPlugin(config);
		registerConfig(plugin, config);
		File file = plugin.getFolder().resolve(data.getProperties().path()).toFile();
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

	public void save(Class<?> config, ConfigFormat format) throws IOException {
		ConfigData data = getConfigData(config);
		File file = Osmium.getPlugin(config).getFolder().resolve(data.getProperties().path()).toFile();
		new ConfigFormatWriter(data, file, format).write(); //File handling is done by the writer
	}

	public ConfigData getConfigData(Class<?> config) {
		ConfigData data = configs.get(config);
		if (data == null) {
			ConfigProperties properties = config.getAnnotation(ConfigProperties.class);
			if (properties == null) {
				throw new IllegalArgumentException("Configuration class must be annotated with @" + ConfigProperties.class.getSimpleName());
			}
			data = new ConfigData(config, properties);
			loadFields(data.getRoot(), config.getDeclaredFields(), config.getDeclaredClasses(), data.getFields());
		}
		return data;
	}

	private void loadFields(Block block, Field[] declaredFields, Class<?>[] declaredClasses, HashMap<String, ConfigField> fields) {
		for (Field field : declaredFields) {
			field.setAccessible(true);
			Setting setting = field.getAnnotation(Setting.class);
			if (setting == null) {
				continue;
			} else if (field.isAnnotationPresent(Transient.class)) {
				continue;
			} else if (!Modifier.isStatic(field.getModifiers())) {
				OsmiumLogger.warn("Invalid configuration setting! Must be declared static: " + field);
				continue;
			}

			ConfigField configField = new ConfigField(field, setting);
			String rawKey = block.getPath().isEmpty() ? configField.getName() : block.getPath() + "." + configField.getName();
			fields.put(getKey(rawKey), configField);
			block.addField(configField);
		}

		for (Class<?> nested : declaredClasses) {
			if (nested.isAnnotationPresent(Transient.class)) {
				continue;
			}
			Field[] nestedFields = nested.getDeclaredFields();
			Class<?>[] nestedClasses = nested.getDeclaredClasses();
			loadFields(block.createChild(getKey(nested.getSimpleName())), nestedFields, nestedClasses, fields);
		}
	}

	public <T> void registerType(Class<T> cls, Deserializer<T> deserializer) {
		ConfigTypes.register(cls, deserializer);
	}

	public <T> void registerType(Class<T> cls, Serializer<T> serializer, Deserializer<T> deserializer) {
		ConfigTypes.register(cls, serializer, deserializer);
	}

	private static final StringBuilder sb = new StringBuilder();

	public static String getKey(String key) {
		sb.setLength(0);
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
		return sb.toString();
	}

}

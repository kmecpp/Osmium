package com.kmecpp.osmium.api.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Collection;
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
import com.kmecpp.osmium.api.util.StringUtil;
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
		ConfigFormat format = ConfigFormats.HOCON;
		ConfigFormatWriter w = new ConfigFormatWriter(data, new File("config.yml"), format);
		//		VirtualConfig v = m.load(Paths.get("config.yml"), ConfigFormats.YAML);
		long start = System.currentTimeMillis();
		//		v.save();
		//		System.out.println(data.getRoot().getBlocks());
		//		System.out.println(data.getRoot().getFields());
		w.write();

		//		new ConfigParser(data, new File("config.yml")).load();

		//		YamlConfiguration yml = new YamlConfiguration();
		//		yml.load(new File("config.yml"));
		//		System.out.println(yml.get("test"));

		//		yml.save(new File("config.yml"));

		//		HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
		//				.setPath(Paths.get("config.yml"))
		//				.build();
		//
		//		loader.load();

		//		m.load(Paths.get("config.yml"));
		//		new ConfigParser(data, new File("config.conf")).load();
		System.out.println("Write: " + (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		new ConfigParser(data, new File("config.yml")).load();
		System.out.println("Read: " + (System.currentTimeMillis() - start) + "ms");
	}

	public void registerConfig(OsmiumPlugin plugin, Class<?> config) {
		plugins.putIfAbsent(plugin, new HashSet<>());
		plugins.get(plugin).add(config);
	}

	public HashSet<Class<?>> getPluginConfigs(OsmiumPlugin plugin) {
		return plugins.getOrDefault(plugin, new HashSet<>());
	}

	public static ConfigFormat getFormat() {
		if (StringUtil.equalsIgnoreCase(CoreOsmiumConfig.configFormat, "YAML", "YML")) {
			return ConfigFormats.YAML;
		}
		return ConfigFormats.HOCON;
	}

	private VirtualConfig load(Path path) throws IOException {
		return this.load(path, getFormat());
	}

	private VirtualConfig load(Path path, ConfigFormat format) throws IOException {
		if (format == ConfigFormats.HOCON) {
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
					.setPath(path)
					.build();
			return new VirtualConfig(path, loader, loader.load());
		} else if (format == ConfigFormats.YAML) {
			YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder()
					.setPath(path)
					.build();

			return new VirtualConfig(path, loader, loader.load());
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void load(Class<?> config) throws IOException {
		ConfigData data = getConfigData(config);
		OsmiumPlugin plugin = Osmium.getPlugin(config);
		VirtualConfig c = this.load(plugin.getFolder().resolve(data.getProperties().path()));
		for (Entry<String, ConfigField> entry : data.getFields().entrySet()) {
			ConfigField field = entry.getValue();
			Class<?> type = field.getType();
			ConfigurationNode node = c.getNode(entry.getKey());
			Object value = node.getValue();
			if (value instanceof String) {
				Deserializer<?> d = ConfigTypes.getDeserializer(type);
				if (d != null) {
					entry.getValue().setValue(d.deserialize((String) value));
					continue;
				}
			} else if (node.isVirtual()) {
				if (!field.getSetting().deletable()) {
					plugin.warn("Missing config setting: " + field.getJavaPath());
				}
				continue;
			} else if (Collection.class.isAssignableFrom(field.getType())) {
				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>) field.getValue();
				for (Object e : (Collection<?>) value) {
					collection.add(e);
				}
				continue;
			}

			field.setValue(value);
		}
	}

	public void loadWithParser(Class<?> config) throws IOException {
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
			//			Setting setting = field.getAnnotation(Setting.class);
			//			if (setting == null) {
			//				continue;
			//			} else

			if (field.isAnnotationPresent(Transient.class)) {
				continue;
			} else if (!Modifier.isStatic(field.getModifiers())) {
				OsmiumLogger.warn("Invalid configuration setting! Must be declared static: " + field);
				continue;
			}

			ConfigField configField = new ConfigField(field, field.getAnnotation(Setting.class));
			String rawKey = block.getPath().isEmpty() ? configField.getName() : block.getPath() + "." + configField.getName();
			fields.put(getKey(rawKey), configField);
			block.addField(configField);
		}

		for (Class<?> nested : declaredClasses) {
			if (nested.isAnnotationPresent(Transient.class) || nested.isAnnotationPresent(ConfigType.class)) {
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
		writeKey(sb, key);
		return sb.toString();
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

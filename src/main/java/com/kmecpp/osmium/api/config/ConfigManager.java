package com.kmecpp.osmium.api.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.google.common.reflect.TypeToken;
import com.kmecpp.osmium.Directory;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.persistence.Deserializer;
import com.kmecpp.osmium.api.persistence.Serializer;
import com.kmecpp.osmium.api.util.FileUtil;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

public class ConfigManager {

	private final HashMap<Class<?>, OsmiumConfig> configs = new HashMap<>();

	public ConfigManager() {
		registerDeseralizer(UUID.class, UUID::fromString);
	}

	public HashMap<Class<?>, OsmiumConfig> getConfigs() {
		return configs;
	}

	public boolean isLoaded(Class<?> cls) {
		return configs.containsKey(cls);
	}

	public <T> void registerDeseralizer(Class<T> cls, Deserializer<T> deserializer) {
		registerSerialization(cls, (obj) -> String.valueOf(obj), deserializer);
	}

	public <T> void registerSerialization(Class<T> cls, Serializer<T> serializer, Deserializer<T> deserializer) {
		TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(cls), new TypeSerializer<T>() {

			@Override
			public T deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode node) throws ObjectMappingException {
				return deserializer.deserialize(node.getString());
			}

			@Override
			public void serialize(@NonNull TypeToken<?> type, T obj, @NonNull ConfigurationNode node) throws ObjectMappingException {
				node.setValue(serializer.serialize(obj));
			}

		});
	}

	public void load(Class<?> configClass) throws IOException {
		Configuration properties = getProperties(configClass);
		boolean save = FileUtil.createFile(getPath(configClass).toFile());

		OsmiumConfig config = configs.get(configClass);
		Path path = getPath(configClass);
		if (config == null) {
			ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
					.setDefaultOptions(ConfigurationOptions.defaults().setHeader(properties.header()))
					.setPath(path)
					.build();
			long start = System.currentTimeMillis();
			CommentedConfigurationNode root = loader.load();
			System.out.println("File Read: " + (System.currentTimeMillis() - start) + "ms");
			config = new OsmiumConfig(configClass, path, loader, root);

		}
		long start = System.currentTimeMillis();
		boolean r = config.reload();
		System.out.println(save + ", " + r);
		save |= r;
		System.out.println("Field Update: " + (System.currentTimeMillis() - start) + "ms");
		//		updateClass(configClass, config, UpdateMethod.LOAD);

		if (save) {
			OsmiumLogger.debug("Saving config after load");
			config.save(true);
		}
	}

	public void save(Class<?> configClass) throws IOException {
		if (!configClass.isAnnotationPresent(Configuration.class)) {
			throw new IllegalArgumentException("Failed to save config. Configuration classes must be annotated with @" + Configuration.class.getSimpleName());
		}
		OsmiumConfig config = configs.get(configClass);

		if (config == null) {
			throw new IllegalStateException("That configuration has never been loaded!");
		}

		config.save(false);
	}

	//	private void update(Class<?> cls, OsmiumConfig config, UpdateMethod method) {
	//		LinkedList<Class<?>> sectionPath = new LinkedList<>();
	//		sectionPath.add(cls);
	//		updateClass(sectionPath, config, method);
	//	}
	//
	//	private void updateClass(String sectionPath, Class<?> current, OsmiumConfig config, UpdateMethod method) {
	//		if (current.getClasses().length == 0) {
	//			updateFields(current, config, method);
	//			return;
	//		}
	//		for (Class<?> nested : current.getClasses()) {
	//			updateFields(nested, config, method);
	//			updateClass(sectionPath + "." + , nested, config, method);
	//		}
	//	}
	//
	//	private void updateFields(Class<?> cls, OsmiumConfig config, UpdateMethod method) {
	//		for (Field field : cls.getDeclaredFields()) {
	//			Setting setting = field.getAnnotation(Setting.class);
	//			if (setting != null) {
	//				if (!Modifier.isStatic(field.getModifiers())) {
	//					OsmiumLogger.warn("Invalid configuration setting! Must be declared static: " + field);
	//					continue;
	//				}
	//
	//				String nodePath = getPath(field, setting);
	//				field.setAccessible(true);
	//				CommentedConfigurationNode node = config.getNode(nodePath);
	//				try {
	//					if (method == UpdateMethod.INIT || method == UpdateMethod.LOAD) {
	//						if (!node.isVirtual()) {
	//							field.set(null, node.getValue());
	//						}
	//					} else {
	//						if (method == UpdateMethod.INIT && !setting.comment().isEmpty()) {
	//							node.setComment(setting.comment());
	//						}
	//
	//						node.setValue(field.get(null));
	//					}
	//				} catch (Exception e) {
	//					e.printStackTrace(); //Exceptions shouldn't be thrown here
	//				}
	//			}
	//		}
	//	}
	//
	//	private static enum UpdateMethod {
	//
	//		INIT,
	//		LOAD,
	//		SAVE;
	//
	//	}

	public Path getPath(Class<?> configClass) {
		//			Path path = Paths.get("plugins", Osmium.getPlugin(configClass).getName(), properties.path());
		return Directory.pluginFolder(Osmium.getPlugin(configClass)).resolve(getProperties(configClass).path());
	}

	public Configuration getProperties(Class<?> configClass) {
		Configuration properties = configClass.getAnnotation(Configuration.class);
		if (properties == null) {
			throw new IllegalArgumentException("Configuration classes must be annotated with @" + Configuration.class.getSimpleName());
		}
		return properties;
	}

	//	private static String getPath(ConfigurationNode node) {
	//		StringBuilder sb = new StringBuilder();
	//		for (Object part : node.getPath()) {
	//			sb.append(String.valueOf(part) + ".");
	//		}
	//		sb.setLength(sb.length() - 1);
	//		return sb.toString();
	//	}

	//	private String getPath(Field field, Setting setting) {
	//		String parent = (setting.parent().isEmpty() ? "" : setting.parent() + ".");
	//		if (!setting.name().isEmpty()) {
	//			return parent + setting.name();
	//		}
	//
	//		StringBuilder sb = new StringBuilder();
	//		for (int i = 0; i < field.getName().length(); i++) {
	//			char c = field.getName().charAt(i);
	//			if (i > 0 && Character.isUpperCase(c)) {
	//				sb.append("-");
	//			}
	//			sb.append(Character.toLowerCase(c));
	//		}
	//		return parent + sb.toString();
	//	}

}

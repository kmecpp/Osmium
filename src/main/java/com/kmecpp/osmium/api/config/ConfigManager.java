package com.kmecpp.osmium.api.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import javax.inject.Singleton;

import com.kmecpp.osmium.Directory;
import com.kmecpp.osmium.Osmium;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Singleton
public class ConfigManager {

	private final HashMap<Class<?>, OsmiumConfig> configs = new HashMap<>();

	public HashMap<Class<?>, OsmiumConfig> getConfigs() {
		return configs;
	}

	public boolean isLoaded(Class<?> cls) {
		return configs.containsKey(cls);
	}

	public void load(Class<?> configClass) throws IOException {
		long start = System.currentTimeMillis();
		Configuration properties = getProperties(configClass);

		boolean firstSave = false;
		File file = getPath(configClass).toFile();
		if (!file.exists()) {
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
			firstSave = true;
		}

		OsmiumConfig config = configs.get(configClass);
		if (config == null) {
			ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
					.setDefaultOptions(ConfigurationOptions.defaults().setHeader(properties.header()))
					.setPath(getPath(configClass))
					.build();

			long s = System.currentTimeMillis();
			config = new OsmiumConfig(configClass, loader, loader.load());
			System.out.println("File Read: " + (System.currentTimeMillis() - s) + "ms");
		}
		config.reload();

		//		updateClass(configClass, config, UpdateMethod.LOAD);

		if (firstSave) {
			config.save(true);
		}
		System.out.println("Load Time: " + (System.currentTimeMillis() - start) + "ms");
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

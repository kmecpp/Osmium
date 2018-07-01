package com.kmecpp.osmium.api.config;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Singleton;

import com.kmecpp.osmium.OsmiumLogger;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Singleton
public class ConfigManager {

	public void loadConfig(Class<?> configClass) {
		update(configClass, true);
	}

	public void saveConfig(Class<?> configClass) {
		update(configClass, false);
	}

	public void update(Class<?> configClass, boolean loadOnly) {
		try {
			boolean firstSave = false;
			Configuration properties = getConfigProperties(configClass);

			Path path = Paths.get(properties.path());
			File file = path.toFile();
			if (!file.exists()) {
				if (file.getParentFile() != null) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				loadOnly = false;
				firstSave = true;
			}

			ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(path).build();
			CommentedConfigurationNode root = loader.load();
			if (firstSave) {
				root.setComment(properties.header());
			}

			for (Field field : configClass.getDeclaredFields()) {
				Setting setting = field.getAnnotation(Setting.class);
				if (setting != null) {
					if (!Modifier.isStatic(field.getModifiers())) {
						OsmiumLogger.warn("Configuration setting must be declared static: " + field);
						continue;
					}

					String nodePath = setting.path().isEmpty() ? getDefaultPath(field) : setting.path();
					field.setAccessible(true);
					CommentedConfigurationNode node = root.getNode(nodePath);
					if (loadOnly) {
						if (!node.isVirtual()) {
							field.set(null, node.getValue());
						}
					} else {
						if (firstSave) {
							node.setComment(setting.comment());
						}

						node.setValue(field.get(null));
					}
				}
			}
			if (!loadOnly) {
				loader.save(root);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Configuration getConfigProperties(Class<?> cls) {
		Configuration properties = cls.getAnnotation(Configuration.class);
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

	private String getDefaultPath(Field field) {
		String name = field.getName();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (i > 0 && Character.isUpperCase(c)) {
				sb.append("-");
			}
			sb.append(Character.toLowerCase(c));
		}
		return sb.toString();
	}

}

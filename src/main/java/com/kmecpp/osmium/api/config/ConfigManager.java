package com.kmecpp.osmium.api.config;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;

import javax.inject.Singleton;

import com.kmecpp.osmium.Directory;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.logging.OsmiumLogger;

import ninja.leaping.configurate.ConfigurationOptions;
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

			//			Path path = Paths.get("plugins", Osmium.getPlugin(configClass).getName(), properties.path());
			Path path = Directory.pluginFolder(Osmium.getPlugin(configClass)).resolve(properties.path());
			File file = path.toFile();
			if (!file.exists()) {
				if (file.getParentFile() != null) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				loadOnly = false;
				firstSave = true;
			}

			ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
					.setDefaultOptions(ConfigurationOptions.defaults().setHeader(properties.header()))
					.setPath(path)
					.build();

			CommentedConfigurationNode root = loader.load();

			for (Field field : configClass.getDeclaredFields()) {
				Setting setting = field.getAnnotation(Setting.class);
				if (setting != null) {
					if (!Modifier.isStatic(field.getModifiers())) {
						OsmiumLogger.warn("Configuration setting must be declared static: " + field);
						continue;
					}

					String nodePath = getPath(field, setting);
					field.setAccessible(true);
					CommentedConfigurationNode node = root.getNode((Object[]) nodePath.split("\\."));
					if (loadOnly) {
						if (!node.isVirtual()) {
							field.set(null, node.getValue());
						}
					} else {
						if (firstSave && !setting.comment().isEmpty()) {
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

	private String getPath(Field field, Setting setting) {
		String parent = (setting.parent().isEmpty() ? "" : setting.parent() + ".");
		if (!setting.name().isEmpty()) {
			return parent + setting.name();
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < field.getName().length(); i++) {
			char c = field.getName().charAt(i);
			if (i > 0 && Character.isUpperCase(c)) {
				sb.append("-");
			}
			sb.append(Character.toLowerCase(c));
		}
		return parent + sb.toString();
	}

}

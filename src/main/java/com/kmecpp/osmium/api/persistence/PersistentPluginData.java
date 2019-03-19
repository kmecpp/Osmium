package com.kmecpp.osmium.api.persistence;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;

import com.kmecpp.osmium.api.config.DataFile;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class PersistentPluginData {

	private OsmiumPlugin plugin;
	private HashSet<Field> fields;

	private DataFile file;

	public PersistentPluginData(OsmiumPlugin plugin) {
		this.plugin = plugin;
		this.fields = new HashSet<>();

		try {
			this.file = new DataFile(plugin.getFolder().resolve("plugin.data"));
		} catch (IOException e) {
			OsmiumLogger.error("Failed to load plugin data! This could have severe consequences!");
			e.printStackTrace();
		}
	}

	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	public DataFile getFile() {
		return file;
	}

	public HashSet<Field> getFields() {
		return fields;
	}

	public void addField(Field field) {
		this.fields.add(field);

		try {
			CommentedConfigurationNode node = file.getNode(getId(field));
			if (node.isVirtual()) {
				return;
			}

			Object value = node.getValue();
			if (value == null && field.getType().isPrimitive()) {
				return;
			}

			field.set(null, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	//	public void reload() {
	//		for (Field field : fields) {
	//			try {
	//				field.set(null, file.getNode(getId(field)).getValue());
	//			} catch (IllegalArgumentException | IllegalAccessException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}

	public void save() {
		OsmiumLogger.debug("Saving persistent data for " + plugin.getName());
		if (fields.isEmpty()) {
			return;
		}

		for (Field field : fields) {
			try {
				file.getNode(getId(field)).setValue(field.get(null));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		try {
			file.save();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static String getId(Field field) {
		return field.getDeclaringClass().getName() + "." + field.getName();
	}

}

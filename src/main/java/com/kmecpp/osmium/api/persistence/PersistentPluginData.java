package com.kmecpp.osmium.api.persistence;

import java.io.IOException;
import java.util.HashSet;

import com.kmecpp.osmium.api.config.DataFile;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class PersistentPluginData {

	private OsmiumPlugin plugin;
	private HashSet<PersistentField> fields;

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

	public HashSet<PersistentField> getFields() {
		return fields;
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

	public CommentedConfigurationNode load(PersistentField field) {
		fields.add(field);
		CommentedConfigurationNode node = file.getNode("ids." + field.getId());
		if (node.isVirtual()) {
			node = file.getNode("classes." + field.getLocation());
		}
		return node;
	}

	public void save() {
		OsmiumLogger.debug("Saving persistent data for " + plugin.getName());
		if (fields.isEmpty()) {
			return;
		}

		for (PersistentField field : fields) {
			try {
				file.getNode("ids." + field.getId()).setValue(field.getValue());
				file.getNode("classes." + field.getLocation()).setValue(field.getValue());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		try {
			file.save();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}

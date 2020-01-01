package com.kmecpp.osmium.platform.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public abstract class BukkitConfigFile {

	private static final HashMap<Class<?>, BukkitConfigFile> configs = new HashMap<>();

	private OsmiumPlugin plugin;
	private Path path;
	private boolean resource;
	private YamlConfiguration config;

	public BukkitConfigFile(String path) {
		this(path, false);
	}

	public BukkitConfigFile(String path, boolean resource) {
		this.plugin = Osmium.getPlugin();
		this.path = plugin.getFolder().resolve(path);
		this.resource = resource;
		configs.put(this.getClass(), this);
	}

	protected void onLoad(Configuration config) {
	}

	protected void onSave(Configuration config) {
	}

	public final void load() {
		try {
			File file = path.toFile();
			path.getParent().toFile().mkdirs();
			boolean exists = file.exists();
			if (!exists) {
				if (this.resource) {
					try (InputStream in = plugin.getClass().getResourceAsStream("/" + path)) {
						Files.copy(in, this.path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					file.createNewFile();
				}
			}
			config = YamlConfiguration.loadConfiguration(file);
			onLoad(config);
			if (!exists) {
				save();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void save() {
		try {
			onSave(config);
			config.save(path.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final Path getPath() {
		return path;
	}

	public static BukkitConfigFile get(Class<?> cls) {
		return configs.get(cls);
	}

	public static Collection<BukkitConfigFile> getAll() {
		return configs.values();
	}

}

package com.kmecpp.osmium.platform.bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public abstract class BukkitConfigFile extends YamlConfigFile {

	private static final HashMap<Class<?>, BukkitConfigFile> configs = new HashMap<>();

	private OsmiumPlugin plugin;
	protected boolean resource;

	public BukkitConfigFile(String path) {
		this(path, false);
	}

	public BukkitConfigFile(String path, boolean resource) {
		super(null);
		this.plugin = Osmium.getPlugin(this.getClass());
		this.path = plugin != null ? plugin.getFolder().resolve(path) : Paths.get(path).toAbsolutePath();
		this.resource = resource;
		configs.put(this.getClass(), this);
	}

	//	public BukkitConfigFile(Path path, boolean resource) {
	//		super(path);
	//		this.plugin = Osmium.getPlugin(this.getClass());
	//		this.resource = resource;
	//		configs.put(this.getClass(), this);
	//	}

	@Override
	protected void generate() {
		if (this.resource) {
			try (InputStream in = plugin.getClass().getResourceAsStream("/" + path)) {
				Files.copy(in, this.path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static BukkitConfigFile get(Class<?> cls) {
		return configs.get(cls);
	}

	public static Collection<BukkitConfigFile> getAll() {
		return configs.values();
	}

}

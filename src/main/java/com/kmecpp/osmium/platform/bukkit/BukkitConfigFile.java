package com.kmecpp.osmium.platform.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public abstract class BukkitConfigFile extends YamlConfiguration {

	private static final HashMap<Class<?>, BukkitConfigFile> configs = new HashMap<>();

	private OsmiumPlugin plugin;
	private Path path;
	private boolean resource;

	public BukkitConfigFile(String path) {
		this(path, false);
	}

	public BukkitConfigFile(String path, boolean resource) {
		this.plugin = Osmium.getPlugin(this.getClass());
		this.path = plugin != null ? plugin.getFolder().resolve(path) : Paths.get(path).toAbsolutePath();
		this.resource = resource;
		configs.put(this.getClass(), this);
	}

	protected void onLoad() {
	}

	protected void onSave() {
	}

	public final void load() {
		try {
			File file = path.toFile();
			Files.createDirectories(path.getParent());
			boolean exists = file.exists();
			if (!exists) {
				if (this.resource) {
					try (InputStream in = plugin.getClass().getResourceAsStream("/" + path)) {
						Files.copy(in, this.path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				this.load(file);
			}
			this.onLoad();
			if (!exists) {
				save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final void save() {
		try {
			this.onSave();
			this.save(path.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final Path getPath() {
		return path;
	}

	// PERSISTENT VALUES
	public String getPersistentString(String path, String def) {
		return getPersistentObject(path, def);
	}

	public List<String> getPersistentStringList(String path, List<String> def) {
		return getPersistentObject(path, def);
	}

	public int getPersistentInt(String path, int def) {
		return getPersistentObject(path, def);
	}

	public float getPersistentFloat(String path, float def) {
		return getPersistentObject(path, def);
	}

	public double getPersistentDouble(String path, double def) {
		return getPersistentObject(path, def);
	}

	public boolean getPersistentBoolean(String path, boolean def) {
		return getPersistentObject(path, def);
	}

	public <T> T getPersistentObject(String path, T def) {
		return getPersistentObject(path, def, false);
	}

	@SuppressWarnings("unchecked")
	public <T> T getPersistentObject(String path, T def, boolean save) { //The existing method this.get(path, def) will not set the default value, only return it
		if (this.contains(path)) {
			Object val = this.get(path);
			if (val.getClass().isAssignableFrom(def.getClass())) {
				return (T) val;
			} else {
				throw new RuntimeException("Invalid data type! Expected: '" + def.getClass().getSimpleName() + "' found: " + val.getClass().getSimpleName());
			}
		} else {
			this.set(path, def);
			if (save) {
				this.save();
			}
			return def;
		}
	}

	public static BukkitConfigFile get(Class<?> cls) {
		return configs.get(cls);
	}

	public static Collection<BukkitConfigFile> getAll() {
		return configs.values();
	}

}

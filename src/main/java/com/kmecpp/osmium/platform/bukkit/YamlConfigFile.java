package com.kmecpp.osmium.platform.bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class YamlConfigFile extends YamlConfiguration {

	protected Path path;

	public YamlConfigFile(Path path) {
		this.path = path;
	}

	protected void onLoad() {
	}

	protected void onSave() {
	}

	protected void generate() {

	}

	public final void load() {
		try {
			File file = path.toFile();
			Files.createDirectories(path.getParent());
			boolean exists = file.exists();
			if (!exists) {
				generate();
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

	public long getPersistentLong(String path, long def) {
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
			if (val.getClass() == Integer.class && def.getClass() == Long.class) {
				return (T) new Long(val.toString());
			} else if (val.getClass().isAssignableFrom(def.getClass())) {
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

	public Path getPath() {
		return path;
	}

}

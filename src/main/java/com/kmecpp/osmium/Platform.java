package com.kmecpp.osmium;

import com.kmecpp.jlib.StringUtil;

public enum Platform {

	BUKKIT("org.bukkit.Bukkit", "plugin.yml"),
	SPONGE("org.spongepowered.api.Sponge", "mcmod.info");

	private final String className;
	private final String metaFile;
	private final boolean active;

	Platform(String className, String metaFile) {
		this.className = className;
		this.metaFile = metaFile;
		this.active = Reflection.classExists(className); //If running in an IDE both Platforms are active
	}

	public String getName() {
		return StringUtil.capitalize(name());
	}

	public String getMainClass() {
		return className;
	}

	public String getMetaFile() {
		return metaFile;
	}

	public boolean isActive() {
		return active;
	}

	public static boolean exists() {
		return getPlatform() != null;
	}

	public static Platform getPlatform() {
		if (SPONGE.isActive()) {
			return SPONGE; //Primary platform
		} else if (BUKKIT.isActive()) {
			return BUKKIT;
		}
		return null;
	}

	public static void execute(PlatformSpecificExecutor executor) {
		if (SPONGE.isActive()) {
			executor.sponge();
		}
		if (BUKKIT.isActive()) {
			executor.bukkit();
		}
		if (!exists()) {
			throw new Error("No platform is present!");
		}
	}

	public static interface PlatformSpecificExecutor {

		void bukkit();

		void sponge();

	}

}

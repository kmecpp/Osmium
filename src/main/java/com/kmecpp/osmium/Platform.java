package com.kmecpp.osmium;

import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.api.util.StringUtil;

public enum Platform {

	BUKKIT("org.bukkit.Bukkit", "plugin.yml"),
	SPONGE("org.spongepowered.api.Sponge", "mcmod.info"),
	BUNGEE("net.md_5.bungee.BungeeCord", "bungee.yml"),

	;

	private final String className;
	private final String metaFile;
	private final boolean active;

	private static final Platform CURRENT_PLATFORM = BUKKIT.active ? BUKKIT : SPONGE.active ? SPONGE : BUNGEE.active ? BUNGEE : null;
	private static final boolean IS_PROXY = CURRENT_PLATFORM == BUNGEE;

	private Platform(String className, String metaFile) {
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

	public static boolean isBukkit() {
		return BUKKIT.active;
	}

	public static boolean isSponge() {
		return SPONGE.active;
	}

	public static boolean isBungeeCord() {
		return BUNGEE.active;
	}

	public static Platform getPlatform() {
		return CURRENT_PLATFORM;
	}

	public static boolean isGame() {
		return !IS_PROXY;
	}

	public static boolean isProxy() {
		return IS_PROXY;
	}

	public static boolean isDev() {
		return isBukkit() && isSponge();
	}

	public static void execute(Runnable sponge, Runnable bukkit) {
		if (SPONGE.active) {
			sponge.run();
		}
		if (BUKKIT.active) {
			bukkit.run();
		}
	}

	public static void execute(PlatformSpecificExecutor executor) {
		if (SPONGE.active) {
			executor.sponge();
		}
		if (BUKKIT.active) {
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

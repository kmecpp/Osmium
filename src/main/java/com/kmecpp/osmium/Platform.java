package com.kmecpp.osmium;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.spongepowered.api.Sponge;

import com.kmecpp.jlib.utils.StringUtil;
import com.kmecpp.osmium.platform.bukkit.BukkitPlugin;
import com.kmecpp.osmium.platform.sponge.SpongePlugin;

public enum Platform {

	BUKKIT("org.bukkit.Bukkit", "plugin.yml"),
	SPONGE("org.spongepowered.api.Sponge", "mcmod.info");

	private final String className;
	private final String metaFile;
	private final boolean active;

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

	public static boolean exists() {
		return getPlatform() != null;
	}

	public static boolean isBukkit() {
		return BUKKIT.active;
	}

	public static boolean isSponge() {
		return SPONGE.active;
	}

	public static Path getPluginFolder() {
		if (SPONGE.active) {
			return Sponge.getGame().getConfigManager().getPluginConfig(SpongePlugin.getInstance()).getDirectory();
		}
		if (BUKKIT.active) {
			return Paths.get(BukkitPlugin.getInstance().getDataFolder().toURI());
		}
		return null;
	}

	public static Platform getPlatform() {
		if (SPONGE.active) {
			return SPONGE; //Primary platform
		} else if (BUKKIT.active) {
			return BUKKIT;
		}
		return null;
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

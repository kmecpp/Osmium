package com.kmecpp.osmium;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class Directory {

	public static Path pluginFolder(OsmiumPlugin plugin) {
		return plugin.getPluginFolder();
		//		return plugins(plugin.getName());
	}

	public static Path plugins(String... subfolders) {
		Path plugins = Platform.isBukkit() ? Paths.get("plugins") : Paths.get("mods");
		for (String sub : subfolders) {
			plugins = plugins.resolve(sub);
		}
		return plugins;
	}

	public static String getJarFilePath(Class<?> cls) {
		return getJarFile(cls).getName();
	}

	public static JarFile getJarFile(Class<?> cls) {
		//Weird Sponge hack. Not sure why this is necessary
		try {
			return Platform.isSponge()
					? ((JarURLConnection) cls.getProtectionDomain().getCodeSource().getLocation().toURI().toURL().openConnection()).getJarFile()
					: new JarFile(new File(cls.getProtectionDomain().getCodeSource().getLocation().toURI()));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

}

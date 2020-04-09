package com.kmecpp.osmium;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class Directory {

	public static Path pluginFolder(OsmiumPlugin plugin) {
		return plugin.getFolder();
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
		JarFile jarFile = getJarFile(cls);
		return jarFile != null ? jarFile.getName() : null;
	}

	public static JarFile getJarFile(Class<?> cls) {
		//Weird Sponge hack. Not sure why this is necessary
		try {
			if (Platform.isDev()) {
				return null;
			} else if (Platform.isSponge() && !Platform.isBukkit()) {
				return ((JarURLConnection) cls.getProtectionDomain().getCodeSource().getLocation().toURI().toURL().openConnection()).getJarFile();
			} else {
				return new JarFile(new File(cls.getProtectionDomain().getCodeSource().getLocation().toURI()));
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

}

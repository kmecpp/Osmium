package com.kmecpp.osmium;

import java.io.IOException;
import java.util.Properties;

import com.kmecpp.jlib.utils.IOUtil;
import com.kmecpp.jlib.utils.StringUtil;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public final class OsmiumData {

	private static final Properties properties = new Properties();

	private OsmiumData() {
	}

	static {
		updateProperties();
	}

	public static Class<? extends OsmiumPlugin> getMainClass() {
		try {
			return Class.forName(properties.getProperty("main")).asSubclass(OsmiumPlugin.class);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not find or load main Osmium plugin class!", e);
		}
	}

	public static String getPluginPackage() {
		return null; //TODO
	}

	public static OsmiumPlugin constructPlugin() {
		try {
			return getMainClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Failed to instantiate Osmium plugin!", e);
		}
	}

	private static void updateProperties() {
		properties.clear();

		String data = readData();
		for (String line : StringUtil.getLines(data)) {
			String[] parts = line.split(":");
			if (parts.length == 2) {
				properties.setProperty(parts[0].trim(), parts[1].trim());
			} else {
				OsmiumLogger.error("Could not to parse osmium data yaml!");
			}
		}
	}

	private static String readData() {
		try {
			return IOUtil.readString(Osmium.class.getResource("/osmium.yml"));
		} catch (IOException e) {
			throw new RuntimeException("Could not read osmium data yml!", e);
		}
	}

}

//package com.kmecpp.osmium;
//
//import java.io.IOException;
//import java.util.Properties;
//
//import com.kmecpp.jlib.utils.IOUtil;
//import com.kmecpp.jlib.utils.StringUtil;
//import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
//
//public final class OsmiumProperties {
//
//	//	private final Properties properties = new Properties();
//	private final Class<? extends OsmiumPlugin> main;
//
//	public OsmiumProperties(Object plugin) {
//		try {
//			for (String line : IOUtil.readLines(Osmium.class.getResource("/osmium.properties"))) {
//				//			System.out.println("LINE: " + line);
//				String[] parts = line.split(":");
//				if (parts.length == 2) {
//					properties.setProperty(parts[0].trim(), parts[1].trim());
//				} else {
//					OsmiumLogger.error("Could not to parse osmium data yaml!");
//				}
//			}
//			try {
//				return Class.forName(properties.getProperty("main")).asSubclass(OsmiumPlugin.class);
//			} catch (ClassNotFoundException e) {
//				throw new RuntimeException("Could not find or load main Osmium plugin class!", e);
//			}
//		} catch (IOException e) {
//			throw new RuntimeException("Could not read osmium data yml!", e);
//		}
//
//	}
//
//	public static OsmiumPlugin constructPlugin() {
//		try {
//			return getMainClass().newInstance();
//		} catch (InstantiationException | IllegalAccessException e) {
//			throw new RuntimeException("Failed to instantiate Osmium plugin!", e);
//		}
//	}
//
//}

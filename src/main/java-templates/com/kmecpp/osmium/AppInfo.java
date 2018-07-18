package com.kmecpp.osmium;

public class AppInfo {

	public static final String ID = "${project.artifactId}";
	public static final String NAME = "${project.name}";
	public static final String VERSION = "${project.version}";

	//	private static final Properties properties = new Properties();
	//
	//	static {
	//		try (InputStream stream = AppInfo.class.getResourceAsStream("app.properties")) {
	//			properties.load(stream);
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//		ID = getProperty("id");
	//		NAME = getProperty("name");
	//		VERSION = getProperty("version");
	//	}
	//
	//	private static String getProperty(String key) {
	//		String value = properties.getProperty(key);
	//		return value != null ? value : "Unknown";
	//	}

}

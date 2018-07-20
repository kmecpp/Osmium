package com.kmecpp.osmium;

public class AppInfo {

	public static final String ID = "${project_id}";
	public static final String NAME = "${project_name}";
	public static final String VERSION = "${project_version}";
	public static final String AUTHOR = "${project_author}";
	public static final String DESCRIPTION = "${project_description}";
	public static final String URL = "${project_url}";

	public static final String SPONGE_VERSION = "${sponge_version}";

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

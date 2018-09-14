package com.kmecpp.osmium;

public class AppInfo {

	public static final String ID = "${project_id}";
	public static final String NAME = "${project_name}";
	public static final String VERSION = "${project_version}";
	public static final String AUTHOR = "${project_author}";
	public static final String DESCRIPTION = "${project_description}";
	public static final String URL = "${project_url}";

	public static final String SPONGE_VERSION = "${sponge_version}";

	//	public static final String ID = get("id");
	//	public static final String NAME = get("name");
	//	public static final String VERSION = get("version");
	//	public static final String AUTHOR = get("author");
	//	public static final String DESCRIPTION = get("description");
	//	public static final String URL = get("url");
	//
	//	public static final String SPONGE_VERSION = get("sponge_version");

	//	private static Properties properties;
	//
	//	public static String get(String id) {
	//		if (properties == null) {
	//			properties = new Properties();
	//
	//			try {
	//				properties.load(AppInfo.class.getResourceAsStream("/appinfo.properties"));
	//			} catch (IOException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//
	//		return properties.getProperty(id, "UNKNOWN");
	//	}

}

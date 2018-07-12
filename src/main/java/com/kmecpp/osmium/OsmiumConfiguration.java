package com.kmecpp.osmium;

import com.kmecpp.osmium.api.config.Configuration;
import com.kmecpp.osmium.api.config.Setting;

@Configuration(path = "osmium.conf")
public class OsmiumConfiguration {

	@Setting
	public static boolean debug = false;

	/*
	 * DATABASE
	 */
	@Setting(path = "database")
	public static boolean enableMySQL = false;

	@Setting(path = "database")
	public static String mysqlHost = "";

	@Setting(path = "database")
	public static int mysqlPort = 3306;

	@Setting(path = "database")
	public static String mysqlDatabase = "";

	@Setting(path = "database")
	public static String mysqlUsername = "";

	@Setting(path = "database")
	public static String mysqlPassword = "";

}

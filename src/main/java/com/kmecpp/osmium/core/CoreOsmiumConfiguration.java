package com.kmecpp.osmium.core;

import com.kmecpp.osmium.api.config.Configuration;
import com.kmecpp.osmium.api.config.Setting;

@Configuration(path = "osmium.conf", header = "Osmium configuration file, author: kmecpp, website: https://github.com/kmecpp/Osmium")
public class CoreOsmiumConfiguration {

	@Setting
	public static boolean debug = false;

	@Setting
	public static boolean coloredConsole = true;

	/*
	 * DATABASE
	 */
	@Setting(parent = "database")
	public static boolean enableMysql = false;

	@Setting(parent = "database")
	public static String mysqlHost = "";

	@Setting(parent = "database")
	public static int mysqlPort = 3306;

	@Setting(parent = "database")
	public static String mysqlDatabase = "";

	@Setting(parent = "database")
	public static String mysqlUsername = "";

	@Setting(parent = "database")
	public static String mysqlPassword = "";

}

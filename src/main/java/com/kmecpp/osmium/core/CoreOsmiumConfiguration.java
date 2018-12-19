package com.kmecpp.osmium.core;

import java.util.UUID;

import com.kmecpp.osmium.api.config.ConfigProperties;
import com.kmecpp.osmium.api.config.Setting;

@ConfigProperties(path = "osmium.conf", header = "Osmium configuration file, author: kmecpp, website: https://github.com/kmecpp/Osmium")
public class CoreOsmiumConfiguration {

	/*
	 * ROOT
	 */
	@Setting(comment = "Set to true to enable debug level logging")
	public static boolean debug = false;

	@Setting(comment = "Choose the config file format for Osmium plugins. This is not yet implemented"
			+ "\nValid formats: FHRC, YAML, HOCON")
	public static String configFormat = "FHRC";

	@Setting(comment = "Whether or not to display color in console messages")
	public static boolean coloredConsole = true;

	/*
	 * DATABASE
	 */
	public static class Database {

		@Setting(comment = "Set to true to store plugin data in a MySQL database instead of SQLite")
		public static boolean enableMysql = false;

		@Setting(comment = "The address to access the database")
		public static String host = "";

		@Setting(comment = "The port of the MySQL database. Usually this is 3306.")
		public static int port = 3306;

		@Setting(comment = "The name of the MySQL database")
		public static String database = "";

		@Setting(comment = "The username to access the MySQL database")
		public static String username = "";

		@Setting(comment = "The password to access the MySQL database")
		public static String password = "";

	}

	public static class Metrics {

		@Setting(comment = "Set to true to enable metrics for all Osmium plugins")
		public static boolean enabled = false;

		@Setting(comment = "Set this to false to suppress all related messages if metrics are disabled")
		public static boolean pleaseEnableMessage = true;

		@Setting(comment = "Whether or not to log errors related to submitting plugin metrics")
		public static boolean logErrors = false;

		@Setting(comment = "Unique server ID. Do not change this value")
		public static UUID serverId = UUID.randomUUID();

	}

}

package com.kmecpp.osmium.core;

import java.time.ZoneId;
import java.util.UUID;

import com.kmecpp.osmium.api.config.ConfigClass;
import com.kmecpp.osmium.api.config.Setting;

@ConfigClass(path = "osmium.conf", header = "Osmium configuration file, author: kmecpp, website: https://github.com/kmecpp/Osmium")
public class OsmiumCoreConfig {

	//	static {
	//		Osmium.getConfigManager().registerType(ZoneId.class, ZoneId::getId, ZoneId::of);
	//	}

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

	@Setting(comment = "Server time zone to use")
	public static String timeZone = ZoneId.systemDefault().getId();

	//	public static int[] intlist = new int[] { 1, 2, 3, 4, 5, 6 };
	//
	//	@Setting(type = Integer.class)
	//	public static HashMap<String, Integer> map = new HashMap<>();
	//
	//	@Setting(type = Test.class)
	//	public static ArrayList<Test> test = new ArrayList<>();
	//
	//	static {
	//		map.put("MAPKEY1", 1);
	//		map.put("MAPKEY2", 2);
	//		test.add(new Test(2, 3.1415926535));
	//		test.add(new Test(2, 2.71818182));
	//	}
	//
	//	@ConfigType
	//	public static class Test {
	//
	//		public int a;
	//		public double b;
	//		public UUID uuid;
	//
	//		public Test(int a, double b) {
	//			this.a = a;
	//			this.b = b;
	//			this.uuid = UUID.randomUUID();
	//		}
	//
	//	}

	/*
	 * DATABASE
	 */
	public static class Database {

		@Setting(name = "use-mysql", comment = "Set to true to store plugin data in a MySQL database instead of SQLite")
		public static boolean useMySql = false;

		@Setting(comment = "The address to access the database")
		public static String host = "localhost";

		@Setting(comment = "The port of the MySQL database. Usually this is 3306.")
		public static int port = 3306;

		@Setting(comment = "The name of the MySQL database")
		public static String database = "";

		@Setting(comment = "The username to access the MySQL database")
		public static String username = "";

		@Setting(comment = "The password to access the MySQL database")
		public static String password = "";

		public static class Stats {

			@Setting
			public static boolean logStats = false;

		}

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

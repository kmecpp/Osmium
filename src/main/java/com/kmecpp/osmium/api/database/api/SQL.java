package com.kmecpp.osmium.api.database.api;

public class SQL {

	public static final String NULL = "NULL";
	public static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
	public static final String CURRENT_TIMESTAMP_ON_UPDATE = "CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP";

	public static enum SQLDefaultValue {

		CURRENT_TIMESTAMP("CURRENT_TIMESTAMP"),
		CURRENT_TIMESTAMP_ON_UPDATE("CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"),

		;

		private String value;

		private SQLDefaultValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

	}

	//	public static final SQLPhrase CURRENT_TIMESTAMP = new SQLPhrase("CURRENT_TIMESTAMP");
	//	public static final SQLPhrase CURRENT_TIMESTAMP_ON_UPDATE = new SQLPhrase("CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
	//
	//	private final String phrase;
	//
	//	public SQLPhrase(String phrase) {
	//		this.phrase = phrase;
	//	}
	//
	//	public String getPhrase() {
	//		return phrase;
	//	}

}

package com.kmecpp.osmium.api.database;

public class SQLPhrase {

	public static final SQLPhrase DEFAULT_CURRENT_TIMESTAMP = new SQLPhrase("DEFAULT CURRENT_TIMESTAMP");
	public static final SQLPhrase DEFAULT_CURRENT_TIMESTAMP_ON_UPDATE = new SQLPhrase("DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

	private final String phrase;

	public SQLPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getPhrase() {
		return phrase;
	}

}

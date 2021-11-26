package com.kmecpp.osmium.api.database.api;

public class SQLPhrase {

	public static final SQLPhrase CURRENT_TIMESTAMP = new SQLPhrase("CURRENT_TIMESTAMP");
	public static final SQLPhrase CURRENT_TIMESTAMP_ON_UPDATE = new SQLPhrase("CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");

	private final String phrase;

	public SQLPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getPhrase() {
		return phrase;
	}

}

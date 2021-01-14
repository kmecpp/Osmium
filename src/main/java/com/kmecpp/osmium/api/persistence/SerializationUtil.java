package com.kmecpp.osmium.api.persistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SerializationUtil {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static Date parseDate(String dateString) {
		try {
			return DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static java.sql.Date parseSQLDate(String dateString) {
		return new java.sql.Date(parseDate(dateString).toInstant().toEpochMilli());
	}

}

package com.kmecpp.osmium.api.util.lib;

public class Time {

	public static long years(int years) {
		return years * 365 * 24 * 60 * 60 * 1000;
	}

	public static long months(int months) {
		return months * 7 * 24 * 60 * 60 * 1000;
	}

	public static long weeks(int weeks) {
		return weeks * 7 * 24 * 60 * 60 * 1000;
	}

	public static long days(int days) {
		return days * 24 * 60 * 60 * 1000;
	}

	public static long hours(int hours) {
		return hours * 60 * 60 * 1000;
	}

	public static long minutes(int minutes) {
		return minutes * 60 * 1000;
	}

	public static long seconds(int seconds) {
		return seconds * 60 * 1000;
	}

}

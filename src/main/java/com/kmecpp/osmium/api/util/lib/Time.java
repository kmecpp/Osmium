package com.kmecpp.osmium.api.util.lib;

public class Time {

	public static long years(int years) {
		return years * 1000 * 60 * 60 * 24 * 365;
	}

	public static long months(int months) {
		return months * 1000 * 60 * 60 * 24 * 30;
	}

	public static long weeks(int weeks) {
		return weeks * 1000 * 60 * 60 * 24 * 7;
	}

	public static long days(int days) {
		return days * 1000 * 60 * 60 * 24;
	}

	public static long hours(int hours) {
		return hours * 1000 * 60 * 60;
	}

	public static long minutes(int minutes) {
		return minutes * 1000 * 60;
	}

	public static long seconds(int seconds) {
		return seconds * 1000;
	}

	public static double toYears(long milliseconds) {
		return (double) milliseconds / 1000 / 60 / 60 / 24 / 365;
	}

	public static double toMonths(long milliseconds) {
		return (double) milliseconds / 1000 / 60 / 60 / 24 / 30;
	}

	public static double toWeeks(long milliseconds) {
		return (double) milliseconds / 1000 / 60 / 60 / 24 / 7;
	}

	public static double toDays(long milliseconds) {
		return (double) milliseconds / 1000 / 60 / 60 / 24;
	}

	public static double toHours(long milliseconds) {
		return (double) milliseconds / 1000 / 60 / 60;
	}

	public static double toMinutes(long milliseconds) {
		return (double) milliseconds / 1000 / 60;
	}

	public static double toSeconds(long milliseconds) {
		return (double) milliseconds / 1000;
	}

}

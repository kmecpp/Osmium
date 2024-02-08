package com.kmecpp.osmium.api.util.lib;

import com.kmecpp.osmium.api.util.StringUtil;

public class Time {

	private static final long SECOND = (long) 1000;
	private static final long MINUTE = (long) 1000 * 60;
	private static final long HOUR = (long) 1000 * 60 * 60;
	private static final long DAY = (long) 1000 * 60 * 60 * 24;
	private static final long WEEK = (long) 1000 * 60 * 60 * 24 * 7;
	private static final long MONTH = (long) 1000 * 60 * 60 * 24 * 30;
	private static final long YEAR = (long) 1000 * 60 * 60 * 24 * 365;

	public static long years(int years) {
		return years * (long) 1000 * 60 * 60 * 24 * 365;
	}

	public static long months(int months) {
		return months * (long) 1000 * 60 * 60 * 24 * 30;
	}

	public static long weeks(int weeks) {
		return weeks * (long) 1000 * 60 * 60 * 24 * 7;
	}

	public static long days(int days) {
		return days * (long) 1000 * 60 * 60 * 24;
	}

	public static long hours(int hours) {
		return hours * (long) 1000 * 60 * 60;
	}

	public static long minutes(int minutes) {
		return minutes * (long) 1000 * 60;
	}

	public static long seconds(int seconds) {
		return seconds * (long) 1000;
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

	public static String format(long milliseconds) {
		if (milliseconds > YEAR) {
			return StringUtil.plural(Math.round(milliseconds / YEAR), "year");
		} else if (milliseconds > MONTH) {
			return StringUtil.plural(Math.round(milliseconds / MONTH), "month");
		} else if (milliseconds > WEEK) {
			return StringUtil.plural(Math.round(milliseconds / WEEK), "week");
		} else if (milliseconds > DAY) {
			return StringUtil.plural(Math.round(milliseconds / DAY), "day");
		} else if (milliseconds > HOUR) {
			return StringUtil.plural(Math.round(milliseconds / HOUR), "hour");
		} else if (milliseconds > MINUTE) {
			return StringUtil.plural(Math.round(milliseconds / MINUTE), "minute");
		} else if (milliseconds > SECOND) {
			return StringUtil.plural(Math.round(milliseconds / SECOND), "second");
		} else {
			return StringUtil.plural(milliseconds, "millisecond");
		}
	}

}

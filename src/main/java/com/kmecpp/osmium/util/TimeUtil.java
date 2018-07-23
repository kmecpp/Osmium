package com.kmecpp.osmium.util;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {

	private static TimeZone timeZone;

	public static TimeZone getTimeZone() {
		return timeZone;
	}

	public static void setTimeZone(String id) {
		timeZone = TimeZone.getTimeZone(id);
	}

	public static Calendar getCalendar(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar;
	}

	public static Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(timeZone);
		return calendar;
	}

	public static int getDay() {
		return getCalendar().get(Calendar.DAY_OF_MONTH);
	}

	public static int getMonth() {
		return getCalendar().get(Calendar.MONTH) + 1;
	}

	public static int getYear() {
		return getCalendar().get(Calendar.YEAR);
	}

	public static long millis(long start) {
		return System.currentTimeMillis() - start;
	}

	public static double seconds(long start) {
		return (System.currentTimeMillis() - start) / 1000D;
	}

	public static double minutes(long start) {
		return (System.currentTimeMillis() - start) / 1000D / 60D;
	}

	public static double hours(long start) {
		return (System.currentTimeMillis() - start) / 1000D / 60D / 60D;
	}

	public static double days(long start) {
		return (System.currentTimeMillis() - start) / 1000D / 60D / 60D / 24D;
	}

	public static boolean isSameDay(long t1, long t2) {
		Calendar c1 = getCalendar(t1);
		Calendar c2 = getCalendar(t2);
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
	}

}

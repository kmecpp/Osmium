package com.kmecpp.osmium.api.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {

	private static TimeZone timeZone = TimeZone.getDefault();
	private static ZoneId zoneId = timeZone.toZoneId();

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");

	public static TimeZone getTimeZone() {
		return timeZone;
	}

	public static ZoneId getZoneId() {
		return zoneId;
	}

	public static void setTimeZone(String id) {
		timeZone = TimeZone.getTimeZone(id);
		zoneId = timeZone.toZoneId();
	}

	public static String getDate() {
		return getDate(0);
	}

	public static String getDate(int offset) {
		ZonedDateTime purgeDate = ZonedDateTime.now(zoneId).plusDays(offset);
		return purgeDate.format(DATE_FORMATTER);
	}

	public static ZonedDateTime now() {
		return ZonedDateTime.now(zoneId);
	}

	public static long getStartOfDay(int dayOffset) {
		return LocalDate.now().plusDays(dayOffset).atStartOfDay(zoneId).toInstant().toEpochMilli();
	}

	public static String formatTotalMillis(long time) {
		return formatTime(1, time, "", "");
	}

	public static String formatTotalSeconds(long time) {
		return formatTime(1000, time, "", "");
	}

	public static String formatTotalSeconds(long time, String amountPrefix, String unitPrefix) {
		return formatTime(1000, time, amountPrefix, unitPrefix);
	}

	public static String formatEpoch(long epoch) {
		return formatEpoch(epoch, DATE_TIME_FORMATTER);
	}

	public static String formatEpoch(long epoch, DateTimeFormatter formatter) {
		Instant instant = Instant.ofEpochMilli(epoch);
		return instant.atZone(timeZone.toZoneId()).format(formatter);
	}

	private static String formatTime(int modifier, long time, String amountPrefix, String unitPrefix) {
		time *= modifier;

		final long day = 86400 * 1000;
		if (time > day) {
			return StringUtil.plural(time / day, "day", amountPrefix, unitPrefix);
		}

		final long hour = 3600 * 1000;
		if (time > hour) {
			return StringUtil.plural(time / hour, "hour", amountPrefix, unitPrefix);
		}

		final long minute = 60 * 1000;
		if (time > minute) {
			return StringUtil.plural(time / minute, "minute", amountPrefix, unitPrefix);
		}

		final long second = 1000;
		if (time >= second) {
			return StringUtil.plural(time / second, "second", amountPrefix, unitPrefix);
		}

		return StringUtil.plural(time, "millisecond", amountPrefix, unitPrefix);
	}

	public static String formatTotalMillis(long time, int decimals) {
		return formatTime(1, time, "", "", decimals);
	}

	private static String formatTime(int modifier, long time, String amountPrefix, String unitPrefix, int decimals) {
		time *= modifier;

		final long day = 86400 * 1000;
		if (time > day) {
			return amountPrefix + MathUtil.format((double) time / day, decimals) + unitPrefix + " days";
		}

		final long hour = 3600 * 1000;
		if (time > hour) {
			return amountPrefix + MathUtil.format((double) time / hour, decimals) + unitPrefix + " hours";
		}

		final long minute = 60 * 1000;
		if (time > minute) {
			return amountPrefix + MathUtil.format((double) time / minute, decimals) + " minutes";
		}

		final long second = 1000;
		if (time >= second) {
			return amountPrefix + MathUtil.format((double) time / second, decimals) + " seconds";
		}

		return amountPrefix + MathUtil.round((double) time / second, decimals) + " milliseconds";
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

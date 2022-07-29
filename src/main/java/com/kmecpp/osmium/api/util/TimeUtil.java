package com.kmecpp.osmium.api.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {

	private static TimeZone timeZone = TimeZone.getDefault();
	private static ZoneId zoneId = timeZone.toZoneId();

	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
	public static final DateTimeFormatter DATE_TIME_PADDED_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");

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

	public static long getStartOfDay(long epochMilli) {
		return Instant.ofEpochMilli(epochMilli).atZone(zoneId).toLocalDate().atStartOfDay().atZone(zoneId).toInstant().toEpochMilli();
	}

	public static long getStartOfDayFromDayOffset(int dayOffset) {
		return LocalDate.now().plusDays(dayOffset).atStartOfDay(zoneId).toInstant().toEpochMilli();
	}

	public static String formatTotalSeconds(long time, int decimals) {
		return formatTotalMillis(time * 1000, decimals);
	}

	public static String formatTotalMillis(long time, int decimals) {
		final double year = (long) 365 * 86400 * 1000;
		if (Math.abs(time) > year) {
			return MathUtil.format(time / year, decimals) + " years";
		}

		final double day = 86400 * 1000;
		if (Math.abs(time) > day) {
			return MathUtil.format(time / day, decimals) + " days";
		}

		final double hour = 3600 * 1000;
		if (Math.abs(time) > hour) {
			return MathUtil.format(time / hour, decimals) + " hours";
		}

		final double minute = 60 * 1000;
		if (Math.abs(time) > minute) {
			return MathUtil.format(time / minute, decimals) + " minutes";
		}

		final double second = 1000;
		if (Math.abs(time) >= second) {
			return MathUtil.format(time / second, decimals) + " seconds";
		}

		return StringUtil.plural(time, "millisecond");
	}

	public static String formatTotalMillisPrecise(long time) {
		final long year = (long) 365 * 86400 * 1000;
		final long day = 86400 * 1000;
		final long hour = 3600 * 1000;
		final long minute = 60 * 1000;
		final long second = 1000;

		if (Math.abs(time) > year) {
			long years = time / year;
			long days = (time - years * year) / day;
			return StringUtil.plural(years, "year") + " and " + StringUtil.plural(days, "day");
		}

		else if (Math.abs(time) > day) {
			long days = time / day;
			long hours = (time - days * day) / hour;
			return StringUtil.plural(days, "day") + " and " + StringUtil.plural(hours, "hour");
		}

		else if (Math.abs(time) > hour) {
			long hours = time / hour;
			long minutes = (time - hours * hour) / minute;
			return StringUtil.plural(hours, "hour") + " and " + StringUtil.plural(minutes, "minute");
		}

		else if (Math.abs(time) > minute) {
			long minutes = time / minute;
			long seconds = (time - minutes * minute) / second;
			return StringUtil.plural(minutes, "minute") + " and " + StringUtil.plural(seconds, "second");
		}

		else if (Math.abs(time) >= second) {
			return StringUtil.plural(time / second, "second");
		}

		return StringUtil.plural(time, "millisecond");
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

	public static String formatEpochPadded(long epoch) {
		return formatEpoch(epoch, DATE_TIME_PADDED_FORMATTER);
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

	//	public static String formatTotalMillis(long time, int decimals) {
	//		return formatTime(1, time, "", "", decimals);
	//	}
	//
	//	private static String formatTime(int modifier, long time, String amountPrefix, String unitPrefix, int decimals) {
	//		time *= modifier;
	//
	//		final long day = 86400 * 1000;
	//		if (time > day) {
	//			return amountPrefix + MathUtil.format((double) time / day, decimals) + unitPrefix + " days";
	//		}
	//
	//		final long hour = 3600 * 1000;
	//		if (time > hour) {
	//			return amountPrefix + MathUtil.format((double) time / hour, decimals) + unitPrefix + " hours";
	//		}
	//
	//		final long minute = 60 * 1000;
	//		if (time > minute) {
	//			return amountPrefix + MathUtil.format((double) time / minute, decimals) + " minutes";
	//		}
	//
	//		final long second = 1000;
	//		if (time >= second) {
	//			return amountPrefix + MathUtil.format((double) time / second, decimals) + " seconds";
	//		}
	//
	//		return amountPrefix + MathUtil.round((double) time / second, decimals) + " milliseconds";
	//	}

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

	public static double secondsSince(long start) {
		return (System.currentTimeMillis() - start) / 1000D;
	}

	public static double minutesSince(long start) {
		return (System.currentTimeMillis() - start) / 1000D / 60D;
	}

	public static double hoursSince(long start) {
		return (System.currentTimeMillis() - start) / 1000D / 60D / 60D;
	}

	public static double daysSince(long start) {
		return (System.currentTimeMillis() - start) / 1000D / 60D / 60D / 24D;
	}

	//	public static boolean isSameDay2(long t1, long t2) {
	//		Calendar c1 = getCalendar(t1);
	//		Calendar c2 = getCalendar(t2);
	//		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
	//	}

	public static boolean isSameDay(long t1, long t2) {
		return Instant.ofEpochMilli(t1).atZone(zoneId).toLocalDate().isEqual(Instant.ofEpochMilli(t2).atZone(zoneId).toLocalDate());
	}

	public static LocalDateTime getLocalDateTime(long time) {
		return Instant.ofEpochMilli(time).atZone(zoneId).toLocalDateTime();
	}

}

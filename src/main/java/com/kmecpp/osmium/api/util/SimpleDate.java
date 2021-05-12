package com.kmecpp.osmium.api.util;

import java.util.Calendar;
import java.util.Date;

public class SimpleDate {

	private int year;
	private int month;
	private int day;

	public static SimpleDate current() {
		return new SimpleDate();
	}

	public SimpleDate() {
		this(TimeUtil.getYear(), TimeUtil.getMonth(), TimeUtil.getDay());
	}

	public SimpleDate(long timeMillis) {
		this(getCalendarFromMillis(timeMillis));
	}

	public SimpleDate(Date date) {
		this(getCalendarFromDate(date));
	}

	public SimpleDate(Calendar calendar) {
		this.year = calendar.get(Calendar.YEAR);
		this.month = calendar.get(Calendar.MONTH) + 1;
		this.day = calendar.get(Calendar.DAY_OF_MONTH);
	}

	public SimpleDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public static SimpleDate fromString(String str) {
		String[] parts = str.split("-");
		return new SimpleDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public boolean isToday() {
		return current().equals(this);
	}

	public String toDateString() {
		return month + "/" + day + "/" + year;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimpleDate) {
			SimpleDate day = (SimpleDate) obj;
			return day.year == this.year && day.month == this.month && day.day == this.day;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + year;
		result = 31 * result + month;
		result = 31 * result + day;
		return result;
	}

	@Override
	public String toString() {
		return year + "-" + month + "-" + day;
	}

	private static Calendar getCalendarFromMillis(long timeMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		return calendar;
	}

	private static Calendar getCalendarFromDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

}

package com.kmecpp.osmium;

import com.kmecpp.osmium.api.database.CustomSerialization;
import com.kmecpp.osmium.api.util.TimeUtil;

public class SimpleDate implements CustomSerialization {

	private int year;
	private int month;
	private int day;

	public static SimpleDate current() {
		return new SimpleDate();
	}

	public SimpleDate() {
		this(TimeUtil.getYear(), TimeUtil.getMonth(), TimeUtil.getDay());
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimpleDate) {
			SimpleDate day = (SimpleDate) obj;
			return day.year == this.year && day.month == this.month && day.day == this.day;
		}
		return false;
	}

	@Override
	public String toString() {
		return year + "-" + month + "-" + day;
	}

	@Override
	public String serialize() {
		return toString();
	}

}

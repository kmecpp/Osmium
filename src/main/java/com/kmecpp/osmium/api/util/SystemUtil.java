package com.kmecpp.osmium.api.util;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

public class SystemUtil {

	public static final long KB_2 = 1 << 10;
	public static final long MB_2 = 1 << 20;
	public static final long GB_2 = 1 << 30;

	public static final long KB_10 = 1_000;
	public static final long MB_10 = 1_000_000;
	public static final long GB_10 = 1_000_000_000;

	private static TimeZone timeZone = TimeZone.getDefault();

	protected SystemUtil() {
	}

	/**
	 * Sets the time zone this class will use to return time
	 *
	 * @param timeZone
	 *            the new timezone
	 */
	public static void setTimeZone(TimeZone timeZone) {
		SystemUtil.timeZone = timeZone;
	}

	/**
	 * Gets the system operating system information
	 *
	 * @return the operating system information
	 */
	public static String getOS() {
		return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ", " + System.getProperty("os.version") + ")";
	}

	/**
	 * Gets the current system date time as a string. The format is as specified
	 * by getDate() and getTime(), combined and separated by a space
	 *
	 * @return the current system time date
	 */
	public static String getDateTime() {
		return getDate() + " " + getTime();
	}

	/**
	 * Gets the current system date as a string with the following format:
	 * month/day/year
	 *
	 * @return the current system time
	 */
	public static String getDate() {
		Calendar calendar = Calendar.getInstance(timeZone);
		return (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
	}

	/**
	 * Gets the current system time in the hour:minute:second format
	 *
	 * @return the current system time
	 */
	public static String getTime() {
		Calendar calendar = Calendar.getInstance(timeZone);
		return calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
	}

	/**
	 * Gets the number of processors available to the JVM
	 *
	 * @return the number of available processors
	 */
	public static int getAvailableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * Gets the total free memory left available to the JVM
	 *
	 * @return the total free memory
	 */
	public static long getFreeMemory() {
		return (Runtime.getRuntime().maxMemory() - getUsedMemory()) / MB_2;
	}

	/**
	 * Gets the current used memory in megabytes
	 *
	 * @return the current used memory
	 */
	public static long getUsedMemory() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB_2;
	}

	/**
	 * Gets the maximum amount of memory that the JVM will attempt to use in
	 * megabytes
	 *
	 * @return the maximum JVM memory
	 */
	public static long getTotalMemory() {
		return Runtime.getRuntime().maxMemory() / MB_2;
	}

	/**
	 * Gets the current Java version
	 *
	 * @return the Java version
	 */
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}

	/**
	 * Gets a list of the available filesystem roots
	 *
	 * @return the filesystem roots
	 */
	public static File[] getDiskRoots() {
		return File.listRoots();
	}

	/**
	 * Gets the primary filesystem root, which is the one currently being used
	 * by the program
	 *
	 * @return the primary filesystem root
	 */
	public static File getDiskRoot() {
		return new File(File.separator);
		//		File file = new File(".");
		//		while (file.getParent() != null) {
		//			file = file.getParentFile();
		//		}
		//		return file;
	}

	/**
	 * Gets the amount of free disk space on the primary filesystem root in
	 * gigabytes
	 *
	 * @return the amount of free disk space
	 */
	public static long getFreeDiskSpace() {
		return getDiskRoot().getFreeSpace() / GB_2;
	}

	/**
	 * Gets the amount of used disk space on the primary filesystem root in
	 * gigabytes
	 *
	 * @return the amount of used disk space
	 */
	public static long getUsedDiskSpace() {
		return getTotalDiskSpace() - getFreeDiskSpace();
	}

	/**
	 * Gets the total disk space on the primary filesystem root in gigabytes
	 *
	 * @return the total disk space
	 */
	public static long getTotalDiskSpace() {
		return getDiskRoot().getTotalSpace() / GB_2;
	}

}

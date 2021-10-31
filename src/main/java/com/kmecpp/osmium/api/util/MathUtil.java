package com.kmecpp.osmium.api.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {

	private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

	public static float averageTotal(float currentAverage, int totalSampleCount, float sample) {
		return currentAverage + (sample - currentAverage) / (float) totalSampleCount;
	}

	public static double averageTotal(double currentAverage, int totalSampleCount, double sample) {
		return currentAverage + (sample - currentAverage) / (double) totalSampleCount;
	}

	public static float average(float currentAverage, int currentSampleCount, float sample) {
		return average(currentAverage, currentSampleCount, sample, 1);
	}

	public static double average(double currentAverage, int currentSampleCount, double sample) {
		return average(currentAverage, currentSampleCount, sample, 1);
	}

	public static float average(float currentAverage, int currentSampleCount, float sample, int newSampleCount) {
		return (currentSampleCount * currentAverage + newSampleCount * sample) / (float) (currentSampleCount + newSampleCount);
	}

	public static double average(double currentAverage, int currentSampleCount, double sample, int newSampleCount) {
		return (currentSampleCount * currentAverage + newSampleCount * sample) / (double) (currentSampleCount + newSampleCount);
	}

	public static int cap(long n) {
		return n > Integer.MAX_VALUE ? Integer.MAX_VALUE : n < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) n;
	}

	public static int randInt() {
		return RAND.nextInt();
	}

	public static int randInt(int bound) {
		return RAND.nextInt(bound);
	}

	public static int randInt(int least, int bound) {
		return RAND.nextInt(least, bound);
	}

	public static boolean randBoolean() {
		return RAND.nextBoolean();
	}

	public static String round(double n, int decimalDigits) {
		DecimalFormat format = new DecimalFormat("0." + StringUtil.repeat('#', decimalDigits));
		format.setRoundingMode(RoundingMode.HALF_UP);
		return format.format(n);
	}

	public static String format(double n) {
		return format(n, 2);
	}

	public static String format(double n, int decimalDigits) {
		DecimalFormat format = new DecimalFormat("0." + StringUtil.repeat('0', decimalDigits));
		format.setRoundingMode(RoundingMode.HALF_UP);
		return format.format(n);
		//		String rounded = round(n, decimalDigits);
		//		return rounded + (rounded.contains(".")
		//				? StringUtil.repeat('0', decimalDigits - rounded.split("\\.")[1].length())
		//				: "." + StringUtil.repeat('0', decimalDigits));
	}

	public static int bound(int x, int min, int max) {
		return Math.max(min, Math.min(max, x));
	}

}

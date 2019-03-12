package com.kmecpp.osmium.api.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {

	private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

	public static int randInt(int least, int bound) {
		return RAND.nextInt(least, bound);
	}

	public static int randInt() {
		return RAND.nextInt();
	}

	public static boolean randBoolean() {
		return RAND.nextBoolean();
	}

	public static String round(double n, int decimalDigits) {
		DecimalFormat format = new DecimalFormat("#." + StringUtil.repeat('#', decimalDigits));
		format.setRoundingMode(RoundingMode.HALF_UP);
		return format.format(n);
	}

	public static String format(double n) {
		return format(n, 2);
	}

	public static String format(double n, int decimalDigits) {
		String rounded = round(n, decimalDigits);
		return rounded + (rounded.contains(".")
				? StringUtil.repeat('0', decimalDigits - rounded.split("\\.")[1].length())
				: "." + StringUtil.repeat('0', decimalDigits));
	}

}

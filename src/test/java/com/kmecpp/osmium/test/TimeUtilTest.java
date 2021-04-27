package com.kmecpp.osmium.test;

import com.kmecpp.osmium.api.util.TimeUtil;

public class TimeUtilTest {

	public static void main(String[] args) {
		long currentTime = System.currentTimeMillis();
		long testTime = currentTime / 2;

		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			TimeUtil.isSameDay(currentTime, testTime);
		}
		System.out.println("TIME TAKEN: " + (System.currentTimeMillis() - start) + "ms");

	}

}

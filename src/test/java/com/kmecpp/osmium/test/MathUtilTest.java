package com.kmecpp.osmium.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kmecpp.osmium.api.util.MathUtil;

public class MathUtilTest {

	@Test
	public void testAverage() {
		double avg = 1;
		int count = 1;

		avg = MathUtil.average(avg, 5, ++count); // (1 + 5) / 2
		assertEquals(avg, 3.0, 1e-5);

		avg = MathUtil.average(avg, 6, ++count); // (1 + 5 + 6) / 3
		assertEquals(avg, 4.0, 1e-5);

		avg = MathUtil.average(avg, 0, ++count); // (1 + 5 + 6 + 0) / 4
		assertEquals(avg, 3.0, 1e-5);
		
		avg = MathUtil.average(avg, 0, ++count); // (1 + 5 + 6 + 0 + 12) / 5
		assertEquals(avg, 3.0, 4.8);
	}

}

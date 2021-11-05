package com.kmecpp.osmium.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.kmecpp.osmium.api.util.MCUtil;
import com.kmecpp.osmium.api.util.Pair;

public class MCUtilTest {

	@Test
	public void testScales() {
		assertTrue(Math.sqrt(Long.MAX_VALUE) / 2 < Integer.MAX_VALUE);
	}

	@Test
	public void testGetRegionId() {
		for (int i = 0; i < SPIRAL.length; i++) {
			//			System.out.println(i + " " + Arrays.toString(SPIRAL[i]));
			assertEquals(i, MCUtil.getRegionId(SPIRAL[i][0], SPIRAL[i][1]));
		}
	}

	@Test
	public void testGetRegionCoords() {
		for (int i = 0; i < SPIRAL.length; i++) {
			//			System.out.println(i + " " + Arrays.toString(SPIRAL[i]));
			Pair<Integer, Integer> coords = MCUtil.getCoordsFromRegionId(i);
			assertEquals((Integer) SPIRAL[i][0], coords.getFirst());
			assertEquals((Integer) SPIRAL[i][1], coords.getSecond());
		}
	}

	@Test
	public void testGetXZRegionCoords() {
		for (int i = 0; i < SPIRAL.length; i++) {
			//			System.out.println(i + " " + Arrays.toString(SPIRAL[i]));
			assertEquals(SPIRAL[i][0], MCUtil.getXFromRegionId(i));
			assertEquals(SPIRAL[i][1], MCUtil.getZFromRegionId(i));
		}
	}

	private static final int[][] SPIRAL = {
			{ 0, 0 }, //Index 0
			{ -1, 0 },
			{ -1, -1 },
			{ 0, -1 },
			{ 1, -1 },
			{ 1, 0 },
			{ 1, 1 },
			{ 0, 1 },
			{ -1, 1 },
			{ -2, 1 },
			{ -2, 0 }, //Index 10
			{ -2, -1 },
			{ -2, -2 },
			{ -1, -2 },
			{ 0, -2 },
			{ 1, -2 },
			{ 2, -2 },
			{ 2, -1 },
			{ 2, 0 },
			{ 2, 1 },
			{ 2, 2 }, //Index 20
			{ 1, 2 },
			{ 0, 2 },
			{ -1, 2 },
			{ -2, 2 },
			{ -3, 2 },
			{ -3, 1 },
			{ -3, 0 },
			{ -3, -1 },
			{ -3, -2 },
			{ -3, -3 }, //Index 30
			{ -2, -3 },
			{ -1, -3 },
			{ 0, -3 },
			{ 1, -3 },
			{ 2, -3 },
			{ 3, -3 },
			{ 3, -2 },
			{ 3, -1 },
			{ 3, 0 },
			{ 3, 1 }, //Index 40
			{ 3, 2 },
			{ 3, 3 },
			{ 2, 3 },
			{ 1, 3 },
			{ 0, 3 },
			{ -1, 3 },
			{ -2, 3 },
			{ -3, 3 },
			{ -4, 3 },
			{ -4, 2 }, //Index 50
			{ -4, 1 },
			{ -4, 0 },
			{ -4, -1 },
			{ -4, -2 },
			{ -4, -3 },
			{ -4, -4 },
			{ -3, -4 },
			{ -2, -4 },
			{ -1, -4 },
			{ 0, -4 }, //Index 60
			{ 1, -4 },
			{ 2, -4 },
			{ 3, -4 },
			{ 4, -4 },
			{ 4, -3 },
			{ 4, -2 },
			{ 4, -1 },
			{ 4, 0 },
			{ 4, 1 },
			{ 4, 2 }, //Index 70
			{ 4, 3 },
			{ 4, 4 },
			{ 3, 4 },
			{ 2, 4 },
			{ 1, 4 },
			{ 0, 4 },
			{ -1, 4 },
			{ -2, 4 },
			{ -3, 4 },
			{ -4, 4 }, //Index 80
			{ -5, 4 },
			{ -5, 3 },
			{ -5, 2 },
			{ -5, 1 },
			{ -5, 0 },
			{ -5, -1 },
			{ -5, -2 },
			{ -5, -3 },
			{ -5, -4 },
			{ -5, -5 }, //Index 90
			{ -4, -5 },
			{ -3, -5 },
			{ -2, -5 },
			{ -1, -5 },
			{ 0, -5 },
			{ 1, -5 },
			{ 2, -5 },
			{ 3, -5 },
			{ 4, -5 },
			{ 5, -5 }, //Index 100
			{ 5, -4 },
			{ 5, -3 },
			{ 5, -2 },
			{ 5, -1 },
			{ 5, 0 },
			{ 5, 1 },
			{ 5, 2 },
			{ 5, 3 },
			{ 5, 4 },
			{ 5, 5 }, //Index 110
	};

}

package com.kmecpp.osmium.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kmecpp.osmium.AppInfo;

public class OsmiumTest {
	
	@Test
	public void testOsmium() {
		assertEquals("osmium", AppInfo.ID);
	}

}

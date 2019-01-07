package com.kmecpp.osmium.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.config.ConfigProperties;
import com.kmecpp.osmium.api.config.Setting;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.PlayerMovePositionEvent;
import com.kmecpp.osmium.api.logging.OsmiumLogger;

public class OsmiumTest {

	static {
		OsmiumLogger.setStandardOutput(true);
	}

	@Test
	public void testOsmium() {
		assertEquals("osmium", AppInfo.ID);
	}

	@ConfigProperties(path = "src/test/resources/test.config", header = "Config!")
	public static class Config {

		@Setting(comment = " comment!")
		public static boolean debug = true;

		@Setting(comment = " comment!")
		public static String str = "test\"";

	}

	//	@Test
	//	public void testSaveConfig() throws Exception {
	//		Osmium.getConfigurationManager().save(Config.class);
	//		assertTrue(true);
	//	}
	//
	//	@Test
	//	public void testLoadConfig() throws Exception {
	//		Osmium.getConfigurationManager().load(Config.class);
	//	}

	//	@Test
	//	public void testItemTypes() {
	//		for (ItemType type : ItemType.values()) {
	//			if (type != ItemType.BANNER && type != ItemType.BED) {
	//				assertNotNull("ITEM IS NULL: " + type, type.getSource());
	//			}
	//		}
	//	}
	//
	//	@Test
	//	public void testBlockTypes() {
	//		for (BlockType type : BlockType.values()) {
	//			if (type != BlockType.BED) {
	//				assertNotNull("BLOCK IS NULL: " + type, type.getSource()); //TODO test Bukkit and Sponge
	//			}
	//		}
	//	}

	@Test
	public void testEvents() throws Exception {
		Osmium.getEventManager().registerListener(PlayerMovePositionEvent.class, this, this.getClass().getMethod("onEvent", PlayerMovePositionEvent.class));
	}

	@Listener
	public void onEvent(PlayerMovePositionEvent e) {
	}

}

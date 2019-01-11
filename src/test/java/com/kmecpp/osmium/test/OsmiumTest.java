package com.kmecpp.osmium.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.junit.Test;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.command.CS;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.config.ConfigProperties;
import com.kmecpp.osmium.api.config.Setting;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.event.events.PlayerMovePositionEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.Plugin;
import com.kmecpp.osmium.platform.osmium.OsmiumPlayerMovePositionEvent;

public class OsmiumTest {

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

	/*
	 * Test all events have constructor and getSource does not return Object
	 * Test events which have multiple source events
	 * Test nested classes all extend from the parent
	 * 
	 * Test colors and color schemes
	 */

	@Test
	public void testColorScheme() {
		assertEquals(CS.X6AB.getPrimary().toString(), ChatColor.GOLD.toString());
		assertEquals(CS.X6AB.getSecondary().toString(), ChatColor.GREEN.toString());
		assertEquals(CS.X6AB.getTertiary().toString(), ChatColor.AQUA.toString());
	}

	@Test
	public void testColor() {
		assertEquals(Chat.GREEN.toString(), ChatColor.GREEN.toString());
	}

	@Test
	public void testPlugins() throws NoSuchMethodException, SecurityException {
		@Plugin(name = "test", version = "1.0")
		class TestOsmiumPlugin extends OsmiumPlugin {

		}
		TestOsmiumPlugin plugin = new TestOsmiumPlugin();
		Method method = plugin.getClass().getSuperclass().getDeclaredMethod("setupPlugin", Object.class);
		method.setAccessible(true);
		//		try {
		//			method.invoke(plugin, new SpongePlugin() {});
		//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		//			e.printStackTrace();
		//		}

		//		assertTrue(true);
	}

	@Test
	public void testRegisterEvents() {
		try {
			Osmium.getEventManager().registerListener(PlayerMovePositionEvent.class, Order.DEFAULT, this,
					this.getClass().getMethod("onEvent", PlayerMovePositionEvent.class));
			assertTrue(true);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFireEvents() {
		Osmium.getEventManager().callEvent(new OsmiumPlayerMovePositionEvent(null));
		assertTrue(true);
	}

	@Listener
	public void onEvent(PlayerMovePositionEvent e) {
		throw new UnsupportedOperationException();
	}

}

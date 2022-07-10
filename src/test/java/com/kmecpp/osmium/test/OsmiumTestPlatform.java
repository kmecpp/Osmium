package com.kmecpp.osmium.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.junit.Test;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.command.CS;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.config.ConfigClass;
import com.kmecpp.osmium.api.config.PluginConfigTypeData;
import com.kmecpp.osmium.api.config.Setting;
import com.kmecpp.osmium.api.event.EventInfo;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.event.events.PlayerInteractEvent;
import com.kmecpp.osmium.api.event.events.osmium.PlayerMovePositionEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.Plugin;
import com.kmecpp.osmium.platform.osmium.OsmiumPlayerMovePositionEvent;

import junit.framework.TestCase;

public class OsmiumTestPlatform extends TestCase {

	@Test
	public void testOsmium() {
		assertEquals("osmium", AppInfo.ID);
	}

	@ConfigClass(path = "src/test/resources/test.config", header = "Config!")
	public static class Config {

		@Setting(comment = " comment!")
		public static boolean debug = true;

		@Setting(comment = " comment!")
		public static String str = "test\"";

		@Setting
		public static HashMap<String, HashMap<UUID, Integer>> list = new HashMap<>();

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

	@Plugin(name = "test", version = "1.0")
	public static class OsmiumTestPlugin extends OsmiumPlugin {

	}

	@Test
	public void testPlugins() throws NoSuchMethodException, SecurityException {
		OsmiumTestPlugin plugin = new OsmiumTestPlugin();
		Method method = plugin.getClass().getSuperclass().getDeclaredMethod("setupPlugin", Object.class, PluginConfigTypeData.class);
		method.setAccessible(true);
		//		try {
		//			method.invoke(plugin, new SpongePlugin() {});
		//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		//			e.printStackTrace();
		//		}

		//		assertTrue(true);	
	}

	//	@SuppressWarnings("unchecked")
	//	@Test
	//	public void testLoad() throws Exception {
	//		Constructor<?> c = ClassLoader.getSystemClassLoader()
	//				.loadClass("org.bukkit.plugin.java.PluginClassLoader").getConstructors()[0];
	//		new OsmiumBukkitMain();
	//	}

	@Test
	public void testRegisterEvents() {
		try {
			Osmium.getEventManager().registerOsmiumEventListener(null, PlayerMovePositionEvent.class, Order.DEFAULT, this,
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

	@Test
	public void testEventClasses() {
		EventInfo.get(PlayerInteractEvent.class);//This will load all event classes
	}

	@Listener
	public void onEvent(PlayerMovePositionEvent e) {
		throw new UnsupportedOperationException();
	}

}

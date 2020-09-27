package com.kmecpp.osmium.core;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.OsmiumUserIds;
import com.kmecpp.osmium.api.TickTimeUnit;
import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.inventory.menu.InventoryManager;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.Plugin;
import com.kmecpp.osmium.api.util.TimeUtil;
import com.kmecpp.osmium.platform.osmium.OsmiumServerShutdownEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumServerStartedEvent;

@Plugin(name = AppInfo.NAME, version = AppInfo.VERSION, authors = { AppInfo.AUTHOR }, url = AppInfo.URL)
public class OsmiumCore extends OsmiumPlugin {

	private static OsmiumCore instance;

	public OsmiumCore() {
		if (instance == null) {
			instance = this;
		} else {
			throw new IllegalStateException("Plugin already constructed!");
		}
	}

	@Override
	public void onLoad() {
		TimeUtil.setTimeZone(OsmiumCoreConfig.timeZone);

		if (OsmiumCoreConfig.Database.useMySql) {
			this.getMySQLDatabase().initialize("osmium", OsmiumCoreConfig.Database.host, OsmiumCoreConfig.Database.port,
					OsmiumCoreConfig.Database.database, OsmiumCoreConfig.Database.username, OsmiumCoreConfig.Database.password);

			this.getMySQLDatabase().createTable(UserTable.class);
		}
	}

	@Override
	public void onInit() {
		enableMetrics();

		Osmium.getPlayerDataManager().start();

		Osmium.getTask().setTime(0, 1, TickTimeUnit.MINUTE).setExecutor((t) -> {
			OsmiumData.update();
		}).start();

		Osmium.getTask().setTime(0, 15, TickTimeUnit.MINUTE).setExecutor((t) -> {
			OsmiumUserIds.cleanup();
		}).start();

		Osmium.getTask().setAsync(true).setTime(0, 1, TickTimeUnit.HOUR).setExecutor(task -> {
			saveAllData();
		}).start();

		//This wont actually get run until the server has completely started
		Osmium.getTask().setExecutor(task -> {
			Osmium.getPlugins().forEach(OsmiumPlugin::onServerStarted);
			Osmium.getEventManager().callEvent(new OsmiumServerStartedEvent());
		}).start();

		this.getClassProcessor().onEnable(InventoryManager.class);

		//		if (Platform.isBukkit()) {
		//			this.metricsImplementation = new OsmiumMetrics(getPluginImplementation());
		//		} else if (Platform.isSponge()) {
		//			PluginContainer plugin = getPluginImplementation();
		//			this.metricsImplementation = Reflection.newInstance(SpongeMetrics.class, plugin, plugin.getLogger(), Sponge.getConfigManager().getSharedConfig(plugin).getDirectory());
		//			//			Reflection.newInstance(org.bstats.sponge.Metrics.class);
		//			//			this.metricsImplementation = org.bstats.sponge.Metrics.
		//		}
	}

	@Override
	public void onPostInit() {
		Osmium.getConfigManager().lateInit();
	}

	@Override
	public void onDisable() {
		saveAllData();

		Osmium.getEventManager().callEvent(new OsmiumServerShutdownEvent());
	}

	private static void saveAllData() {
		for (OsmiumPlugin plugin : Osmium.getPlugins()) {
			try {
				Osmium.savePluginData(plugin);
				Osmium.getPlayerDataManager().saveAllPlayers();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public static OsmiumCore getPlugin() {
		return instance;
	}

	public static SQLDatabase getDatabase() {
		return OsmiumCoreConfig.Database.useMySql ? instance.getMySQLDatabase() : instance.getSQLiteDatabase();
	}

}

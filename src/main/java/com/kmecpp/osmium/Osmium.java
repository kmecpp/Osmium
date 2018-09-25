package com.kmecpp.osmium;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;

import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.command.SimpleCommand;
import com.kmecpp.osmium.api.config.ConfigManager;
import com.kmecpp.osmium.api.database.Database;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.EventManager;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumMetrics;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.PluginLoader;
import com.kmecpp.osmium.api.tasks.CountdownTask;
import com.kmecpp.osmium.api.tasks.OsmiumTask;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;

public final class Osmium {

	private static final HashMap<Class<? extends OsmiumPlugin>, Database> databases = new HashMap<>();

	private static final PluginLoader pluginLoader = new PluginLoader();
	private static final ConfigManager configManager = new ConfigManager();
	private static final CommandManager commandManager = new CommandManager();
	private static final EventManager eventManager = new EventManager();
	private static final OsmiumMetrics metrics = new OsmiumMetrics();
	protected static boolean shuttingDown;

	/*
	 * TODO:
	 * - Configs
	 * - Commands
	 * - Schedulers
	 * 
	 * - More events
	 * 
	 * - Don't register listeners separately. Register one time for each event
	 * type and then have osmium call the individual methods
	 * 
	 */

	static {
		OsmiumLogger.info("Osmium API v" + AppInfo.VERSION + " initialized");
	}

	private Osmium() {
	}

	public static OsmiumMetrics getMetrics() {
		return metrics;
	}

	public static PluginLoader getPluginLoader() {
		return pluginLoader;
	}

	public static Collection<OsmiumPlugin> getPlugins() {
		return pluginLoader.getPlugins();
	}

	public static Optional<OsmiumPlugin> getPlugin(String name) {
		return pluginLoader.getPlugin(name);
	}

	public static OsmiumPlugin getPlugin(Class<?> cls) {
		return pluginLoader.getPlugin(cls);
	}

	public static void execute(String command) {
		commandManager.processCommand(command);
	}

	public static Database getDatabase() {
		return getDatabase(getInvokingPlugin());
	}

	public static Database getDatabase(OsmiumPlugin plugin) {
		Database database = databases.get(plugin.getClass());
		if (database == null) {
			database = new Database(plugin);
			database.start();
			databases.put(plugin.getClass(), database);
		}
		return database;
	}

	public static void shutdown() {
		if (Platform.isBukkit()) {
			Bukkit.shutdown();
		} else if (Platform.isSponge()) {
			Sponge.getServer().shutdown();
		}
	}

	public static void broadcast(String message) {
		if (Platform.isBukkit()) {
			Bukkit.broadcastMessage(Chat.style(message));
		} else if (Platform.isSponge()) {
			Sponge.getServer().getBroadcastChannel().send(SpongeAccess.getText(Chat.style(message)));
		}
	}

	public static void broadcast(Predicate<Player> filter, String message) {
		for (Player player : Osmium.getPlayers()) {
			if (filter.test(player)) {
				player.sendMessage(message);
			}
		}
	}

	public static OsmiumTask getTask() {
		return getTask(getInvokingPlugin());
	}

	public static OsmiumTask getTask(OsmiumPlugin plugin) {
		return new OsmiumTask(plugin);
	}

	public static CountdownTask countdown(int count) {
		return countdown(getInvokingPlugin(), count);
	}

	public static CountdownTask countdown(OsmiumPlugin plugin, int count) {
		return new CountdownTask(plugin, count);
	}

	public static boolean isShuttingDown() {
		return shuttingDown;
	}

	//	public static Task getScheduler() {
	//		return scheduler;
	//	}

	public static final Platform getPlatform() {
		return Platform.getPlatform();
	}

	public static ConfigManager getConfigurationManager() {
		return configManager;
	}

	public static boolean reloadConfig(Class<?> cls) {
		try {
			configManager.load(cls);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static void on(Platform platform, Runnable runnable) {
		if (Platform.getPlatform() == platform) {
			runnable.run();
		}
	}

	public static <T> T getValue(Callable<T> bukkit, Callable<T> sponge) {
		try {
			return Platform.isBukkit() ? bukkit.call() : Platform.isSponge() ? sponge.call() : null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Collection<Player> getPlayers() {
		return PlayerList.getPlayers();
	}

	public static Optional<Player> getPlayer(String name) {
		return Optional.of(PlayerList.getPlayer(name));
	}

	public static Collection<World> getWorlds() {
		return WorldList.getWorlds();
	}

	public static Optional<World> getWorld(String name) {
		return Optional.of(WorldList.getWorld(name));
	}

	public static boolean getOnlineMode() {
		if (Platform.isBukkit()) {
			return Bukkit.getOnlineMode();
		} else if (Platform.isSponge()) {
			return Sponge.getServer().getOnlineMode();
		} else {
			return false;
		}
	}

	public static EventManager getEventManager() {
		return eventManager;
	}

	public static CommandManager getCommandManager() {
		return commandManager;
	}

	public static SimpleCommand registerCommand(String name, String... aliases) {
		return registerCommand(getInvokingPlugin(), name, aliases);
	}

	public static SimpleCommand registerCommand(OsmiumPlugin plugin, String name, String... aliases) {
		return commandManager.register(plugin, new Command(name, aliases));
	}

	private static OsmiumPlugin getInvokingPlugin() {
		return Osmium.getPlugin(Reflection.getInvokingClass(1));
	}

	//	public static String getPluginId(String name) {
	//		return name.replace(' ', '-').toLowerCase();
	//	}

	//	public static Optional<Player> getPlayer(String name) {
	//		if (Platform.isBukkit()) {
	//			return Optional.ofNullable(new BukkitPlayer(Bukkit.getPlayer(name)));
	//		} else if (Platform.isSponge()) {
	//			Optional<org.spongepowered.api.entity.living.player.Player> optionalPlayer = Sponge.getServer().getPlayer(name);
	//			return optionalPlayer.isPresent() ? Optional.of(new SpongePlayer(optionalPlayer.get())) : Optional.empty();
	//		} else {
	//			return Optional.empty();
	//		}
	//	}
	//
	//	public static Optional<World> getWorld(String name) {
	//		if (Platform.isBukkit()) {
	//			return Optional.ofNullable(new BukkitWorld(Bukkit.getWorld(name)));
	//		} else if (Platform.isSponge()) {
	//			Optional<org.spongepowered.api.world.World> optionalWorld = Sponge.getServer().getWorld(name);
	//			return optionalWorld.isPresent() ? Optional.of(new SpongeWorld(optionalWorld.get())) : Optional.empty();
	//		} else {
	//			return Optional.empty();
	//		}
	//	}

}

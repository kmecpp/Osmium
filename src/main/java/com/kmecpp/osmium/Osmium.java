package com.kmecpp.osmium;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;

import com.kmecpp.jlib.utils.IOUtil;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.config.ConfigManager;
import com.kmecpp.osmium.api.database.Database;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.PluginInstance;
import com.kmecpp.osmium.api.tasks.CountdownTask;
import com.kmecpp.osmium.api.tasks.OsmiumTask;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;

public final class Osmium {

	private static final HashMap<String, OsmiumPlugin> pluginFiles = new HashMap<>();
	private static final HashMap<Class<? extends OsmiumPlugin>, OsmiumPlugin> plugins = new HashMap<>();
	private static final HashMap<Class<? extends OsmiumPlugin>, Database> databases = new HashMap<>();

	private static final ConfigManager configManager = new ConfigManager();
	private static final CommandManager commandManager = new CommandManager();
	protected static boolean shuttingDown;

	/*
	 * TODO:
	 * - Configs
	 * - Commands
	 * - Schedulers
	 * 
	 * - More events
	 * 
	 */

	private Osmium() {
	}

	public static Collection<OsmiumPlugin> getPlugins() {
		return plugins.values();
	}

	public static Optional<OsmiumPlugin> getPlugin(String name) {
		for (OsmiumPlugin plugin : plugins.values()) {
			if (plugin.getName().equalsIgnoreCase(name)) {
				return Optional.of(plugin);
			}
		}
		return Optional.empty();
	}

	public static OsmiumPlugin getPlugin(Class<?> cls) {
		return pluginFiles.get(Directory.getJarFilePath(cls));
	}

	public static Database getDatabase(OsmiumPlugin plugin) {
		Database database = databases.get(plugin.getClass());
		if (database == null) {
			database = new Database(plugin);
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

	public static OsmiumTask schedule(OsmiumPlugin plugin) {
		return new OsmiumTask(plugin);
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

	public static void reloadConfig(Class<?> cls) {
		configManager.loadConfig(cls);
	}

	public static void saveConfig(Class<?> cls) {
		configManager.saveConfig(cls);
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

	public static OsmiumPlugin loadPlugin(Object pluginImpl) {
		try {
			String[] lines = IOUtil.readLines(pluginImpl.getClass().getResource("/osmium.properties"));
			try {
				String mainClassName = lines[0].split(":")[1].trim();
				ClassLoader pluginClassLoader = pluginImpl.getClass().getClassLoader();
				Class<? extends OsmiumPlugin> main = pluginClassLoader.loadClass(mainClassName).asSubclass(OsmiumPlugin.class);

				OsmiumPlugin plugin = main.newInstance();

				for (Field field : plugin.getClass().getDeclaredFields()) {
					if (field.isAnnotationPresent(PluginInstance.class)
							&& Modifier.isStatic(field.getModifiers())
							&& OsmiumPlugin.class.isAssignableFrom(field.getType())) {
						field.setAccessible(true);
						field.set(null, plugin);
					}
				}

				String jarFilePath = Directory.getJarFilePath(plugin.getClass());

				plugins.put(main, plugin);
				pluginFiles.put(jarFilePath, plugin);

				Method method = OsmiumPlugin.class.getDeclaredMethod("setupPlugin", Object.class);
				method.setAccessible(true);
				method.invoke(plugin, pluginImpl);
				//				Reflection.invokeMethod(OsmiumPlugin.class, plugin, "setupPlugin", pluginImpl);//Setup plugin after everything else

				OsmiumLogger.info("Successfully loaded Osmium plugin: " + plugin.getName() + " v" + plugin.getVersion());
				OsmiumLogger.debug("File location: " + jarFilePath);

				return plugin;
			} catch (Exception e) {
				throw new RuntimeException("Could not load Osmium plugin: " + lines[1].split(":")[1].trim(), e);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read osmium.properties from jar: " + pluginImpl.getClass().getName(), e);
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

	public static CommandManager getCommandManager() {
		return commandManager;
	}

	public static void registerCommand(String... aliases) {

	}

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

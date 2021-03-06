package com.kmecpp.osmium;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.common.SpongeImpl;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;
import com.kmecpp.osmium.api.GameProfile;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.config.ConfigManager;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.EventManager;
import com.kmecpp.osmium.api.event.events.osmium.PluginRefreshEvent;
import com.kmecpp.osmium.api.event.events.osmium.PluginReloadEvent;
import com.kmecpp.osmium.api.inventory.BlockType;
import com.kmecpp.osmium.api.inventory.ItemManager;
import com.kmecpp.osmium.api.inventory.ItemType;
import com.kmecpp.osmium.api.plugin.OsmiumMetrics;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.PluginLoader;
import com.kmecpp.osmium.api.tasks.CountdownTask;
import com.kmecpp.osmium.api.tasks.OsmiumTask;
import com.kmecpp.osmium.api.util.FileUtil;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.api.util.WebUtil;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;
import com.kmecpp.osmium.core.PlayerDataManager;
import com.kmecpp.osmium.core.TPSTask;
import com.kmecpp.osmium.platform.bukkit.BukkitUser;
import com.kmecpp.osmium.platform.osmium.OsmiumPluginRefreshEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumPluginReloadEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumServerShutdownEvent;
import com.kmecpp.osmium.platform.sponge.SpongeUser;

import net.md_5.bungee.BungeeCord;

public final class Osmium {

	//	private static final HashMap<Class<? extends OsmiumPlugin>, Database> databases = new HashMap<>();

	private static final PluginLoader pluginLoader = new PluginLoader();
	private static final ConfigManager configManager = new ConfigManager();
	private static final CommandManager commandManager = new CommandManager();
	private static final PlayerDataManager playerDataManager = new PlayerDataManager();
	private static final EventManager eventManager = new EventManager();
	private static final ItemManager itemManager = Platform.isGame() ? new ItemManager() : null;
	private static final OsmiumMetrics metrics = new OsmiumMetrics();

	private static final ExecutorService genericThreadPool = Executors.newFixedThreadPool(3);

	protected static boolean shuttingDown;

	/*
	 * TODO:
	 * - Configs
	 * - Commands
	 * - Schedulers
	 * 
	 * - More events
	 * 
	 * COMMAND REMAPPER (in Osmium config)
	 * 
	 * - Don't register listeners separately. Register one time for each event
	 * type and then have osmium call the individual methods
	 * 
	 */

	static {
		if (!Platform.isDev() && !Platform.isBungeeCord()) {
			try {
				Class.forName(BlockType.class.getName());
				Class.forName(ItemType.class.getName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		try {
			//Load these classes in case jar is replaced at runtime
			Reflection.initialize(new Class<?>[] { FileUtil.class, PlayerList.class, OsmiumServerShutdownEvent.class });
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	//	public static void main(String[] args) throws IOException {
	//		long start = System.currentTimeMillis();
	//		ConfigManager m = new ConfigManager();
	//		ConfigData data = m.getConfigData(Config.class);
	//		new ConfigParser(data, new File("test.txt")).load();
	//		new ConfigWriter(data, new File("test.txt")).write();
	//		System.out.println("TIME: " + (System.currentTimeMillis() - start));
	//		//		com.kmecpp.osmium.api.config.ConfigManager.load(Test.class, new File("test.txt"));
	//	}

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
		OsmiumPlugin plugin = pluginLoader.getPlugin(cls);
		if (plugin == null) {
			throw new IllegalStateException("Failed to find Osmium plugin for " + cls.getName() + ". Either this is not an Osmium plugin or it has not been loaded yet.");
		}
		return plugin;
	}

	public static void addExternalPackage(OsmiumPlugin plugin, String packageName) {
		JarFile jarFile = Directory.getJarFile(plugin.getClassProcessor().getMainClass());
		addExternalPackage(plugin, jarFile, packageName);
	}

	public static void addExternalPackage(OsmiumPlugin plugin, JarFile jarFile, String packageName) {
		HashSet<Class<?>> classes = Reflection.getClasses(jarFile, packageName);
		for (Class<?> cls : classes) {
			pluginLoader.assignPluginToExternalClass(cls, plugin);
			plugin.getClassProcessor().addExternalClass(cls);
		}
	}

	public static void execute(String command) {
		commandManager.processConsoleCommand(command);
	}

	public static void executeOnServerThread(String command) {
		Osmium.getTask().setExecutor(t -> {
			commandManager.processConsoleCommand(command);
		}).start();
	}

	public static ConfigManager getConfigManager() {
		return configManager;
	}

	public static EventManager getEventManager() {
		return eventManager;
	}

	public static CommandManager getCommandManager() {
		return commandManager;
	}

	public static PlayerDataManager getPlayerDataManager() {
		return playerDataManager;
	}

	public static ItemManager getItemManager() {
		return itemManager;
	}

	public static ExecutorService getGenericThreadpool() {
		return genericThreadPool;
	}

	public static double getTPS() {
		return TPSTask.getAverage(60);
	}

	//	public static Database getDatabase() {
	//		return getDatabase(getInvokingPlugin());
	//	}
	//
	//	public static Database getDatabase(OsmiumPlugin plugin) {
	//		Database database = databases.get(plugin.getClass());
	//		if (database == null) {
	//			database = new Database(plugin);
	//			database.start();
	//			databases.put(plugin.getClass(), database);
	//		}
	//		return database;
	//	}

	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> cls) {
		return (T) getInvokingPlugin().getClassProcessor().getClassInstances().get(cls);
	}

	public static void shutdown() {
		//Save plugin data first
		for (OsmiumPlugin plugin : getPlugins()) {
			savePluginData(plugin);
		}

		if (Platform.isBukkit()) {
			Bukkit.shutdown();
		} else if (Platform.isSponge()) {
			Sponge.getServer().shutdown();
		} else if (Platform.isBungeeCord()) {
			BungeeCord.getInstance().stop();
		}
	}

	public static void broadcast(String message) {
		if (Platform.isBukkit()) {
			Bukkit.broadcastMessage(Chat.style(message));
		} else if (Platform.isSponge()) {
			Sponge.getServer().getBroadcastChannel().send(SpongeAccess.getText(Chat.style(message)));
		} else if (Platform.isBungeeCord()) {
			BungeeCord.getInstance().broadcast(message);
		}
	}

	public static void broadcast(Predicate<Player> filter, String message) {
		for (Player player : Osmium.getOnlinePlayers()) {
			if (filter.test(player)) {
				player.send(message);
			}
		}
	}

	public static OsmiumTask getTask() {
		return getInvokingPlugin().getTask();
	}

	public static CountdownTask countdown(int count) {
		return getInvokingPlugin().countdown(count);
	}

	public static Command registerCommand(String name, String... aliases) {
		return getCommandManager().register(getInvokingPlugin(), name, aliases);
	}

	public static OsmiumPlugin getPlugin() {
		return getInvokingPlugin();
	}

	private static OsmiumPlugin getInvokingPlugin() {
		return Osmium.getPlugin(Reflection.getInvokingClass(2));
	}

	//	public static SimpleCommand registerCommand(OsmiumPlugin plugin, String name, String... aliases) {
	//		return commandManager.register(plugin, new Command(name, aliases));
	//	}

	public static boolean isShuttingDown() {
		return shuttingDown;
	}

	public static void setShuttingDown() {
		shuttingDown = true;
	}

	//	public static Task getScheduler() {
	//		return scheduler;
	//	}

	public static void savePluginData(OsmiumPlugin plugin) {
		plugin.saveData();
	}

	public static final Platform getPlatform() {
		return Platform.getPlatform();
	}

	public static boolean loadConfig(Class<?> cls) {
		try {
			configManager.load(cls);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static void saveConfig(Class<?> config) {
		try {
			configManager.save(config);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save configuration class: " + config.getName(), e);
		}
	}

	public static boolean reloadPlugin(OsmiumPlugin plugin) {
		return reloadPlugin(plugin, false);
	}

	public static boolean reloadPlugin(OsmiumPlugin plugin, boolean reloadDatabase) {
		for (Class<?> config : configManager.getPluginConfigs(plugin)) {
			try {
				configManager.load(config);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		PluginRefreshEvent refreshEvent = new OsmiumPluginRefreshEvent(plugin, reloadDatabase);
		plugin.onRefresh(refreshEvent);
		eventManager.callEvent(refreshEvent);

		PluginReloadEvent reloadEvent = new OsmiumPluginReloadEvent(plugin, reloadDatabase);
		plugin.onReload(reloadEvent);
		eventManager.callEvent(reloadEvent);

		if (reloadDatabase) {
			if (plugin.getSQLiteDatabase().isInitialized()) {
				plugin.getSQLiteDatabase().reload();
				if (plugin.getSQLiteDatabase().isConnected()) {
					plugin.getClassProcessor().createDatabaseTables();
				}
			}
			if (plugin.getMySQLDatabase().isInitialized()) {
				plugin.getMySQLDatabase().reload();
				if (plugin.getMySQLDatabase().isConnected()) {
					plugin.getClassProcessor().createDatabaseTables();
				}
			}
		}
		return true;
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

	public static Optional<Player> getPlayer(UUID uuid) {
		if (Platform.isBukkit()) {
			Object bukkitPlayer = Bukkit.getPlayer(uuid);
			return bukkitPlayer != null ? Optional.of(PlayerList.getPlayer(bukkitPlayer)) : Optional.empty();
		} else {
			Optional<?> spongePlayer = Sponge.getServer().getPlayer(uuid);
			return spongePlayer.isPresent() ? Optional.of(PlayerList.getPlayer(spongePlayer.get())) : Optional.empty();
		}
	}

	public static boolean isPlayerOnline(UUID uuid) {
		if (Platform.isBukkit()) {
			return Bukkit.getPlayer(uuid) != null;
		} else {
			return Sponge.getServer().getPlayer(uuid).isPresent();
		}
	}

	public static Collection<Player> getOnlinePlayers() {
		return PlayerList.getPlayers();
	}

	public static Optional<Player> getPlayer(String name) {
		return Optional.ofNullable(PlayerList.getPlayer(name));
	}

	public static Optional<UUID> getUUID(String playerName) {
		Optional<User> user = getUser(playerName);
		return user.isPresent() ? Optional.of(user.get().getUniqueId()) : Optional.empty();
	}

	public static Optional<UUID> lookupUUID(String playerName) {
		try {
			return Optional.of(WebUtil.getPlayerUUID(playerName));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public static Optional<String> lookupName(UUID uuid) {
		try {
			return Optional.of(WebUtil.getPlayerName(uuid));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public static Optional<User> getOrCreateUser(UUID uuid) {
		if (Platform.isBukkit()) {
			OfflinePlayer user = Bukkit.getOfflinePlayer(uuid);
			return Optional.of(new BukkitUser(user));
		} else if (Platform.isSponge()) {
			try {
				org.spongepowered.api.profile.GameProfile gameProfile = Sponge.getServer().getGameProfileManager().get(uuid).get();
				return Optional.of(new SpongeUser(Sponge.getServiceManager().provide(UserStorageService.class).get().getOrCreate(gameProfile)));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return Optional.empty();
	}

	public static Optional<Integer> getUserId(UUID uuid) {
		return OsmiumUserIds.getUserId(uuid);
	}

	public static Optional<GameProfile> getGameProfile(int userId) {
		return Optional.ofNullable(OsmiumUserIds.getProfile(userId, false));
	}

	public static Optional<GameProfile> getGameProfile(int userId, boolean lookup) {
		return Optional.ofNullable(OsmiumUserIds.getProfile(userId, lookup));
	}

	public static Optional<User> getUser(UUID uuid) {
		if (Platform.isBukkit()) {
			OfflinePlayer user = Bukkit.getOfflinePlayer(uuid);
			if (user.hasPlayedBefore()) {
				return Optional.of(new BukkitUser(user));
			}
		} else if (Platform.isSponge()) {
			Optional<org.spongepowered.api.entity.living.player.User> user = Sponge.getServiceManager().provide(UserStorageService.class).get().get(uuid);
			if (user.isPresent()) {
				return Optional.of(new SpongeUser(user.get()));
			}
		}
		return Optional.empty();
	}

	public static Optional<User> getUser(String name) {
		Player onlinePlayer = PlayerList.getPlayer(name);
		if (onlinePlayer != null) {
			return Optional.of(onlinePlayer);
		}
		if (Platform.isBukkit()) {
			@SuppressWarnings("deprecation")
			OfflinePlayer user = Bukkit.getOfflinePlayer(name);
			if (user.hasPlayedBefore()) {
				return Optional.of(new BukkitUser(user));
			}
		} else if (Platform.isSponge()) {
			Optional<org.spongepowered.api.entity.living.player.User> user = Sponge.getServiceManager().provide(UserStorageService.class).get().get(name);
			if (user.isPresent()) {
				return Optional.of(new SpongeUser(user.get()));
			}
		}
		return Optional.empty();
	}

	public static Collection<User> getOperators() {
		ArrayList<User> users = new ArrayList<>();
		try {
			for (JsonValue value : Json.parse(new FileReader("ops.json")).asArray()) {
				try {
					users.add(getUser(value.asObject().get("uuid").asString()).get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}

	public static Collection<World> getWorlds() {
		return WorldList.getWorlds();
	}

	public static Optional<World> getWorld(String name) {
		return Optional.ofNullable(WorldList.getWorld(name));
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

	public static String mcVersion;

	public static String getMinecraftVersion() {
		if (mcVersion == null) {
			try {
				if (Platform.isBukkit()) {
					//				mcVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
					mcVersion = Bukkit.getVersion().substring(Bukkit.getVersion().indexOf(':') + 2, Bukkit.getVersion().length() - 1);
				} else if (Platform.isSponge()) {
					mcVersion = SpongeImpl.MINECRAFT_VERSION.getName();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			if (mcVersion == null) {
				mcVersion = "UNKNOWN";
			}
		}
		return mcVersion;
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

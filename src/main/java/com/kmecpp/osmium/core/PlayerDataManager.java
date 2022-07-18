package com.kmecpp.osmium.core;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.OnlinePlayerData;
import com.kmecpp.osmium.api.TickTimeUnit;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.api.database.MultiplePlayerData;
import com.kmecpp.osmium.api.database.PlayerData;
import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.database.TableData;
import com.kmecpp.osmium.api.database.api.DBTable;
import com.kmecpp.osmium.api.database.api.DatabaseType;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;

public class PlayerDataManager {

	private final HashMap<OsmiumPlugin, HashSet<Class<?>>> registeredTypes = new HashMap<>();
	private final HashMap<UUID, HashMap<Class<?>, Object>> data = new HashMap<>();
	//	private final HashMap<UUID, HashMap<Class<? extends PlayerData>, PlayerData>> playerData = new HashMap<>();
	private final HashMap<UUID, HashMap<Class<? extends MultiplePlayerData<?>>, HashMap<?, MultiplePlayerData<?>>>> multipleData = new HashMap<>();

	private final HashMap<Class<?>, Function<User, ?>> loaders = new HashMap<>();

	private final HashMap<Field, OnlinePlayerData> onlinePlayerDataFields = new HashMap<>();
	private final HashMap<UUID, Long> logoutTimes = new HashMap<>();

	void start() {
		//Safely remove offline players
		Osmium.getTask().setInterval(30, TickTimeUnit.SECOND).setExecutor(t -> {
			cleanRegisteredPlayerData();
		}).start();
	}

	private void cleanRegisteredPlayerData() {
		System.out.println("Osmium Online Players: " + Osmium.getOnlinePlayers().size() + " == " + Osmium.getOnlinePlayers());
		long start = System.nanoTime();

		long currentTime = System.currentTimeMillis();

		synchronized (logoutTimes) {
			Iterator<Entry<UUID, Long>> offlinePlayerIterator = logoutTimes.entrySet().iterator();
			while (offlinePlayerIterator.hasNext()) {
				Entry<UUID, Long> entry = offlinePlayerIterator.next();
				UUID playerId = entry.getKey();
				long logoutTime = entry.getValue();
				long timeSinceLogout = currentTime - logoutTime;
				boolean removePlayerMetadata = true;

				for (Entry<Field, OnlinePlayerData> fieldEntry : onlinePlayerDataFields.entrySet()) {
					try {
						Object dataObject = fieldEntry.getKey().get(null);
						OnlinePlayerData annotation = fieldEntry.getValue();
						if (dataObject == null) {
							continue;
						}

						int retainThreshold = annotation.retainMinutes() * 1000 * 60;
						boolean shouldRemove = timeSinceLogout > retainThreshold;

						if (shouldRemove) {
							if (Map.class.isAssignableFrom(dataObject.getClass())) { //TODO: Do we maybe want to support Map<GameProfile, ?> too?
								Reflection.<Map<UUID, ?>> cast(dataObject).remove(playerId);
							} else if (Set.class.isAssignableFrom(dataObject.getClass())) {
								Reflection.<Set<UUID>> cast(dataObject).remove(playerId);
							}
						} else {
							removePlayerMetadata = false;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (removePlayerMetadata) {
					offlinePlayerIterator.remove();
				}
			}
			long end = System.nanoTime();
			System.out.println("Cleaned " + onlinePlayerDataFields.size() + " Player Maps (" + ((end - start) / 1000F) + "us)");
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(Player player, Class<T> cls) {
		return (T) data.get(player.getUniqueId()).get(cls);
	}

	public <T> void setData(Player player, Class<T> cls, T value) {
		data.get(player.getUniqueId()).put(cls, value);
	}

	public <T> void registerDataLoader(Class<T> cls, Function<User, T> loader) {
		loaders.put(cls, loader);
	}

	//	@SuppressWarnings("unchecked")
	//	public <T extends PlayerData> T getPlayerData(Player player, Class<T> dataType) {
	//		//		System.out.println("GETTING PLAYER FOR: " + player.getName() + " :: " + data.get(player.getUniqueId()));
	//		return (T) playerData.get(player.getUniqueId()).get(dataType);
	//	}

	public <K, V extends MultiplePlayerData<K>> HashMap<K, V> getAll(Player player, Class<V> dataType) {
		//		System.out.println("GETTING PLAYER FOR: " + player.getName() + " :: " + data.get(player.getUniqueId()));
		//		ArrayList<E> result = new ArrayList<>();
		return Reflection.cast(multipleData.get(player.getUniqueId()).get(dataType));
	}

	//TODO: Phase this out in favor of an abstraction that utilizes the osmium user id
	public void registerPlayerDataType(OsmiumPlugin plugin, Class<?> dataType) {
		HashSet<Class<?>> types = registeredTypes.get(plugin);
		if (types == null) {
			types = new HashSet<>();
			registeredTypes.put(plugin, types);
		}
		types.add(dataType);
	}

	@SafeVarargs
	public final void registerPlayerDataTypes(OsmiumPlugin plugin, Class<?>... types) {
		for (Class<?> cls : types) {
			registerPlayerDataType(plugin, cls);
		}
	}

	public <T> Set<Entry<UUID, T>> get(Class<T> type) {
		return get(type, true);
	}

	@SuppressWarnings("unchecked")
	public <T> Set<Entry<UUID, T>> get(Class<T> type, boolean onlyOnline) {
		HashMap<UUID, T> map = new HashMap<>();
		for (Entry<UUID, HashMap<Class<?>, Object>> entry : data.entrySet()) {
			if (!onlyOnline || (onlyOnline && Osmium.isPlayerOnline(entry.getKey()))) {
				map.put(entry.getKey(), (T) entry.getValue().get(type));
			}
		}
		return map.entrySet();
	}

	public HashSet<Class<?>> getRegisteredTypes(OsmiumPlugin plugin) {
		return registeredTypes.getOrDefault(plugin, new HashSet<>());
	}

	//	public <T> void forEach(Class<T> type, Consumer<T> consumer) {
	//						for()
	//	}

	//This is not in core so it can't listen to events
	public <K> void onPlayerStartAuthentication(User user) {
		//		System.out.println("AUTHENCIATED!");
		long start = System.currentTimeMillis();
		synchronized (logoutTimes) {
			logoutTimes.remove(user.getUniqueId());
		}

		loaders.entrySet().forEach(entry -> {
			try {
				Class<?> dataClass = entry.getKey();
				Object data = entry.getValue().apply(user);
				this.data.computeIfAbsent(user.getUniqueId(), k -> new HashMap<>()).put(dataClass, data);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		});

		for (Entry<OsmiumPlugin, HashSet<Class<?>>> entry : registeredTypes.entrySet()) {
			OsmiumPlugin plugin = entry.getKey();

			for (Class<?> rawType : entry.getValue()) {
				DBTable tableAnnotation = rawType.getDeclaredAnnotation(DBTable.class);
				if (tableAnnotation.type().length != 1) {
					OsmiumLogger.warn("Player data table '" + rawType.getName() + "' must have only a single type! Not: " + Arrays.toString(tableAnnotation.type()));
					continue;
				}
				final DatabaseType dbType = tableAnnotation.type()[0];
				final SQLDatabase database = dbType == DatabaseType.MYSQL ? plugin.getMySQLDatabase() : plugin.getSQLiteDatabase();
				final TableData table = database.getTable(rawType);

				try {
					if (MultiplePlayerData.class.isAssignableFrom(rawType)) {
						Class<? extends MultiplePlayerData<K>> type = Reflection.cast(rawType);
						HashMap<K, MultiplePlayerData<K>> map = new HashMap<>();
						plugin.getMySQLDatabase().queryColumns(type, "uuid", user.getUniqueId()).forEach(obj -> { //TODO: MultiplePlayerData support for SQLite
							obj.updatePlayerData(user.getUniqueId(), user.getName());
							map.put(obj.getKey(), obj);
						});
						//						list.stream().forEach(d -> d.updatePlayerData(e.getUniqueId(), e.getPlayerName()));

						HashMap<Class<? extends MultiplePlayerData<?>>, HashMap<K, MultiplePlayerData<K>>> data =
								Reflection.cast(this.multipleData.computeIfAbsent(user.getUniqueId(), k -> new HashMap<>()));
						data.put(type, map);
					} else {
						Class<? extends PlayerData> type = Reflection.cast(rawType);
						PlayerData value = table.isMySQL()
								? plugin.getMySQLDatabase().getOrDefault(type, Reflection.cast(Reflection.createInstance(type)), user.getUniqueId())
								: plugin.getSQLiteDatabase().getOrDefault(type, Reflection.cast(Reflection.createInstance(type)), user.getUniqueId());

						if (value instanceof PlayerData) {
							((PlayerData) value).updatePlayerData(user.getUniqueId(), user.getName());
						}

						System.out.println("LOADED PLAYER DATA: " + value);
						this.data.computeIfAbsent(user.getUniqueId(), k -> new HashMap<>()).put(type, value);
						//				System.out.println("UPDATED PLAYER DATA: " + e.getPlayerName() + ", " + data);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
		System.out.println("PLAYER DATA LOAD TIME: " + (System.currentTimeMillis() - start) + "ms");
	}

	public void onPlayerQuit(PlayerConnectionEvent.Quit e) {
		logoutTimes.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
		savePlayer(e.getPlayer());
	}

	public void savePlayer(Player player) {
		System.out.println("OSMIUM SAVING PLAYER DATA FOR " + player);
		HashMap<Class<?>, Object> playerData = this.data.get(player.getUniqueId());
		if (playerData != null) {
			playerData.entrySet().forEach(e -> {
				if (e.getValue() instanceof PlayerData) {
					((PlayerData) e.getValue()).save();
				}
			});
		}

		//		HashMap<Class<? extends MultiplePlayerData<?>>, HashMap<?, ArrayList<MultiplePlayerData<?>>>> multiplePlayerData = this.multipleData.get(player.getUniqueId());
		//		if (multiplePlayerData != null) {
		//			multiplePlayerData.values().forEach(d -> d.getValue().values().stream().forEach(e -> e.save()));
		//		}

		//		for (Entry<Class<? extends PlayerData>, PlayerData> data : playerData.entrySet()) {
		//			data.getValue().save();
		//			//			Osmium.getPlugin(data.getKey()).getDatabase().replaceInto(data.getKey(), data.getValue());
		//		}
	}

	public void saveAllPlayers() {
		for (Player player : Osmium.getOnlinePlayers()) {
			savePlayer(player);
		}
	}

	public void registerOnlinePlayerDataField(Field field, OnlinePlayerData playerMap) {
		onlinePlayerDataFields.put(field, playerMap);
	}

	public void unregisterOnlinePlayerDataField(Field field) {
		onlinePlayerDataFields.remove(field);
	}

}

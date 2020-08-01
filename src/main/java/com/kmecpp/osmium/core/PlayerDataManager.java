package com.kmecpp.osmium.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.MultiplePlayerData;
import com.kmecpp.osmium.api.database.PlayerData;
import com.kmecpp.osmium.api.database.mysql.MySQLTable;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.tasks.TimeUnit;
import com.kmecpp.osmium.api.util.Reflection;

public class PlayerDataManager {

	private final HashMap<OsmiumPlugin, ArrayList<Class<?>>> registeredTypes = new HashMap<>();
	private final HashMap<UUID, HashMap<Class<? extends PlayerData>, PlayerData>> data = new HashMap<>();
	private final HashMap<UUID, HashMap<Class<? extends MultiplePlayerData<?>>, HashMap<?, MultiplePlayerData<?>>>> multipleData = new HashMap<>();

	void start() {
		//Safely remove offline players
		Osmium.getTask().setInterval(30, TimeUnit.SECOND).setExecutor(t -> {
			Set<UUID> onlineIds = new HashSet<>(Osmium.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet()));
			for (UUID uuid : onlineIds) {
				if (!onlineIds.contains(uuid)) {
					Osmium.getPlayerDataManager().data.remove(uuid);
				}
			}
		}).start();
	}

	@SuppressWarnings("unchecked")
	public <T extends PlayerData> T getData(Player player, Class<T> dataType) {
		//		System.out.println("GETTING PLAYER FOR: " + player.getName() + " :: " + data.get(player.getUniqueId()));
		return (T) data.get(player.getUniqueId()).get(dataType);
	}

	public <K, V extends MultiplePlayerData<K>> HashMap<K, V> getAll(Player player, Class<V> dataType) {
		//		System.out.println("GETTING PLAYER FOR: " + player.getName() + " :: " + data.get(player.getUniqueId()));
		//		ArrayList<E> result = new ArrayList<>();
		return Reflection.cast(multipleData.get(player.getUniqueId()).get(dataType));
	}

	public void registerType(OsmiumPlugin plugin, Class<?> dataType) {
		ArrayList<Class<?>> types = registeredTypes.get(plugin);
		if (types == null) {
			types = new ArrayList<>();
			registeredTypes.put(plugin, types);
		}
		types.add(dataType);
	}

	public final void registerTypes(OsmiumPlugin plugin, Class<?>... types) {
		for (Class<?> cls : types) {
			registerType(plugin, cls);
		}
	}

	public <T> Set<Entry<UUID, T>> get(Class<T> type) {
		return get(type, true);
	}

	@SuppressWarnings("unchecked")
	public <T> Set<Entry<UUID, T>> get(Class<T> type, boolean onlyOnline) {
		HashMap<UUID, T> map = new HashMap<>();
		for (Entry<UUID, HashMap<Class<? extends PlayerData>, PlayerData>> entry : data.entrySet()) {
			if (!onlyOnline || (onlyOnline && Osmium.isPlayerOnline(entry.getKey()))) {
				map.put(entry.getKey(), (T) entry.getValue().get(type));
			}
		}
		return map.entrySet();
	}

	public ArrayList<Class<?>> getRegisteredTypes(OsmiumPlugin plugin) {
		return registeredTypes.getOrDefault(plugin, new ArrayList<>());
	}

	public <T> void forEach(Class<T> type, Consumer<T> consumer) {
		//				for()
	}

	//This is not in core so it can't listen to events
	public <K> void onPlayerAuthenticate(PlayerConnectionEvent.Auth e) {
		//		System.out.println("AUTHENCIATED!");
		long start = System.currentTimeMillis();
		for (Entry<OsmiumPlugin, ArrayList<Class<?>>> entry : registeredTypes.entrySet()) {
			OsmiumPlugin plugin = entry.getKey();

			for (Class<?> rawType : entry.getValue()) {
				try {
					if (MultiplePlayerData.class.isAssignableFrom(rawType)) {
						Class<? extends MultiplePlayerData<K>> type = Reflection.cast(rawType);
						HashMap<K, MultiplePlayerData<K>> map = new HashMap<>();
						plugin.getMySQLDatabase().queryColumns(type, "uuid", e.getUniqueId()).forEach(obj -> {
							obj.updatePlayerData(e.getUniqueId(), e.getPlayerName());
							map.put(obj.getKey(), obj);
						});
						//						list.stream().forEach(d -> d.updatePlayerData(e.getUniqueId(), e.getPlayerName()));

						HashMap<Class<? extends MultiplePlayerData<?>>, HashMap<K, MultiplePlayerData<K>>> data =
								Reflection.cast(this.multipleData.computeIfAbsent(e.getUniqueId(), k -> new HashMap<>()));
						data.put(type, map);
					} else {
						Class<? extends PlayerData> type = Reflection.cast(rawType);
						PlayerData value;
						if (type.isAnnotationPresent(MySQLTable.class)) {
							//						if (AdvancedPlayerData.class.isAssignableFrom(type)) {
							//							value = plugin.getMySQLDatabase().getOrDefault(type, Reflection.cast(Reflection.createInstance(type)), e.getUniqueId());
							//						} else {
							System.out.println("GET DATA: " + type.getName());

							value = plugin.getMySQLDatabase().getOrDefault(type, Reflection.cast(Reflection.createInstance(type)), e.getUniqueId());
							//						}
						} else {
							value = plugin.getSQLiteDatabase().getOrDefault(type, Reflection.cast(Reflection.createInstance(type)), e.getUniqueId());
						}

						//				System.out.println("VALUE: " + value);

						if (value instanceof PlayerData) {
							((PlayerData) value).updatePlayerData(e.getUniqueId(), e.getPlayerName());
						}

						this.data.computeIfAbsent(e.getUniqueId(), k -> new HashMap<>()).put(type, value);
						//				System.out.println("UPDATED PLAYER DATA: " + e.getPlayerName() + ", " + data);
						//						data.put(type, value);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
		System.out.println("PLAYER DATA LOAD TIME: " + (System.currentTimeMillis() - start) + "ms");
	}

	public void onPlayerQuit(PlayerConnectionEvent.Quit e) {
		savePlayer(e.getPlayer());
	}

	public void savePlayer(Player player) {
		HashMap<Class<? extends PlayerData>, PlayerData> playerData = this.data.get(player.getUniqueId());
		if (playerData != null) {
			playerData.entrySet().forEach(e -> e.getValue().save());
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

}

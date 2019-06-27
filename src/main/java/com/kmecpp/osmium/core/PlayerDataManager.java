package com.kmecpp.osmium.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.PlayerData;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;

public class PlayerDataManager {

	private final HashMap<OsmiumPlugin, ArrayList<Class<? extends PlayerData>>> registeredTypes = new HashMap<>();
	private final HashMap<UUID, HashMap<Class<? extends PlayerData>, PlayerData>> data = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T getData(Player player, Class<T> dataType) {
		//		System.out.println("GETTING PLAYER FOR: " + player.getName() + " :: " + data);
		return (T) data.get(player.getUniqueId()).get(dataType);
	}

	public void registerType(OsmiumPlugin plugin, Class<? extends PlayerData> dataType) {
		ArrayList<Class<? extends PlayerData>> types = registeredTypes.get(plugin);
		if (types == null) {
			types = new ArrayList<>();
			registeredTypes.put(plugin, types);
		}
		types.add(dataType);
	}

	public HashMap<UUID, HashMap<Class<? extends PlayerData>, PlayerData>> getData() {
		return data;
	}

	@SuppressWarnings("unchecked")
	public <T> Set<Entry<UUID, T>> get(Class<T> type) {
		HashMap<UUID, T> map = new HashMap<>();
		for (Entry<UUID, HashMap<Class<? extends PlayerData>, PlayerData>> entry : data.entrySet()) {
			map.put(entry.getKey(), (T) entry.getValue().get(type));
		}
		return map.entrySet();
	}

	public ArrayList<Class<? extends PlayerData>> getRegisteredTypes(OsmiumPlugin plugin) {
		return registeredTypes.getOrDefault(plugin, new ArrayList<>());
	}

	public <T> void forEach(Class<T> type, Consumer<T> consumer) {
		//				for()
	}

	//This is not in core so it can't listen to events
	public void onPlayerAuthenticate(PlayerConnectionEvent.Auth e) {
		//		System.out.println("AUTHENCIATED!");
		for (Entry<OsmiumPlugin, ArrayList<Class<? extends PlayerData>>> entry : registeredTypes.entrySet()) {
			for (Class<? extends PlayerData> type : entry.getValue()) {
				PlayerData value = entry.getKey().getDatabase().getOrDefault(type, Reflection.cast(Reflection.createInstance(type)), e.getUniqueId());
				//				System.out.println("VALUE: " + value);

				if (value instanceof PlayerData) {
					((PlayerData) value).updatePlayerData(e.getUniqueId(), e.getPlayerName());
				}

				HashMap<Class<? extends PlayerData>, PlayerData> data = this.data.get(e.getUniqueId());
				if (data == null) {
					data = new HashMap<>();
					this.data.put(e.getUniqueId(), data);
				}
				//				System.out.println("UPDATED PLAYER DATA: " + e.getPlayerName() + ", " + data);
				data.put(type, value);
			}
		}
	}

	public void onPlayerQuit(PlayerConnectionEvent.Quit e) {
		savePlayer(e.getPlayer());
		data.remove(e.getUniqueId());
	}

	public void savePlayer(Player player) {
		HashMap<Class<? extends PlayerData>, PlayerData> playerData = this.data.get(player.getUniqueId());
		if (playerData == null) {
			return;
		}

		for (Entry<Class<? extends PlayerData>, PlayerData> data : playerData.entrySet()) {
			data.getValue().save(Osmium.getPlugin(data.getKey()).getDatabase());
			//			Osmium.getPlugin(data.getKey()).getDatabase().replaceInto(data.getKey(), data.getValue());
		}
	}

	public void saveAllPlayers() {
		for (Player player : Osmium.getOnlinePlayers()) {
			savePlayer(player);
		}
	}

}

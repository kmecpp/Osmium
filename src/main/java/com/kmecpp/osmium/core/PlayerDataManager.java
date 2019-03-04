package com.kmecpp.osmium.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Consumer;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.PlayerData;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;

public class PlayerDataManager {

	private HashMap<OsmiumPlugin, ArrayList<Class<?>>> registeredTypes = new HashMap<>();
	private HashMap<UUID, HashMap<Class<?>, Object>> playerData = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T getData(Player player, Class<T> dataType) {
		return (T) playerData.get(player.getUniqueId()).get(dataType);
	}

	public void registerType(OsmiumPlugin plugin, Class<?> dataType) {
		ArrayList<Class<?>> types = registeredTypes.get(plugin);
		if (types == null) {
			types = new ArrayList<>();
			registeredTypes.put(plugin, types);
		}
		types.add(dataType);
	}

	public HashMap<UUID, HashMap<Class<?>, Object>> getData() {
		return playerData;
	}

	public <T> void forEach(Class<T> type, Consumer<T> consumer) {
		//				for()
	}

	//This is not in core so it can't listen to events
	public void onPlayerAuthenticate(PlayerConnectionEvent.Auth e) {
		System.out.println("AUTHENTICATE");
		System.out.println("REGISTERED TYPES: " + registeredTypes);
		for (Entry<OsmiumPlugin, ArrayList<Class<?>>> entry : registeredTypes.entrySet()) {
			System.out.println(entry.getValue());
			for (Class<?> type : entry.getValue()) {
				System.out.println("TYPE: " + type);
				Object value = entry.getKey().getDatabase().getOrDefault(type, Reflection.cast(Reflection.createInstance(type)), e.getUniqueId());

				if (value instanceof PlayerData) {
					((PlayerData) value).updatePlayerData(e.getUniqueId(), e.getPlayerName());
				}

				HashMap<Class<?>, Object> data = this.playerData.get(e.getUniqueId());
				if (data == null) {
					data = new HashMap<>();
					this.playerData.put(e.getUniqueId(), data);
				}
				data.put(type, value);
			}
		}
	}

	public void onPlayerQuit(PlayerConnectionEvent.Quit e) {
		HashMap<Class<?>, Object> playerData = this.playerData.remove(e.getUniqueId());
		System.out.println("PLAYER QUIT!: " + playerData);

		if (playerData == null) {
			return;
		}

		for (Entry<Class<?>, Object> data : playerData.entrySet()) {
			Osmium.getPlugin(data.getKey()).getDatabase().replaceInto(data.getKey(), data.getValue());
		}
	}

}

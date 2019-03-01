package com.kmecpp.osmium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Consumer;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;

public class PlayerDataManager {

	private HashMap<OsmiumPlugin, ArrayList<Class<?>>> registeredTypes = new HashMap<>();
	private HashMap<UUID, HashMap<Class<?>, Object>> data = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T getData(Player player, Class<T> dataType) {
		return (T) data.get(player.getUniqueId()).get(dataType);
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
		return data;
	}

	public <T> void forEach(Class<T> type, Consumer<T> consumer) {
		//				for()
	}

	public void onPlayerAuthenticate(PlayerConnectionEvent.Auth e) {
		for (Entry<OsmiumPlugin, ArrayList<Class<?>>> entry : registeredTypes.entrySet()) {
			for (Class<?> type : entry.getValue()) {
				Object value = entry.getKey().getDatabase().getOrDefault(type, Reflection.cast(Reflection.createInstance(type)), e.getUniqueId());
				HashMap<Class<?>, Object> data = this.data.get(e.getUniqueId());
				if (data == null) {
					data = new HashMap<>();
					this.data.put(e.getUniqueId(), data);
				}
				data.put(type, value);
			}
		}
	}

	public void onPlayerQuit(PlayerConnectionEvent.Quit e) {
		for (Entry<Class<?>, Object> data : data.remove(e.getUniqueId()).entrySet()) {
			Osmium.getPlugin(data.getKey()).getDatabase().replaceInto(data.getKey(), data.getValue());
		}
	}

}

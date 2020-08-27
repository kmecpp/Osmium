package com.kmecpp.osmium.core;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.api.util.Pair;

public class OsmiumListener {

	private static final HashMap<UUID, Pair<Integer, Long>> ids = new HashMap<>();

	@Listener(order = Order.FIRST)
	public void on(PlayerConnectionEvent.Auth e) {
		if (OsmiumCoreConfig.Database.useMySql) {
			//			String update = "insert ignore into osmium_users(uuid, name) values (?, ?) on duplicate key update name=?";

			int result = OsmiumCore.getPlugin().getMySQLDatabase()
					.update("update osmium_users set name='" + e.getPlayerName() + "' where uuid='" + e.getUniqueId() + "'");

			if (result == 0) {
				OsmiumCore.getPlugin().getMySQLDatabase()
						.update("insert ignore into osmium_users(uuid, name) values ('" + e.getUniqueId() + "', '" + e.getPlayerName() + "')");
			}

			//			OsmiumCore.getPlugin().getMySQLDatabase().preparedStatement("insert ignore into osmium_users(uuid, name) values (?, ?)", ps -> {
			//				ps.setString(1, String.valueOf(e.getUniqueId()));
			//				ps.setString(2, e.getPlayerName());
			//				ps.setString(3, e.getPlayerName());
			//			});
		} else {
			int result = OsmiumCore.getPlugin().getSQLiteDatabase()
					.update("UPDATE osmium_users SET name='" + e.getPlayerName() + "' WHERE uuid='" + e.getUniqueId() + "'");
			if (result == 0) {
				OsmiumCore.getPlugin().getSQLiteDatabase()
						.update("INSERT OR IGNORE INTO osmium_users(uuid, name) values ('" + e.getUniqueId() + "', '" + e.getPlayerName() + "');");
			}
		}
		//		INSERT INTO visits (ip, hits)
		//		VALUES ('127.0.0.1', 1)
		//		ON CONFLICT(ip) DO UPDATE SET hits = hits + 1;

		System.out.println("AUTHENTICATE");

		System.out.println("DONE");

		getUserId(e.getUniqueId()).get(); //Cache ID
		Osmium.getPlayerDataManager().onPlayerAuthenticate(e, Osmium.getOrCreateUser(e.getUniqueId()).get());
	}

	@Listener(order = Order.FIRST)
	public void on(PlayerConnectionEvent.Login e) {
	}

	@Listener(order = Order.LAST)
	public void on(PlayerConnectionEvent.Quit e) {
		Osmium.getPlayerDataManager().onPlayerQuit(e);
	}

	public static Optional<Integer> getUserId(UUID uuid) {
		Pair<Integer, Long> data = ids.get(uuid);
		if (data == null) {
			String query = "select id from osmium_users where uuid='" + uuid + "'";
			Integer id = OsmiumCore.getDatabase().get(query, rs -> rs.getInt(1));
			if (id == null) {
				return Optional.empty();
			}
			data = new Pair<>(id, System.currentTimeMillis());
		}
		return Optional.of(data.getFirst());
	}

	public static void cleaupIds() {
		long currentTime = System.currentTimeMillis();
		ids.entrySet().removeIf(e -> currentTime - e.getValue().getSecond() > 1000 * 60 * 30); //30 Minutes
	}

}

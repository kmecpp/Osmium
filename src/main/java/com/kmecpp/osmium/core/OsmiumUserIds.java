package com.kmecpp.osmium.core;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.GameProfile;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.Pair;

public class OsmiumUserIds {

	private static final HashMap<UUID, Pair<Integer, Long>> ids = new HashMap<>();
	private static final HashMap<Integer, Pair<GameProfile, Long>> idToProfileMap = new HashMap<>();

	public static void onAsyncPreLogin(UUID uuid, String name) {
		if (uuid == null) {
			uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)); //Bukkit/Bungee convention
			OsmiumLogger.warn("Utilizing offline UUIDs to generate an ID for " + name);
		}

		OsmiumUserIds.createUserId(uuid, name);
		//		INSERT INTO visits (ip, hits)
		//		VALUES ('127.0.0.1', 1)
		//		ON CONFLICT(ip) DO UPDATE SET hits = hits + 1;

		int id = getUserId(uuid).get(); //Cache ID
		idToProfileMap.put(id, new Pair<>(new GameProfile(uuid, name), System.currentTimeMillis()));
		Osmium.getPlayerDataManager().onPlayerAuthenticate(Osmium.getOrCreateUser(uuid, name).get());
	}

	public static GameProfile getProfile(int userId, boolean lookup) {
		Pair<GameProfile, Long> pair = idToProfileMap.get(userId);
		if (pair != null) {
			return pair.getFirst();
		} else if (lookup) {
			UserTable table = OsmiumCore.getDatabase().get(UserTable.class, userId);
			if (table != null) {
				GameProfile profile = new GameProfile(table.getUniqueId(), table.getName());
				idToProfileMap.put(userId, new Pair<>(profile, System.currentTimeMillis()));
				return profile;
			}
		}
		return null;
	}

	public static void createUserId(UUID uuid, String playerName) {
		if (OsmiumCoreConfig.Database.useMySql) {
			int result = OsmiumCore.getPlugin().getMySQLDatabase().update("update osmium_users set name='" + playerName + "' where uuid='" + uuid + "'");
			if (result == 0) {
				OsmiumCore.getPlugin().getMySQLDatabase().update("insert ignore into osmium_users(uuid, name) values ('" + uuid + "', '" + playerName + "')");
			}
		} else {
			int result = OsmiumCore.getPlugin().getSQLiteDatabase().update("UPDATE users SET name='" + playerName + "' WHERE uuid='" + uuid + "'");
			if (result == 0) {
				OsmiumCore.getPlugin().getSQLiteDatabase().update("INSERT OR IGNORE INTO users(uuid, name) values ('" + uuid + "', '" + playerName + "');");
			}
		}
	}

	public static Optional<Integer> getUserId(UUID uuid) {
		Pair<Integer, Long> data = ids.get(uuid);
		if (data == null) {
			String query = "select id from " + (OsmiumCoreConfig.Database.useMySql ? "osmium_users" : "users") + " where uuid='" + uuid + "'";

			Integer id = OsmiumCore.getDatabase().get(query, rs -> rs.getInt(1));
			if (id == null) {
				return Optional.empty();
			}
			data = new Pair<>(id, System.currentTimeMillis());
			ids.put(uuid, data);
		}
		return Optional.of(data.getFirst());
	}

	public static int getUserIdFromPlayer(Player player) {
		if (player.getName().startsWith("[") || player.getName().contains("-")) {
			return -1;
		} else {
			Optional<Integer> optionalId = Osmium.getUserId(player.getUniqueId());
			if (!optionalId.isPresent()) {
				OsmiumLogger.warn("Could not get user ID player: " + player.getName());
			}
			return optionalId.orElse(-1);
		}
	}

	public static void cleanup() {
		long currentTime = System.currentTimeMillis();
		ids.entrySet().removeIf(e -> currentTime - e.getValue().getSecond() > 1000 * 60 * 30); //30 Minutes
		idToProfileMap.entrySet().removeIf(e -> currentTime - e.getValue().getSecond() > 1000 * 60 * 30); //30 Minutes
	}

}

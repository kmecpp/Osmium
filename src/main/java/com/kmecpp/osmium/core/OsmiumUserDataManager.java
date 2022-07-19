package com.kmecpp.osmium.core;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.GameProfile;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.Pair;

public class OsmiumUserDataManager {

	//	private static final HashMap<UUID, Pair<Integer, Long>> ids = new HashMap<>();
	private static final HashMap<UUID, Pair<UserTable, Long>> userData = new HashMap<>();
	private static final HashMap<Integer, Pair<GameProfile, Long>> idToProfileMap = new HashMap<>();

	public static void onAsyncPreLogin(PlayerConnectionEvent.Auth e) {
		UUID uuid = e.getUniqueId();
		String name = e.getPlayerName();

		if (uuid == null) {
			uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)); //Bukkit/Bungee convention
			OsmiumLogger.warn("Utilizing offline UUIDs to generate an ID for " + name);
		}

		try {
			OsmiumUserDataManager.createUserId(uuid, name); //Ensure 
		} catch (Exception ex) {
			ex.printStackTrace();
			e.setCancelled(true);
			e.setKickMessage("[Osmium] Unable to create or retrieve user ID");
			return;
		}
		//		INSERT INTO visits (ip, hits)
		//		VALUES ('127.0.0.1', 1)
		//		ON CONFLICT(ip) DO UPDATE SET hits = hits + 1;

		UserTable data = getUserData(uuid).get(); //The above should ensure that we always get valid user data
		idToProfileMap.put(data.getId(), new Pair<>(new GameProfile(uuid, name), System.currentTimeMillis()));
		Osmium.getPlayerDataManager().onPlayerStartAuthentication(Osmium.getOrCreateUser(uuid, name).get());
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
		String tableName = OsmiumCoreConfig.Database.useMySql ? "osmium_users" : "users";
		int result = OsmiumCore.getDatabase().update("update " + tableName + " set name='" + playerName + "' where uuid='" + uuid + "'");

		if (result == 0) {
			if (OsmiumCoreConfig.Database.useMySql) {
				OsmiumCore.getPlugin().getMySQLDatabase().update("insert ignore into osmium_users(uuid, name) values ('" + uuid + "', '" + playerName + "')");
			} else {
				OsmiumCore.getPlugin().getSQLiteDatabase().update("INSERT OR IGNORE INTO users(uuid, name) values ('" + uuid + "', '" + playerName + "');");
			}
		}
	}

	public static Optional<UserTable> getUserData(UUID uuid) {
		Pair<UserTable, Long> cachedData = userData.get(uuid);
		if (cachedData != null) {
			return Optional.of(cachedData.getFirst());
		}

		UserTable result = OsmiumCore.getDatabase().get(UserTable.class, "uuid", uuid.toString());
		if (result != null) {
			userData.put(uuid, new Pair<>(result, System.currentTimeMillis()));
		}
		return Optional.ofNullable(result);
	}

	public static final UserTable FAKE_USER_DATA = UserTable.createFakeUserTable();

	public static UserTable getUserDataFromPlayer(Player player) {
		return player.getName().startsWith("[") || player.getName().contains("-") ? FAKE_USER_DATA
				: getUserData(player.getUniqueId()).get();
	}

	public static void cleanup() {
		long currentTime = System.currentTimeMillis();
		userData.entrySet().removeIf(e -> currentTime - e.getValue().getSecond() > 1000 * 60 * 30); //30 Minutes
		idToProfileMap.entrySet().removeIf(e -> currentTime - e.getValue().getSecond() > 1000 * 60 * 30); //30 Minutes
	}

}

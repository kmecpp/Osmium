package com.kmecpp.osmium.platform.bukkit;

import java.time.ZoneId;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.core.OsmiumUserDataManager;

public class BukkitUser implements User {

	private OfflinePlayer user;
	private String name; //Bukkit's OfflinePlayer's name is null when the player first joins. Use our own to avoid this problem

	public BukkitUser(OfflinePlayer user, String name) {
		this.user = user;
		this.name = name;
	}

	@Override
	public OfflinePlayer getSource() {
		return user;
	}

	@Override
	public UUID getUniqueId() {
		return user.getUniqueId();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getOsmiumId() {
		return Osmium.getUserId(user.getUniqueId()).orElse(-1);
	}

	@Override
	public ZoneId getTimeZone() {
		return OsmiumUserDataManager.getUserData(user.getUniqueId()).get().getTimeZone();
	}

	@Override
	public boolean isOp() {
		return user.isOp();
	}

	@Override
	public long getLastPlayed() {
		return user.getLastPlayed();
	}

	@Override
	public long getFirstPlayed() {
		return user.getFirstPlayed();
	}

	@Override
	public boolean hasPlayedBefore() {
		return user.hasPlayedBefore();
	}

	@Override
	public boolean isOnline() {
		return user.isOnline();
	}

}

package com.kmecpp.osmium.platform.bukkit;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.User;

public class BukkitUser implements User {

	private OfflinePlayer user;

	public BukkitUser(OfflinePlayer user) {
		this.user = user;
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
		return user.getName();
	}

	@Override
	public int getOsmiumId() {
		return Osmium.getUserId(user.getUniqueId()).orElse(-1);
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

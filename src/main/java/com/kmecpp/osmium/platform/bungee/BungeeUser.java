package com.kmecpp.osmium.platform.bungee;

import java.time.ZoneId;
import java.util.UUID;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.core.OsmiumUserDataManager;

import net.md_5.bungee.BungeeCord;

public class BungeeUser implements User {

	private UUID uuid;
	private String name;

	public BungeeUser(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	@Override
	public UUID getSource() {
		return uuid;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getOsmiumId() {
		return Osmium.getUserId(uuid).orElse(-1);
	}

	@Override
	public ZoneId getTimeZone() {
		return OsmiumUserDataManager.getUserData(uuid).get().getTimeZone();
	}

	@Override
	public boolean isOp() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getLastPlayed() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getFirstPlayed() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasPlayedBefore() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOnline() {
		return BungeeCord.getInstance().getPlayer(uuid) != null;
	}

}

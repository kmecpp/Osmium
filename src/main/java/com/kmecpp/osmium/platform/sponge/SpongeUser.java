package com.kmecpp.osmium.platform.sponge;

import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.core.OsmiumUserDataManager;

public class SpongeUser implements User {

	private org.spongepowered.api.entity.living.player.User user;

	public SpongeUser(org.spongepowered.api.entity.living.player.User user) {
		this.user = user;
	}

	@Override
	public org.spongepowered.api.entity.living.player.User getSource() {
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
	public ZoneId getTimeZone() {
		return OsmiumUserDataManager.getUserData(user.getUniqueId()).get().getTimeZone();
	}

	@Override
	public boolean isOp() {
		return user.hasPermission("*");
	}

	@Override
	public long getLastPlayed() {
		Optional<JoinData> data = user.get(JoinData.class);
		if (data.isPresent()) {
			return data.get().lastPlayed().get().toEpochMilli();
		} else {
			return 0;
		}
	}

	@Override
	public long getFirstPlayed() {
		Optional<JoinData> data = user.get(JoinData.class);
		if (data.isPresent()) {
			return data.get().firstPlayed().get().toEpochMilli();
		} else {
			return 0;
		}
	}

	@Override
	public boolean hasPlayedBefore() {
		return user.isOnline() ? user.getPlayer().get().hasPlayedBefore() : true;
	}

	@Override
	public boolean isOnline() {
		return user.isOnline();
	}

}

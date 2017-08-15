package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.Player;

public class SpongePlayer implements Player {

	private org.spongepowered.api.entity.living.player.Player spongePlayer;

	public SpongePlayer(org.spongepowered.api.entity.living.player.Player player) {
		this.spongePlayer = player;
	}

	public org.spongepowered.api.entity.living.player.Player getSpongePlayer() {
		return spongePlayer;
	}

	@Override
	public void sendMessage(String message) {
		spongePlayer.sendMessage(Text.of(message));
	}

	@Override
	public boolean respawn() {
		return spongePlayer.respawnPlayer();
	}

}

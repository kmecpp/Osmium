package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.entity.Player;

public class SpongePlayer implements Player {

	private org.spongepowered.api.entity.living.player.Player player;

	public SpongePlayer(org.spongepowered.api.entity.living.player.Player player) {
		this.player = player;
	}

	@Override
	public org.spongepowered.api.entity.living.player.Player getSource() {
		return player;
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public void sendMessage(String message) {
		player.sendMessage(Text.of(message));
	}

	@Override
	public boolean respawn() {
		return player.respawnPlayer();
	}

	@Override
	public boolean isOp() {
		return player.hasPermission("*");
	}

	@Override
	public void setOp(boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasPermission(String permission) {
		// TODO Auto-generated method stub
		return false;
	}

}

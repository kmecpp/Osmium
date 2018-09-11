package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.Location;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.Inventory;

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
	public void sendRawMessage(String message) {
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
		throw new UnsupportedOperationException("Sponge players do not have operator status");
	}

	@Override
	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}

	@Override
	public Inventory getInventory() {
		return new SpongeInventory(player.getInventory());
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.fromImplementation(player.gameMode().get());
	}

	@Override
	public void setGameMode(GameMode mode) {
		player.gameMode().set(mode.getGameModeImpl());
	}

	@Override
	public double getHealth() {
		return player.health().get();
	}

	@Override
	public void setHealth(double health) {
		player.health().set(health);
	}

	@Override
	public Location getLocation() {
		return SpongeAccess.getLocation(player.getLocation());
	}

	@Override
	public void teleport(Location location) {
		player.setLocation(location.getImplementation());
	}

}

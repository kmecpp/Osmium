package com.kmecpp.osmium.platform.sponge;

import java.util.UUID;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.location.Direction;
import com.kmecpp.osmium.api.location.Location;

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
	public String getWorldName() {
		return player.getWorld().getName();
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
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
	public ItemStack getItemInMainHand() {
		return new SpongeItemStack(player.getItemInHand(HandTypes.MAIN_HAND).orElse(null));
	}

	@Override
	public ItemStack getItemInOffHand() {
		return new SpongeItemStack(player.getItemInHand(HandTypes.MAIN_HAND).orElse(null));
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.fromImplementation(player.gameMode().get());
	}

	@Override
	public void setGameMode(GameMode mode) {
		player.gameMode().set(mode.getSource());
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
	public Direction getDirection() {
		return new Direction((float) (player.getRotation().getY() + 90) % 360, (float) player.getRotation().getX() * -1);
	}

	@Override
	public void teleport(Location location) {
		player.setLocation(location.getImplementation());
	}

	@Override
	public String getDisplayName() {
		return player.getDisplayNameData().displayName().get().toString();
	}

	@Override
	public void setDisplayName(String name) {
		player.getDisplayNameData().displayName().set(SpongeAccess.getText(name));
	}

}

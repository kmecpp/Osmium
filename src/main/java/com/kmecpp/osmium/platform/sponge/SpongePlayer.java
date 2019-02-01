package com.kmecpp.osmium.platform.sponge;

import java.util.Optional;

import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.text.Text;

import com.flowpowered.math.vector.Vector3d;
import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;

public class SpongePlayer extends SpongeEntityLiving implements Player {

	private org.spongepowered.api.entity.living.player.Player player;

	public SpongePlayer(org.spongepowered.api.entity.living.player.Player player) {
		super(player);
		this.player = player;
	}

	@Override
	public org.spongepowered.api.entity.living.player.Player getSource() {
		return player;
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
		return new SpongeItemStack(player.getItemInHand(HandTypes.MAIN_HAND));
	}

	@Override
	public ItemStack getItemInOffHand() {
		return new SpongeItemStack(player.getItemInHand(HandTypes.MAIN_HAND));
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.fromSource(player.gameMode().get());
	}

	@Override
	public void setGameMode(GameMode mode) {
		player.gameMode().set(mode.getSource());
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public boolean isOp() {
		return player.hasPermission("*");
	}

	@Override
	public long getLastPlayed() {
		Optional<JoinData> data = player.get(JoinData.class);
		if (data.isPresent()) {
			return data.get().lastPlayed().get().toEpochMilli();
		} else {
			return 0;
		}
	}

	@Override
	public long getFirstPlayed() {
		Optional<JoinData> data = player.get(JoinData.class);
		if (data.isPresent()) {
			return data.get().firstPlayed().get().toEpochMilli();
		} else {
			return 0;
		}
	}

	@Override
	public boolean hasPlayedBefore() {
		return player.hasPlayedBefore();
	}

	@Override
	public boolean isOnline() {
		return player.isOnline();
	}

	@Override
	public void openInventory(Inventory inventory) {
		player.openInventory((org.spongepowered.api.item.inventory.Inventory) inventory.getSource());
	}

	@Override
	public void closeInventory() {
		player.closeInventory();
	}

	@Override
	public com.kmecpp.osmium.api.location.Vector3d getVelocity() {
		Vector3d v = player.getVelocity();
		return new com.kmecpp.osmium.api.location.Vector3d(v.getX(), v.getY(), v.getZ());
	}

	@Override
	public void setVelocity(double x, double y, double z) {
		player.setVelocity(new Vector3d(x, y, z));
	}

	@Override
	public void kick() {
		player.kick();
	}

	@Override
	public void kick(String message) {
		player.kick(Text.of(message));
	}

}

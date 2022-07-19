package com.kmecpp.osmium.platform.bukkit;

import java.time.ZoneId;

import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.SoundType;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.location.Vector3d;
import com.kmecpp.osmium.core.OsmiumUserDataManager;
import com.kmecpp.osmium.core.UserTable;

public class BukkitPlayer extends BukkitEntityLiving implements Player {

	private org.bukkit.entity.Player player;
	private UserTable osmiumData;

	public BukkitPlayer(org.bukkit.entity.Player player) {
		super(player);
		this.player = player;
		this.osmiumData = OsmiumUserDataManager.getUserDataFromPlayer(this);
	}

	@Override
	public int getOsmiumId() {
		return osmiumData.getId();
	}

	@Override
	public ZoneId getTimeZone() {
		return osmiumData.getTimeZone();
	}

	@Override
	public org.bukkit.entity.Player getSource() {
		return player;
	}

	@Override
	public String getDisplayName() {
		return player.getDisplayName();
	}

	@Override
	public boolean respawn() {
		if (player.isDead()) {
			player.spigot().respawn();
			return true;
		}
		return false;
	}

	@Override
	public void sendMessage(String message) {
		player.sendMessage(message);
	}

	@Override
	public void setOp(boolean value) {
		player.setOp(value);
	}

	@Override
	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}

	@Override
	public Inventory getInventory() {
		return new BukkitInventory(player.getInventory());
	}

	@Override
	public int getSelectedSlot() {
		return player.getInventory().getHeldItemSlot();
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItemInMainHand() {
		try {
			return new BukkitItemStack(player.getInventory().getItemInMainHand());
		} catch (NoSuchMethodError e) {
			return new BukkitItemStack(player.getInventory().getItemInHand());
		}
	}

	@Override
	public ItemStack getItemInOffHand() {
		return new BukkitItemStack(player.getInventory().getItemInMainHand());
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.fromSource(player.getGameMode());
	}

	@Override
	public void setGameMode(GameMode mode) {
		player.setGameMode(mode.getSource());
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public boolean isOp() {
		return player.isOp();
	}

	@Override
	public long getLastPlayed() {
		return player.getLastPlayed();
	}

	@Override
	public long getFirstPlayed() {
		return player.getFirstPlayed();
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
		player.openInventory((org.bukkit.inventory.Inventory) inventory.getSource());
	}

	@Override
	public void closeInventory() {
		player.closeInventory();
	}

	@Override
	public Vector3d getVelocity() {
		Vector v = player.getVelocity();
		return new Vector3d(v.getX(), v.getY(), v.getZ());
	}

	@Override
	public void setVelocity(double x, double y, double z) {
		player.setVelocity(new Vector(x, y, z));
	}

	@Override
	public void kick() {
		player.kickPlayer("You have been kicked from the server!");
	}

	@Override
	public void kick(String message) {
		player.kickPlayer(message);
	}

	@Override
	public void chat(String message) {
		player.chat(message);
	}

	@Override
	public void heal() {
		player.setHealth(20);
		player.setFoodLevel(20);
		for (PotionEffect e : player.getActivePotionEffects()) {
			player.removePotionEffect(e.getType());
		}
	}

	@Override
	public int getFoodLevel() {
		return player.getFoodLevel();
	}

	@Override
	public void setFoodLevel(int level) {
		player.setFoodLevel(level);
	}

	@Override
	public int getTotalExperience() {
		return player.getTotalExperience();
	}

	@Override
	public void setTotalExperience(int exp) {
		player.setTotalExperience(exp);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void hidePlayer(Player player) {
		this.player.hidePlayer((org.bukkit.entity.Player) player.getSource());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void showPlayer(Player player) {
		this.player.showPlayer((org.bukkit.entity.Player) player.getSource());
	}

	@Override
	public boolean getAllowFlight() {
		return player.getAllowFlight();
	}

	@Override
	public void setAllowFlight(boolean flight) {
		player.setAllowFlight(flight);
	}

	@Override
	public boolean isFlying() {
		return player.isFlying();
	}

	@Override
	public void setFlying(boolean flying) {
		player.setFlying(flying);
	}

	@Override
	public void playSound(SoundType sound, float pitch, float volume) {
		player.playSound(player.getLocation(), (Sound) sound.getSource(), volume, pitch);
	}

	@Override
	public String toString() {
		return player.toString();
	}

}

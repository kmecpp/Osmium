package com.kmecpp.osmium.api.entity;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.SoundType;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.api.location.Vector3d;

public interface Player extends User, EntityLiving, CommandSender {

	boolean respawn();

	ItemStack getItemInMainHand();

	ItemStack getItemInOffHand();

	GameMode getGameMode();

	void setGameMode(GameMode mode);

	Inventory getInventory();

	int getSelectedSlot();

	void openInventory(Inventory inventory);

	void closeInventory();

	void setVelocity(double x, double y, double z);

	Vector3d getVelocity();

	void kick();

	void kick(String message);

	void chat(String message);

	int getFoodLevel();

	void setFoodLevel(int level);

	int getTotalExperience();

	void setTotalExperience(int exp);

	void hidePlayer(Player player);

	void showPlayer(Player player);

	boolean getAllowFlight();

	void setAllowFlight(boolean flight);

	void setFlying(boolean flying);

	boolean isFlying();

	void playSound(SoundType sound, float pitch, float volume);

	default <T> T getData(Class<T> type) {
		return Osmium.getPlayerDataManager().getData(this, type);
	}

	default Location getHighestLocation() {
		Location loc = this.getLocation();
		return new Location(this.getWorld(), loc.getX(), this.getWorld().getHighestYAt(loc.getBlockX(), loc.getBlockZ()), loc.getZ());
	}

	default void sendToSpawnLocation() {
		this.setLocation(this.getWorld().getSpawnLocation().getBlockTopCenter());
	}

}

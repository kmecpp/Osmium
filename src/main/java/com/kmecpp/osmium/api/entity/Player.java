package com.kmecpp.osmium.api.entity;

import java.util.HashMap;
import java.util.function.Consumer;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.SoundType;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.database.MultiplePlayerData;
import com.kmecpp.osmium.api.database.Saveable;
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

	default <T extends Saveable> void setData(Class<T> type, T data) {
		Osmium.getPlayerDataManager().setData(this, type, data);
	}

	default <T extends Saveable> void updateData(Class<T> type, Consumer<T> updater) {
		T data = getData(type);
		updater.accept(data);
		data.save();
	}

	default <K, V extends MultiplePlayerData<K>> HashMap<K, V> getMultipleData(Class<V> type) {
		return Osmium.getPlayerDataManager().getAll(this, type);
	}

	default Location getHighestLocation() {
		Location loc = this.getLocation();
		return new Location(this.getWorld(), loc.getX(), this.getWorld().getHighestYAt(loc.getBlockX(), loc.getBlockZ()), loc.getZ());
	}

	default void sendToSpawnLocation() {
		this.setLocation(this.getWorld().getSpawnLocation().getBlockTopCenter());
	}

	//	default BukkitPlayer asBukkitPlayer() {
	//		return (BukkitPlayer) this;
	//	}
	//
	//	default SpongePlayer asSpongePlayer() {
	//		return (SpongePlayer) this;
	//	}

	@SuppressWarnings("unchecked")
	default <T extends org.bukkit.entity.Player> T asBukkitPlayer() {
		return (T) this.getSource();
	}

	@SuppressWarnings("unchecked")
	default <T extends org.spongepowered.api.entity.living.player.Player> T asSpongePlayer() {
		return (T) this.getSource();
	}

}

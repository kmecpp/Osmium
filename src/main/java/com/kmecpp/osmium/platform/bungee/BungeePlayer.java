package com.kmecpp.osmium.platform.bungee;

import java.time.ZoneId;
import java.util.UUID;

import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.SoundType;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.EntityType;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.entity.Projectile;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.location.Direction;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.api.location.Vector3d;
import com.kmecpp.osmium.core.OsmiumUserDataManager;
import com.kmecpp.osmium.core.UserTable;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePlayer implements Player {

	private ProxiedPlayer player;
	private UserTable osmiumData;

	public BungeePlayer(ProxiedPlayer player) {
		this.player = player;
		this.osmiumData = OsmiumUserDataManager.getUserDataFromPlayer(this);
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
	public int getOsmiumId() {
		return osmiumData.getId();
	}

	@Override
	public ZoneId getTimeZone() {
		return osmiumData.getTimeZone();
	}

	@Override
	public boolean isOp() {
		return player.hasPermission("*");
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
		return true;
	}

	@Override
	public ProxiedPlayer getSource() {
		return player;
	}

	@Override
	public double getHealth() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHealth(double health) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getMaxHealth() {
		throw new UnsupportedOperationException();
	}

	@Override
	public World getWorld() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getWorldName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDisplayName() {
		return player.getDisplayName();
	}

	@Override
	public void setDisplayName(String name) {
		player.setDisplayName(name);
	}

	@Override
	public Location getLocation() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setLocation(Location location) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Direction getDirection() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDirection(Direction direction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setVelocity(int x, int y, int z) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityType getType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void launch(Class<? extends Projectile> projectile) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void launch(Class<? extends Projectile> projectile, Direction direction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOp(boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}

	@Override
	public void sendMessage(String message) {
		player.sendMessage(new TextComponent(message));
	}

	@Override
	public boolean respawn() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ItemStack getItemInMainHand() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ItemStack getItemInOffHand() {
		throw new UnsupportedOperationException();
	}

	@Override
	public GameMode getGameMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setGameMode(GameMode mode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Inventory getInventory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getSelectedSlot() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void openInventory(Inventory inventory) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void closeInventory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setVelocity(double x, double y, double z) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Vector3d getVelocity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void kick() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void kick(String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void chat(String message) {
		player.chat(message);
	}

	@Override
	public int getFoodLevel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFoodLevel(int level) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getTotalExperience() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTotalExperience(int exp) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void hidePlayer(Player player) {
		player.hidePlayer(player);
	}

	@Override
	public void showPlayer(Player player) {
		player.showPlayer(player);
	}

	@Override
	public boolean getAllowFlight() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAllowFlight(boolean flight) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFlying(boolean flying) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFlying() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void playSound(SoundType sound, float pitch, float volume) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return player.toString();
	}

}

package com.kmecpp.osmium.api.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import com.kmecpp.osmium.api.config.ConfigProperties;
import com.kmecpp.osmium.api.config.ConfigType;
import com.kmecpp.osmium.api.config.Setting;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.api.location.WorldPosition;

@ConfigProperties(path = "test.conf", loadLate = true)
public class BuildingRegions {

	//	public static final int REGION_SIZE = 100;

	@Setting(type = Region.class)
	public static ArrayList<Region> regions = new ArrayList<>();

	static {
		//		Osmium.getConfigManager().registerType(Region.class, Region::fromString);
	}

	@ConfigType
	public static class Region {

		private String owner;
		private UUID ownerId;
		private HashSet<UUID> memberIds;
		private WorldPosition home;
		private int radius;
		private int x;
		private int z;

		public Region(String owner, UUID ownerId, HashSet<UUID> memberIds, WorldPosition home, int x, int z) {
			this.owner = owner;
			this.ownerId = ownerId;
			this.memberIds = memberIds;
			this.home = home;
			this.radius = 50;
			this.x = x;
			this.z = z;
		}

		public static Region create(Player player) {
			WorldPosition pos = player.getWorldPosition();
			Region region = new Region(player.getName(), player.getUniqueId(), new HashSet<>(), pos, pos.getLocation().getBlockX(), pos.getLocation().getBlockZ());
			regions.add(region);
			return region;
		}

		public String getOwner() {
			return owner;
		}

		public boolean isAllowed(Player player) {
			return player.isOp() || ownerId.equals(player.getUniqueId()) || memberIds.contains(player.getUniqueId());
		}

		public WorldPosition getHome() {
			return home;
		}

		public void setHome(WorldPosition home) {
			this.home = home;
		}

		public boolean isOwner(Player player) {
			return player.isOp() || ownerId.equals(player.getUniqueId());
		}

		public boolean contains(Location location) {
			return contains(location.getBlockX(), location.getBlockZ());
		}

		public boolean contains(int x, int z) {
			int dx = Math.abs(this.x - x);
			int dz = Math.abs(this.z - z);
			if (dx < radius && dz < radius) {
				return true;
			}
			return false;
		}

		public HashSet<UUID> getMemberIds() {
			return memberIds;
		}

		public void addMember(UUID id) {
			memberIds.add(id);
		}

		public String getPosition() {
			return x + ", " + z;
		}

		//		@Override
		//		public String toString() {
		//			return ownerId + "|" + String.join(",", memberIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
		//					+ owner + "," + x + "," + z;
		//		}
		//
		//		public static Region fromString(String str) {
		//			String[] parts = str.split("$");
		//
		//			String[] ids = parts[0].split(",");
		//			ownerId = UUID.fromString(ids[0]);
		//			for (int i = 1; i < ids.length; i++) {
		//				ids.
		//			}
		//			ArrayList<UUID> memberIds = new ArrayList<>();
		//			for (String memberId : parts[0].split("\\|")[1].split(",")) {
		//				memberIds.add(UUID.fromString(memberId));
		//			}
		//
		//			return new Region(UUID.fromString(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
		//		}
	}

	public static void unclaim(Region region) {
		regions.remove(region);
	}

	public static boolean wouldOverlap(Player player) {
		for (Region region : BuildingRegions.regions) {
			if (region.contains(player.getLocation().add(region.radius, 0, 0)) ||
					region.contains(player.getLocation().add(-region.radius, 0, 0)) ||
					region.contains(player.getLocation().add(0, 0, region.radius)) ||
					region.contains(player.getLocation().add(0, 0, -region.radius))) {
				return true;
			}
		}
		return false;
	}

	public static Region getOwnedRegion(Player player) {
		for (Region region : BuildingRegions.regions) {
			if (region.isOwner(player)) {
				return region;
			}
		}
		return null;
	}

	public static Region getRegionPlayerIn(Player player) {
		Location l = player.getLocation();
		return getRegion(l.getBlockX(), l.getBlockZ());
	}

	public static Region getRegion(int x, int z) {
		for (Region region : regions) {
			if (region.contains(x, z)) {
				return region;
			}
		}
		return null;
	}

}

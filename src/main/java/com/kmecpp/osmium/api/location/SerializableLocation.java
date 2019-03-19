package com.kmecpp.osmium.api.location;

import java.util.Optional;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.config.ConfigTypes;
import com.kmecpp.osmium.api.entity.Player;

public class SerializableLocation {

	private Location location;

	private String serialized;

	static {
		ConfigTypes.register(SerializableLocation.class, SerializableLocation::fromString);
	}

	public SerializableLocation(Player player) {
		this(player.getLocation());
	}

	public SerializableLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		if (location == null && serialized != null) {
			String[] parts = serialized.split(",");
			location = new Location(Osmium.getWorld(parts[0]).get(), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
			this.serialized = null;
		}
		return location;
	}

	public void teleport(Player player) {
		player.setLocation(getLocation());
	}

	@Override
	public String toString() {
		return location == null ? serialized : (location.getWorld().getName()
				+ "," + location.getX() + "," + location.getY() + "," + location.getZ());
	}

	public static SerializableLocation fromString(String str) {
		String[] parts = str.split(",");

		Optional<World> world = Osmium.getWorld(parts[0]);
		SerializableLocation position = new SerializableLocation((Location) null);

		if (world.isPresent()) {
			position.location = new Location(Osmium.getWorld(parts[0]).get(), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
		} else {
			position.serialized = str;
		}
		return position;

	}

}

package com.kmecpp.osmium.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;

import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.platform.bukkit.BukkitWorld;
import com.kmecpp.osmium.platform.sponge.SpongeWorld;

public class WorldList {

	private static final HashMap<String, World> worlds = new HashMap<>();

	public static void addWorld(World world) {
		World result = worlds.put(world.getName().toLowerCase(), world);
		if (result != null) {
			OsmiumLogger.warn("Implementation does not support worlds with the same name: " + world.getName() + " and " + result.getName() + ". Things will probably now explode");
		}
	}

	public static void removeWorld(String name) {
		worlds.remove(name.toLowerCase());
	}

	public static World getWorld(Object worldSource) {
		if (Platform.isBukkit()) {
			org.bukkit.World bukkitWorld = (org.bukkit.World) worldSource;
			World world = worlds.get(bukkitWorld.getName().toLowerCase());
			if (world != null) {
				return world;
			}
			return registerWorld(new BukkitWorld(bukkitWorld));
		} else if (Platform.isSponge()) {
			org.spongepowered.api.world.World spongeWorld = (org.spongepowered.api.world.World) worldSource;
			World world = worlds.get(spongeWorld.getName().toLowerCase());
			if (world != null) {
				return world;
			}
			return registerWorld(new SpongeWorld(spongeWorld));
		}
		return null;
	}

	public static World getWorld(String name) {
		World world = worlds.get(name.toLowerCase());
		if (world != null) {
			return world;
		}

		if (Platform.isBukkit()) {
			org.bukkit.World bukkitWorld = Bukkit.getWorld(name);
			if (bukkitWorld != null) {
				return registerWorld(new BukkitWorld(bukkitWorld));
			}
		} else if (Platform.isSponge()) {
			Optional<org.spongepowered.api.world.World> opt = Sponge.getServer().getWorld(name);
			return opt.isPresent() ? registerWorld(new SpongeWorld(opt.get())) : null;
		}
		return null;
	}

	private static World registerWorld(World world) {
		worlds.put(world.getName().toLowerCase(), world);
		return world;
	}

	public static Collection<World> getWorlds() {
		return worlds.values();
	}

}

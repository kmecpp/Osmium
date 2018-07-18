package com.kmecpp.osmium.cache;

import java.util.Collection;
import java.util.HashMap;

import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.logging.OsmiumLogger;

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

	public static World getWorld(String name) {
		return worlds.get(name.toLowerCase());
	}

	public static Collection<World> getWorlds() {
		return worlds.values();
	}

}

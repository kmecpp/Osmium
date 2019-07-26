package com.kmecpp.osmium.api;

import org.bukkit.World.Environment;
import org.spongepowered.api.world.DimensionTypes;

import com.kmecpp.osmium.Platform;

public enum WorldType {

	OVERWORLD,
	NETHER,
	THE_END;

	private Object source;

	static {
		if (Platform.isBukkit()) {
			OVERWORLD.source = Environment.NORMAL;
			NETHER.source = Environment.NETHER;
			THE_END.source = Environment.THE_END;
		} else if (Platform.isSponge()) {
			OVERWORLD.source = DimensionTypes.OVERWORLD;
			NETHER.source = DimensionTypes.NETHER;
			THE_END.source = DimensionTypes.THE_END;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getSource() {
		return (T) source;
	}

	public static WorldType fromImplementation(Object obj) {
		for (WorldType type : values()) {
			if (type.source == obj) {
				return type;
			}
		}
		return null;
	}

}

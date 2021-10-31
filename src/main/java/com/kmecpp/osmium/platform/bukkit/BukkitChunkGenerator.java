package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;

public class BukkitChunkGenerator extends ChunkGenerator {

	protected final static void setBlock(short[][] chunk, int x, int y, int z, short id) {
		if (chunk[y >> 4] == null) {
			chunk[y >> 4] = new short[4096];
		}
		chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = id;
	}

	@SuppressWarnings("deprecation")
	protected final void setBlock(byte[][] result, int x, int y, int z, Material material) {
		int chunk = y >> 4;
		if (result[chunk] == null) {
			result[chunk] = new byte[4096];
		}
		result[chunk][((y & 0xF) << 8) | (z << 4) | x] = (byte) material.getId();
	}

}

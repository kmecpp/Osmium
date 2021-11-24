package com.kmecpp.osmium.api.util;

public class MCUtil {

	public static final int getBlockCoord(double x) {
		return (int) Math.floor(x);
	}

	public static final int getBlockCoordFromChunkOffset(int chunkCoord, int chunkOffset) {
		return (chunkCoord << 4) + chunkOffset;
	}

	//	public static int fastFloor(double x) {
	//		int truncated = (int) x;
	//		return x < (double) truncated ? truncated - 1 : truncated;
	//	}

	public static final int getChunkRelativePosition(int n) {
		return Math.floorMod(n, 16);
	}

	public static final int getChunkCoord(int x) {
		return x >> 4;
	}

	public static final int getChunkCoord(double x) {
		return (int) Math.floor(x) >> 4;
	}

	public static final int getRegionCoord(int x) {
		return getChunkCoord(x) >> 5;
	}

	public static final int getRegionCoord(double x) {
		return getChunkCoord(x) >> 5;
	}

	public static final int getChunkOrigin(int n) {
		return (n >> 4) << 4;
	}

	public static final int getChunkOrigin(double n) {
		return ((int) Math.floor(n) >> 4) << 4;
	}

	/**
	 * Maps 2D integer coordinates to a unique non-negative integer. Coordinates
	 * are ordered based on a left-oriented Ulam spiral, which pairs very nicely
	 * with Minecraft's coordinate partitioning system.
	 * 
	 * Please credit @kmecpp
	 */
	public static final long getRegionId(int xi, int zi) {
		if (Math.abs(xi) > 32_000_000 || Math.abs(zi) > 32_000_000) { //Actual value: 33554431. Long can theoretically support inputs up to 1518500249 but the precision of the sqrt() in the inverse map limits this much further
			throw new IllegalArgumentException("Max coordinate is 32,000,000. Got: " + (Math.abs(xi) > Math.abs(zi) ? xi : zi));
		}
		long x = xi;
		long z = zi;

		return x >= z
				? 4 * (x >= -z ? x * x : z * z) + (x + z)
				: 4 * (x < -z ? x * x + x : z * z + z) - (x + z); //Credit: https://github.com/kmecpp
	}

	/**
	 * The inverse map of getRegionId(). Takes in a single non-negative integer
	 * as an input and returns the unique 2D integer coordinates that correspond
	 * to the given ID.
	 * 
	 * Please credit @kmecpp
	 */
	public static final Pair<Integer, Integer> getCoordsFromRegionId(int n) {
		//Note: Returning int[] is about 10x faster than this. Returning int in 2 different methods (done below) is about 30x faster.
		if (n < 0 || n > 4_096_000_128_000_000L) { //Max ID calculated from (-32M, 32M)
			throw new IllegalArgumentException("Region ID must be between 0 and 4,096,000,128,000,000. Got: " + n);
		}

		int r = (int) Math.sqrt(n);
		int r2 = r * r;
		int offset = n - r2;
		int radius = r / 2;

		return r % 2 == 0
				? offset <= r
						? new Pair<>(radius, offset - radius)
						: new Pair<>((radius + r) - offset, radius)
				: offset <= r
						? new Pair<>(-(radius + 1), radius - offset)
						: new Pair<>(offset - (radius + 1 + r), -(radius + 1)); //Credit: https://github.com/kmecpp
	}

	/**
	 * The inverse map of getRegionId(). Takes in a single non-negative integer
	 * as an input and returns the X coordinate of the unique 2D integer
	 * coordinates that correspond to the given ID.
	 * 
	 * Please credit @kmecpp
	 */
	public static final int getXFromRegionId(long n) {
		if (n < 0 || n > 4_096_000_128_000_000L) { //Max ID calculated from (-32M, 32M)
			throw new IllegalArgumentException("Region ID must be between 0 and 4,096,000,128,000,000. Got: " + n);
		}

		int r = (int) Math.sqrt(n);
		long r2 = r * r; //Not guaranteed to fit inside an int. Only (sqrt(Long.MAX)/2 < Int.MAX < sqrt(Long.MAX))
		int offset = (int) (n - r2);
		int radius = r / 2;

		return r % 2 == 0
				? offset <= r ? radius : (radius + r) - offset
				: offset <= r ? -(radius + 1) : offset - (radius + 1 + r); //Credit: https://github.com/kmecpp
	}

	/**
	 * The inverse map of getRegionId(). Takes in a single non-negative integer
	 * as an input and returns the Z coordinate of the unique 2D integer
	 * coordinates that correspond to the given ID.
	 * 
	 * Please credit @kmecpp
	 */
	public static final int getZFromRegionId(long n) {
		if (n < 0 || n > 4_096_000_128_000_000L) { //Max ID calculated from (-32M, 32M)
			throw new IllegalArgumentException("Region ID must be between 0 and 4,096,000,128,000,000. Got: " + n);
		}

		int r = (int) Math.sqrt(n);
		long r2 = r * r; //Not guaranteed to fit inside an int. Only (sqrt(Long.MAX)/2 < Int.MAX < sqrt(Long.MAX))
		int offset = (int) (n - r2);
		int radius = r / 2;

		return r % 2 == 0
				? offset <= r ? offset - radius : radius
				: offset <= r ? radius - offset : -(radius + 1); //Credit: https://github.com/kmecpp
	}

	public static boolean isValidUsername(String username) {
		if (username == null || username.length() < 3 || username.length() > 16) {
			return false;
		}

		for (int i = 0; i < username.length(); i++) {
			char c = username.charAt(i);
			if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '_') {
				return false;
			}
		}

		return true;
	}

	//	/**
	//	 * Maps 2D integer coordinates to a unique natural number. Coordinates are
	//	 * ordered based on a left-oriented Ulam spiral, which pairs very nicely
	//	 * with Minecraft's coordinate partitioning system.
	//	 * 
	//	 * Please credit @kmecpp
	//	 */
	//	public static final int getSmallRegionId(int x, int z) {
	//		if (Math.abs(x) > 23169 || Math.abs(z) > 23169) {
	//			throw new IllegalArgumentException("Max coordinate for an integer is 23169");
	//		}
	//
	//		return x >= z
	//				? 4 * (x >= -z ? x * x : z * z) + (x + z)
	//				: 4 * (x < -z ? x * x + x : z * z + z) - (x + z); //Credit: https://github.com/kmecpp
	//	}

}

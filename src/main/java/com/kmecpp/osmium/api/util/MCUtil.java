package com.kmecpp.osmium.api.util;

public class MCUtil {

	public static final int getBlockCoord(double x) {
		return (int) Math.floor(x);
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
		return n >> 4 << 4;
	}

	public static final int getChunkOrigin(double n) {
		return (int) Math.floor(n) >> 4 << 4;
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

}

package com.kmecpp.osmium.api;

public interface Location extends Abstraction {

	World getWorld();

	double getX();

	double getY();

	double getZ();

	//	private World world;
	//	private double x;
	//	private double y;
	//	private double z;
	//	private float pitch;
	//	private float yaw;
	//
	//	public World getWorld() {
	//		return world;
	//	}
	//
	//	public double getX() {
	//		return x;
	//	}
	//
	//	public double getY() {
	//		return y;
	//	}
	//
	//	public double getZ() {
	//		return z;
	//	}
	//
	//	public float getPitch() {
	//		return pitch;
	//	}
	//
	//	public float getYaw() {
	//		return yaw;
	//	}
	//
	//	public int getBlockX() {
	//		return (int) x;
	//	}
	//
	//	public int getBlockY() {
	//		return (int) y;
	//	}
	//
	//	public int getBlockZ() {
	//		return (int) z;
	//	}

}

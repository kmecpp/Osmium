package com.kmecpp.osmium.api.config;

import java.util.ArrayList;

public class Block {

	private String name;
	private int depth;
	private String path;
	private ArrayList<ConfigField> fields;
	private ArrayList<Block> blocks;

	public Block(String name, int depth, String path) {
		this.name = name;
		this.depth = depth;
		this.path = path;
		this.fields = new ArrayList<>();
		this.blocks = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public int getDepth() {
		return depth;
	}

	public String getPath() {
		return path;
	}

	public boolean isRoot() {
		return path.isEmpty();
	}

	public ArrayList<ConfigField> getFields() {
		return fields;
	}

	public void addField(ConfigField field) {
		fields.add(field);
	}

	public ArrayList<Block> getBlocks() {
		return blocks;
	}

	public Block createChild(String name) {
		Block child = new Block(name, depth + 1, (path.isEmpty() ? "" : path + ".") + name.toLowerCase());
		blocks.add(child);
		return child;
	}

}

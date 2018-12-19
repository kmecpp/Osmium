package com.kmecpp.osmium.api.config;

import java.io.IOException;
import java.nio.file.Path;

import com.kmecpp.osmium.api.util.FileUtil;

import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class VirtualConfig {

	private Path path;
	private HoconConfigurationLoader loader;
	private CommentedConfigurationNode root;

	public VirtualConfig(Path path, HoconConfigurationLoader loader, CommentedConfigurationNode root) {
		this.path = path;
		this.loader = loader;
		this.root = root;
	}

	public Path getPath() {
		return path;
	}

	public HoconConfigurationLoader getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getRoot() {
		return root;
	}

	public CommentedConfigurationNode getNode(String path) {
		return root.getNode((Object[]) path.split("\\."));
	}

	public void save() throws IOException {
		ValueType.class.getClass(); //Verify type exists. This is a hacky solution to prevent the data file from being erased if the Osmium jar is overwritten while the server is running
		FileUtil.createFile(path.toFile());
		loader.save(root);
	}

}

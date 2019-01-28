package com.kmecpp.osmium.api.config;

import java.io.IOException;
import java.nio.file.Path;

import com.kmecpp.osmium.api.util.FileUtil;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class VirtualConfig {

	private Path path;
	private ConfigurationLoader<?> loader;
	private ConfigurationNode root;

	public VirtualConfig(Path path, ConfigurationLoader<?> loader, ConfigurationNode root) {
		this.path = path;
		this.loader = loader;
		this.root = root;
	}

	public Path getPath() {
		return path;
	}

	public ConfigurationLoader<?> getLoader() {
		return loader;
	}

	public ConfigurationNode getRoot() {
		return root;
	}

	public ConfigurationNode getNode(String path) {
		return root.getNode((Object[]) path.split("\\."));
	}

	public void save() throws IOException {
		ValueType.class.getClass(); //Verify type exists. This is a hacky solution to prevent the data file from being erased if the Osmium jar is overwritten while the server is running
		FileUtil.createFile(path.toFile());
		loader.save(root);
	}

}

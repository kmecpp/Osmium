package com.kmecpp.osmium.api.config;

import java.io.IOException;
import java.nio.file.Path;

import com.kmecpp.osmium.api.util.FileUtil;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class DataFile {

	protected final Path path;
	protected final ConfigurationLoader<CommentedConfigurationNode> loader;
	protected final CommentedConfigurationNode root;

	public DataFile(Path path) throws IOException {
		this.path = path;
		this.loader = HoconConfigurationLoader.builder()
				.setDefaultOptions(ConfigurationOptions.defaults())
				.setPath(path)
				.build();

		this.root = this.loader.load();
	}

	public DataFile(Path path, ConfigurationLoader<CommentedConfigurationNode> loader, CommentedConfigurationNode root) {
		this.path = path;
		this.loader = loader;
		this.root = root;
	}

	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getRoot() {
		return root;
	}

	public CommentedConfigurationNode getNode(String path) {
		return root.getNode((Object[]) path.split("\\."));
	}

	public CommentedConfigurationNode getNode(String[] path) {
		return root.getNode((Object[]) path);
	}

	public void save() throws IOException {
		FileUtil.createFile(path.toFile());
		loader.save(root);
	}

}
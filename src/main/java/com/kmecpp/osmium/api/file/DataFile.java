package com.kmecpp.osmium.api.file;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.kmecpp.osmium.api.util.FileUtil;
import com.kmecpp.osmium.core.OsmiumCoreConfig;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

public abstract class DataFile {

	private Path path;
	private ConfigurationLoader<? extends ConfigurationNode> loader;
	private ConfigurationNode root;

	public DataFile() {
		FileProperties properties = this.getClass().getAnnotation(FileProperties.class);
		if (properties == null) {
			throw new IllegalArgumentException("Data files must be annotated with @" + FileProperties.class.getSimpleName());
		}

		this.path = Paths.get(properties.path());
		if (OsmiumCoreConfig.configFormat.equals("YAML")) {
			this.loader = YAMLConfigurationLoader.builder().setPath(path).build();
		} else {
			this.loader = HoconConfigurationLoader.builder().setPath(path).build();
		}
	}

	public void load() throws IOException {
		this.root = loader.load();
	}

	public Path getPath() {
		return path;
	}

	public ConfigurationLoader<? extends ConfigurationNode> getLoader() {
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

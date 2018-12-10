package com.kmecpp.osmium.api.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

public class YAMLHandler extends ConfigHandler {

	private static final String INDENT = "  ";

	private StringBuilder sb = new StringBuilder();

	private Yaml loader = new Yaml();
	private Map<String, Object> config;

	public YAMLHandler(OsmiumConfig config) {
		super(config);
	}

	public <T> void serialize(ConfigField field) {
		//		for (Field field : config.getConfigClass().getDeclaredFields()) {
		//			Setting setting = field.getAnnotation(Setting.class);
		//			if (setting != null) {
		//
		//			}
		//		}
	}

	@SuppressWarnings("unchecked")
	public void load(File file) throws IOException {
		Object obj = loader.load(new FileInputStream(file));
		if (!(obj instanceof Map)) {
			throw new IOException("Invalid YAML configuration file: " + file.getName());
		}
		config = (Map<String, Object>) obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(String[] path) {
		Map<String, Object> map = config;

		for (int i = 0; i < path.length; i++) {
			Object result = map.get(path[i]);
			if (i < path.length - 1) {
				map = (Map<String, Object>) result;
			} else {
				return result;
			}
		}
		return null;
	}

	@Override
	public void serialize(OsmiumConfig config) {
	}

	public <T> T deserialize(Class<T> type, Setting setting) {
		return null;
	}

	@Override
	public void deserailize() {
	}

}

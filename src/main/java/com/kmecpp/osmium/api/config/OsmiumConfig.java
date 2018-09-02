package com.kmecpp.osmium.api.config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.google.common.reflect.TypeToken;
import com.kmecpp.osmium.api.logging.OsmiumLogger;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class OsmiumConfig {

	private final Class<?> configClass;
	//	private final HashMap<String, Field> fields; //TODO: Test whether or not it's worth it to store field data in memory
	private final ConfigField[] fields;
	private final ConfigurationLoader<CommentedConfigurationNode> loader;
	private final CommentedConfigurationNode root;

	public OsmiumConfig(Class<?> cls, ConfigurationLoader<CommentedConfigurationNode> loader, CommentedConfigurationNode root) {
		this.configClass = cls;

		long start = System.currentTimeMillis();
		ArrayList<ConfigField> fields = findFields(new ArrayList<>(), "", cls);
		this.fields = fields.toArray(new ConfigField[fields.size()]);
		System.out.println("Field Search: " + (System.currentTimeMillis() - start) + "ms");

		this.loader = loader;
		this.root = root;
	}

	public Class<?> getConfigClass() {
		return configClass;
	}

	public ConfigField[] getFields() {
		return fields;
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

	public boolean reload() {
		boolean save = false;
		for (ConfigField field : fields) {
			CommentedConfigurationNode node = getNode(field.getPath());

			try {
				if (node.isVirtual() && !field.getSetting().deletable()) {
					setValue(node, field.getField());
					save = true;
				} else {
					field.getField().set(null, node.getValue(TypeToken.of(field.getField().getType())));
				}
			} catch (IllegalArgumentException | IllegalAccessException | ObjectMappingException e) {
				e.printStackTrace();
			}
		}
		return save;
	}

	public void save(boolean overrideComments) throws IOException {
		for (ConfigField field : fields) {
			CommentedConfigurationNode node = getNode(field.getPath());

			if (overrideComments && !field.getSetting().comment().isEmpty()) {
				node.setComment(field.getSetting().comment());
			}

			try {
				setValue(node, field.getField());
			} catch (IllegalArgumentException | IllegalAccessException | ObjectMappingException e) {
				e.printStackTrace();
			}
		}

		loader.save(root);
	}

	private ArrayList<ConfigField> findFields(ArrayList<ConfigField> fields, String path, Class<?> cls) {
		for (Field field : cls.getFields()) {
			Setting setting = field.getAnnotation(Setting.class);
			if (setting == null) {
				continue;
			}
			if (!Modifier.isStatic(field.getModifiers())) {
				OsmiumLogger.warn("Invalid configuration setting! Must be declared static: " + field);
				continue;
			}
			fields.add(new ConfigField(path, field, setting));
		}
		for (Class<?> nested : cls.getClasses()) {
			findFields(fields, path + nested.getSimpleName().toLowerCase() + ".", nested);
		}
		return fields;
	}

	@SuppressWarnings("unchecked")
	private static void setValue(CommentedConfigurationNode node, Field field) throws IllegalArgumentException, IllegalAccessException, ObjectMappingException {
		Object value = field.get(null);

		//Don't delete nodes if their values are null 
		if (value != null) {
			node.setValue((TypeToken<Object>) TypeToken.of(field.getType()), value);
		}
	}

	//	private static String getConfigKey(Class<?> cls) {
	//		String name = cls.getSimpleName();
	//		StringBuilder sb = new StringBuilder();
	//		for (char c : name.toCharArray()) {
	//			if (Character.isLetterOrDigit(c)) {
	//				sb.append(Character.toLowerCase(c));
	//			} else if (c == ' ') {
	//				sb.append('-');
	//			}
	//		}
	//	}

}

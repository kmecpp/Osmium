package com.kmecpp.osmium.api.config;

import java.io.File;
import java.io.IOException;

public abstract class ConfigHandler {

	private OsmiumConfig config;

	public ConfigHandler(OsmiumConfig config) {
		this.config = config;
	}

	//	private ArrayList<ConfigField> findFields(ArrayList<ConfigField> fields, String path, Class<?> cls) {
	//		boolean first = true;
	//		for (Field field : cls.getFields()) {
	//			Setting setting = field.getAnnotation(Setting.class);
	//			if (setting == null) {
	//				continue;
	//			}
	//			if (!Modifier.isStatic(field.getModifiers())) {
	//				OsmiumLogger.warn("Invalid configuration setting! Must be declared static: " + field);
	//				continue;
	//			}
	//			fields.add(new ConfigField(path, field, setting, first));
	//			first = false;
	//		}
	//		for (Class<?> nested : cls.getClasses()) {
	//			findFields(fields, path + nested.getSimpleName().toLowerCase() + ".", nested);
	//		}
	//		return fields;
	//		//		return null;
	//	}

	public abstract void load(File file) throws IOException;

	public abstract Object getValue(String[] path);

	public abstract void serialize(OsmiumConfig config);

	public abstract void deserailize();

	public String multiply(String str, int amount) {
		char[] result = new char[str.length() * amount];
		char[] fill = str.toCharArray();
		for (int i = 0; i < amount; i++) {
			System.arraycopy(fill, 0, result, i * fill.length, fill.length);
		}
		return new String(result);
	}

}

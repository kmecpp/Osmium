package com.kmecpp.osmium.api.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.kmecpp.osmium.api.util.IOUtil;

public class ConfigWriter {

	private ConfigData data;
	private File file;

	private StringBuilder sb = new StringBuilder();
	private StringBuilder tab = new StringBuilder();

	public ConfigWriter(ConfigData data, File file) {
		this.data = data;
		this.file = file;
	}

	public void write() throws IOException {
		writeBlock(data.getRoot());
		IOUtil.write(file, sb.toString());
	}

	private void writeBlock(Block block) {
		if (!block.isRoot()) {
			sb.append('\n');
		}
		//		final char[] tab = new char[block.getDepth()];
		//		Arrays.fill(tab, '\t');

		if (!block.isRoot()) {
			sb.append(block.getName().toLowerCase() + " {\n");
		}
		boolean first = true;
		for (ConfigField field : block.getFields()) {
			//Line spacing
			if (!first) {
				sb.append('\n');
			}
			first = false;

			//Add comment
			if (!field.getSetting().comment().isEmpty()) {
				sb.append("#" + field.getSetting().comment() + "\n");
			}

			//Write key
			sb.append(tab);
			sb.append(field.getName() + ": ");
			if (field.getType().isPrimitive() || field.getType() == String.class) {
				sb.append(String.valueOf(field.getValue()) + "\n");
			} else {
				writeValue(field.getType(), field.getValue());
				sb.append('\n');
			}

		}
		for (Block nestedBlock : block.getBlocks()) {
			tab.append('\t');
			writeBlock(nestedBlock);
			tab.setLength(tab.length() - 1);
		}

		if (!block.isRoot()) {
			sb.append(tab);
			if (tab.length() > 0) {
				sb.setLength(sb.length() - 1);
			}
			sb.append("}");
		}
	}

	//	private void writeSetting(char[] tab, Class<?> type, String key, Object value) {
	//
	//	}

	private void writeValue(Class<?> type, Object value) {
		//Write value
		if (type.isArray()) {
			//Write primitive arrays using Arrays.toString()
			if (type.getComponentType().isPrimitive()) {
				if (type == byte[].class) {
					sb.append(Arrays.toString((byte[]) value));
				} else if (type == short[].class) {
					sb.append(Arrays.toString((short[]) value));
				} else if (type == int[].class) {
					sb.append(Arrays.toString((int[]) value));
				} else if (type == long[].class) {
					sb.append(Arrays.toString((long[]) value));
				} else if (type == float[].class) {
					sb.append(Arrays.toString((float[]) value));
				} else if (type == double[].class) {
					sb.append(Arrays.toString((double[]) value));
				} else if (type == boolean[].class) {
					sb.append(Arrays.toString((boolean[]) value));
				} else if (type == char[].class) {
					sb.append(Arrays.toString((char[]) value));
				}
			} else {
				//Write general array
				Object[] arr = (Object[]) value;
				writeList(arr, null);
			}
		}

		else if (Collection.class.isAssignableFrom(type)) {
			Collection<?> collection = (Collection<?>) value;
			writeList(null, collection);
		}

		else if (Map.class.isAssignableFrom(type)) {
			sb.setLength(sb.length() - 2);
			sb.append(" {\n");
			tab.append('\t');
			Map<?, ?> map = (Map<?, ?>) value;
			for (Entry<?, ?> entry : map.entrySet()) {
				sb.append(tab);
				sb.append(entry.getKey() + ": ");
				writeValue(entry.getValue().getClass(), entry.getValue());
				sb.append('\n');
			}
			tab.setLength(tab.length() - 1);
			sb.append(tab);
			sb.append("}");
		}

		else {
			String str = ConfigTypes.serialize(value);
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				sb.append(c);
				if (c == '\n') {
					sb.append(tab);
				}
			}
		}
	}

	private void writeList(Object[] arr, Collection<?> collection) {
		sb.append("[\n");
		tab.append('\t');
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				writeElement(arr[i], i == arr.length - 1);
			}
		} else {
			int i = 0, length = collection.size();
			for (Object obj : collection) {
				writeElement(obj, i == length - 1);
				i++;
			}
		}
		tab.setLength(Math.max(0, tab.length() - 1));
		sb.append(tab);
		sb.append(']');
	}

	private void writeElement(Object value, boolean last) {
		sb.append(tab);
		writeValue(value == null ? Object.class : value.getClass(), value);
		sb.append(",\n");
	}

}

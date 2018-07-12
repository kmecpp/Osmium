package com.kmecpp.osmium.api.database;

import java.util.ArrayList;
import java.util.Iterator;

public class DBList implements CustomSerialization, Iterable<String> {

	private ArrayList<String> list = new ArrayList<>();

	public DBList() {
	}

	public DBList(String text) {
		for (String str : text.split(",")) {
			if (str.startsWith("|") && !str.startsWith("\\|")) {
				list.set(list.size() - 1, list.get(list.size() - 1) + "," + str.substring(1).replace("\\|", "|"));
			} else {
				list.add(str.replace("\\|", "|"));
			}
		}
	}

	@Override
	public String toString() {
		if (list.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String str : list) {
			sb.append(str.replace("|", "\\|").replace(",", ",|") + ",");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	public int size() {
		return list.size();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public boolean contains(String str) {
		return list.contains(str);
	}

	public void add(String str) {
		list.add(str);
	}

	public void add(int index, String str) {
		list.add(index, str);
	}

	public void remove(int index) {
		list.remove(index);
	}

	public void remove(Object obj) {
		list.remove(obj);
	}

	@Override
	public Iterator<String> iterator() {
		return list.iterator();
	}

	@Override
	public String serialize() {
		return toString();
	}

}

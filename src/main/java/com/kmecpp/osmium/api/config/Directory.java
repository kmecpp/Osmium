//package com.kmecpp.osmium.api.config;
//
//import java.io.File;
//
//import com.kmecpp.enjinnews.EnjinNews;
//
//public enum Directory {
//
//	ROOT(""),
//	PLUGIN("plugins" + File.separator + EnjinNews.NAME),
//	USERDATA(PLUGIN + File.separator + "userdata");
//
//	private final String path;
//
//	private Directory(String path) {
//		this.path = path;
//	}
//
//	public File getFile(String path) {
//		return new File(this.path + File.separator + path);
//	}
//
//	public YamlConfig getConfig(String path) {
//		return new YamlConfig(getFile(path));
//	}
//
//	@Override
//	public String toString() {
//		return path;
//	}
//
//}

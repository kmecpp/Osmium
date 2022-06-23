//package com.kmecpp.osmium;
//
//import java.net.URL;
//import java.net.URLClassLoader;
//
//public class OsmiumClassLoader extends URLClassLoader {
//
//	public OsmiumClassLoader(ClassLoader parent) {
//		super(new URL[0], parent);
//	}
//
//	@Override
//	public void addURL(URL url) {
//		super.addURL(url);
//	}
//
//	@Override
//	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//		return super.loadClass(name, false);
//	}
//
//}

package com.kmecpp.osmium.compile;

import java.io.IOException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class FileManager extends ForwardingJavaFileManager<JavaFileManager> {

	private static final StandardJavaFileManager STANDARD_JAVA_FILE_MANAGER = CodeCompiler.JAVAC.getStandardFileManager(null, null, null);

	private JavaClassLoader classLoader = JavaClassLoader.create();

	public FileManager(String className) {
		super(STANDARD_JAVA_FILE_MANAGER);
		this.classLoader.addClass(new JavaClass(className));
	}

	public JavaClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
		return classLoader.getClass(className);
	}

	public byte[] getBytecode(String className) {
		return classLoader.getClass(className).getByteCode();
	}

	public Class<?> loadClass(String className) throws ClassNotFoundException {
		return classLoader.loadClass(className);
	}

}

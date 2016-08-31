package com.kmecpp.osmium.compile;

import java.io.IOException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class FileManager extends ForwardingJavaFileManager<JavaFileManager> {

	private CompiledCode compiledCode;
	private DynamicClassLoader classLoader;

	protected FileManager(JavaFileManager fileManager, CompiledCode compiledCode, DynamicClassLoader classLoader) {
		super(fileManager);
		this.compiledCode = compiledCode;
		this.classLoader = classLoader;
		this.classLoader.setCode(compiledCode);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
		return compiledCode;
	}

}

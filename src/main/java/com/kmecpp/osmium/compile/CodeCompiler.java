
package com.kmecpp.osmium.compile;

import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class CodeCompiler {

	public static final JavaCompiler JAVAC = ToolProvider.getSystemJavaCompiler();

	//	public static Class<?> compileClass(String className, String source) throws Exception {
	//		SourceCode sourceCode = new SourceCode(className, source);
	//		CompiledCode compiledCode = new CompiledCode(className);
	//		DynamicClassLoader cl = DynamicClassLoader.create();
	//		com.kmecpp.osmium.compile.FileManager fileManager = new com.kmecpp.osmium.compile.FileManager(JAVAC.getStandardFileManager(null, null, null), compiledCode, cl);
	//		JAVAC.getTask(null, fileManager, null, null, null, Arrays.asList(sourceCode)).call();
	//		return cl.loadClass(className);
	//	}

	public static Class<?> compileClass(String className, String source) throws Exception {
		JavaSource sourceCode = new JavaSource(className, source);
		FileManager fileManager = new FileManager(className);
		JAVAC.getTask(null, fileManager, null, null, null, Arrays.asList(sourceCode)).call();
		return fileManager.loadClass(className);
	}

	public static byte[] getBytecode(String className, String source) {
		FileManager fileManager = new FileManager(className);
		fileManager.getClassLoader().addClass(new JavaClass("com.kmecpp.osmium.platform.bukkit.BukkitPlugin"));
		JAVAC.getTask(null, fileManager, null, null, null, Arrays.asList(new JavaSource(className, source))).call();
		return fileManager.getBytecode(className);
	}

}

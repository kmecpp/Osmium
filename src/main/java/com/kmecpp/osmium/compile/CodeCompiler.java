package com.kmecpp.osmium.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CodeCompiler {

	public static final JavaCompiler JAVAC = ToolProvider.getSystemJavaCompiler();

	public static Class<?> compileClass(String className, String source) throws Exception {
		SourceCode sourceCode = new SourceCode(className, source);
		CompiledCode compiledCode = new CompiledCode(className);
		DynamicClassLoader cl = DynamicClassLoader.create();
		com.kmecpp.osmium.compile.FileManager fileManager = new com.kmecpp.osmium.compile.FileManager(JAVAC.getStandardFileManager(null, null, null), compiledCode, cl);
		JAVAC.getTask(null, fileManager, null, null, null, Arrays.asList(sourceCode)).call();
		return cl.loadClass(className);
		//		return null;
	}

	public static JavaFileObject compileClassFile(String className, String source) {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(source);
		out.close();
		JavaFileObject file = new JavaSource(className, writer.toString());

		JAVAC.getTask(null, new FileManager(className), null, null, null, Arrays.asList(file)).call();
		return file;
	}

	public static class JavaSource extends SimpleJavaFileObject {

		final String code;

		public JavaSource(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}

	}

	public static class JavaClass extends SimpleJavaFileObject {

		private ByteArrayOutputStream baos = new ByteArrayOutputStream();

		public JavaClass(String className) {
			super(getUri(className), Kind.CLASS);
		}

		private static final URI getUri(String className) {
			try {
				return new URI(className);
			} catch (URISyntaxException e) {
				throw new RuntimeException("Could not parse URI", e);
			}
		}

		public byte[] getByteCode() {
			return baos.toByteArray();
		}

	}

	public static class FileManager extends ForwardingJavaFileManager<JavaFileManager> {

		private static final StandardJavaFileManager STANDARD_JAVA_FILE_MANAGER = JAVAC.getStandardFileManager(null, null, null);

		private JavaClassLoader classLoader = JavaClassLoader.create();
		//		public JavaClass javaClass;

		public FileManager(String className) {
			super(STANDARD_JAVA_FILE_MANAGER);
			//			JavaClass javaClass = new JavaClass(className);
			//			this.javaClass = javaClass;
			this.classLoader.setClass(new JavaClass(className));

		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
			return classLoader.getClass(className);
		}

	}

	public static class JavaClassLoader extends ClassLoader {

		private final Map<String, JavaClass> classes = new HashMap<>();

		public static JavaClassLoader create() {
			return new JavaClassLoader(ClassLoader.getSystemClassLoader());
		}

		public JavaClassLoader(ClassLoader parent) {
			super(parent);
		}

		public JavaClass getClass(String name) {
			return classes.get(name);
		}

		public JavaClassLoader setClass(JavaClass cc) {
			classes.put(cc.getName(), cc);
			return this;
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			JavaClass cc = classes.get(name);
			if (cc == null) {
				return super.findClass(name);
			}
			byte[] byteCode = cc.getByteCode();
			return defineClass(name, byteCode, 0, byteCode.length);
		}

	}

}

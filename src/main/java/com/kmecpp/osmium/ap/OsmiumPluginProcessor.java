package com.kmecpp.osmium.ap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;

import com.kmecpp.jflame.Json;
import com.kmecpp.jflame.value.JsonArray;
import com.kmecpp.jlib.object.Objects;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumMeta;
import com.kmecpp.osmium.api.plugin.OsmiumPluginDataContainer;
import com.kmecpp.osmium.platform.bukkit.BukkitPlugin;
import com.kmecpp.osmium.platform.sponge.SpongePlugin;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

@SupportedAnnotationTypes({
		"com.kmecpp.osmium.api.plugin.OsmiumMeta"
})
public class OsmiumPluginProcessor extends AbstractProcessor {

	private final HashMap<String, OsmiumPluginDataContainer> plugins = new HashMap<>();

	//	private Path outputPath;

	//	@Override
	//	public synchronized void init(ProcessingEnvironment processingEnv) {
	//		super.init(processingEnv);
	//
	//		String outputFile = processingEnv.getOptions().get(Platform.getPlatform().getMetaFile());
	//		if (outputFile != null && !outputFile.isEmpty()) {
	//			this.outputPath = Paths.get(outputFile);
	//		}
	//	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			if (!roundEnv.errorRaised()) {
				finish();
			}
			return true;
		}

		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(OsmiumMeta.class);
		if (elements.size() == 0) {
			return false;
		}

		for (Element e : elements) {
			System.out.println("ITERATING THROUGH: " + e.getSimpleName());
			if (e.getKind() != ElementKind.CLASS) {
				error("Invalid element of type " + e.getKind() + " annotated with @" + OsmiumMeta.class.getSimpleName());
				continue;
			}

			OsmiumMeta annotation = e.getAnnotation(OsmiumMeta.class);
			String id = annotation.name().toLowerCase();
			if (!plugins.containsKey(id)) {
				plugins.put(id, new OsmiumPluginDataContainer(e.getClass(),
						annotation.name(),
						annotation.version(),
						annotation.description(),
						annotation.url(),
						annotation.authors(),
						annotation.dependencies()));
				System.out.println(Objects.toClassString(plugins.get(id)));
			} else {
				error("Plugin with id '" + id + "' already exists!");
			}

		}
		return true;
	}

	private void finish() {
		if (plugins.size() > 1) {
			error("Multiple classes found with @" + OsmiumMeta.class.getSimpleName() + " annotation! Only one is permitted for Bukkit compatibility.");
		} else if (plugins.size() < 1) {
			error("Failed to find an @" + OsmiumMeta.class.getSimpleName() + " annotated class!");
		}
		if (plugins.size() != 1) {
			return;
		}

		Entry<String, OsmiumPluginDataContainer> entry = plugins.entrySet().iterator().next();
		OsmiumPluginDataContainer meta = entry.getValue();

		//GENERATE META FILES

		//Sponge mcmod.info
		JsonArray plugins = Json.array();
		plugins.add(Json.object()
				.add("modid", entry.getKey())
				.add("name", meta.getName())
				.add("version", meta.getVersion())
				.add("description", meta.getDescription())
				.add("url", meta.getUrl())
				.add("authorList", JsonArray.from(meta.getAuthors()))
				.add("dependencies", JsonArray.from(meta.getDependencies())));
		writeRawFile(Platform.SPONGE.getMetaFile(), plugins.toFormattedString());

		//Bukkit plugin.yml
		StringBuilder sb = new StringBuilder()
				.append("name: " + meta.getName() + "\n")
				.append("main: " + meta.getName() + "Bukkit" + "\n")
				.append("version: " + meta.getVersion() + "\n")
				.append("description: " + meta.getDescription() + "\n")
				.append("website: " + meta.getUrl() + "\n")
				.append("authors: " + Arrays.toString(meta.getAuthors()) + "\n")
				.append("dependencies: " + Arrays.toString(meta.getDependencies()) + "\n");
		writeRawFile(Platform.BUKKIT.getMetaFile(), sb.toString());

		//GENERATE MAIN CLASSES
		//		String SpongeClass = meta.getName() + Platform.SPONGE.getName();
		//		String BukkitClass = meta.getName() + Platform.BUKKIT.getName();

		try {

			//			System.out.println(file.toGenericString());
			//			createJarFile("com.kmecpp.osmium.platform.sponge.SpongePluginAnnotated", ""
			//					+ "import org.spongepowered.api.plugin.Plugin;"
			//					+ "import org.spongepowered.api.plugin.Dependency;"
			//					+ "@Plugin("
			//					+ "id = \"" + meta.getName().toLowerCase() + "\","
			//					+ "name = \"" + meta.getName() + "\","
			//					+ "version = \"" + meta.getVersion() + "\","
			//					+ "description = \"" + meta.getDescription() + "\","
			//					+ "authors = { \"kmecpp\" },"
			//					+ "dependencies = { @Dependency(id = \"Depend\", optional = true) },"
			//					+ "url = \"Url\""
			//					+ ")"
			//					+ "public class SpongePluginAnnotated {"
			//					+ "}");

			//GENERATE MAIN CLASSES
			//			String pkg = meta.getClass().getPackage().getName();
			final String SpongeClass = meta.getName() + Platform.SPONGE.getName();//pkg + ".osmium." + meta.getName() + Platform.SPONGE.getName();
			final String BukkitClass = meta.getName() + Platform.BUKKIT.getName();//pkg + ".osmium." + meta.getName() + Platform.BUKKIT.getName();
			ClassPool pool = ClassPool.getDefault();

			//Bukkit
			pool.insertClassPath(new ClassClassPath(BukkitPlugin.class));
			CtClass ctClassBukkit = pool.makeClass(BukkitClass); //"com.kmecpp.osmium.platform.sponge.SpongePluginAnnotated"

			ctClassBukkit.setSuperclass(pool.getCtClass(BukkitPlugin.class.getName()));
			writeMainClass(ctClassBukkit);

			//Sponge
			pool.insertClassPath(new ClassClassPath(SpongePlugin.class));

			CtClass ctClassSponge = pool.makeClass(SpongeClass); //"com.kmecpp.osmium.platform.sponge.SpongePluginAnnotated"
			ClassFile classFile = ctClassSponge.getClassFile();
			ConstPool cpool = classFile.getConstPool();

			//Set annotation
			AnnotationsAttribute attribute = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
			Annotation annotation = new Annotation("org.spongepowered.api.plugin.Plugin", cpool);
			annotation.addMemberValue("id", new StringMemberValue(meta.getName().toLowerCase(), cpool));
			annotation.addMemberValue("name", new StringMemberValue(meta.getName(), cpool));
			annotation.addMemberValue("version", new StringMemberValue(meta.getVersion(), cpool));
			annotation.addMemberValue("description", new StringMemberValue(meta.getDescription(), cpool));
			annotation.addMemberValue("authors", getAuthors(meta, cpool));
			annotation.addMemberValue("dependencies", getDependencies(meta, cpool));
			annotation.addMemberValue("url", new StringMemberValue(meta.getUrl(), cpool));
			attribute.addAnnotation(annotation);
			classFile.addAttribute(attribute);

			//Set superclass
			ctClassSponge.setSuperclass(pool.getCtClass(SpongePlugin.class.getName()));

			//Write file
			writeMainClass(ctClassSponge);

			//			c.writeFile("target/classes");

			//			System.out.println("CONTENTS: " + file.getCharContent(false));
			//			System.out.println("URI: " + file.toUri());
			//			createJarFile("SpongePluginAnnotated", file.getCharContent(false).toString());

			//			System.out.println(new BufferedReader(new InputStreamReader(file.openInputStream())).readLine());

			//			OutputStream outputStream = processingEnv.getFiler().createClassFile("SpongePluginAnnotated").openOutputStream();
			//			CtClass cc = ClassPool.getDefault().makeClass(SpongePlugin.class.getName() + "Annotated");
			//			ConstPool constantPool = cc.getClassFile().getConstPool();
			//			AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			//			Annotation annotation = new Annotation("Plugin", constpool);
			//
			//			Annotation a = new Annotation("Plugin", constantPool);
			//			a.("name", new StringMemberValue("Chiba", cp));
			//			attr.setAnnotation(a);
			//			cf.addAttribute(attr);

			//			cc.getClassFile().outputStream.write(cc.toBytecode());
			//			outputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ArrayMemberValue getAuthors(OsmiumPluginDataContainer meta, ConstPool cpool) {
		ArrayMemberValue arrayMember = new ArrayMemberValue(cpool);
		String[] authors = meta.getAuthors();
		MemberValue[] elements = new MemberValue[authors.length];
		for (int i = 0; i < authors.length; i++) {
			elements[i] = new StringMemberValue(authors[i], cpool);
		}
		arrayMember.setValue(elements);
		return arrayMember;
	}

	public ArrayMemberValue getDependencies(OsmiumPluginDataContainer meta, ConstPool cpool) {
		ArrayMemberValue arrayMember = new ArrayMemberValue(cpool);
		String[] dependencies = meta.getDependencies();
		MemberValue[] elements = new MemberValue[dependencies.length];
		for (int i = 0; i < dependencies.length; i++) {
			elements[i] = new StringMemberValue(dependencies[i], cpool);
		}
		arrayMember.setValue(elements);
		return arrayMember;
	}

	public void writeMainClass(CtClass ctClass) throws CannotCompileException, IOException {
		ctClass.writeFile(new File(processingEnv.getFiler().createClassFile("root").toUri()).getParent());
	}

	public void writeRawFile(String file, String contents) {
		try (BufferedWriter writer = new BufferedWriter(
				processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", file).openWriter())) {
			writer.write(contents);
		} catch (IOException e) {
			throw new RuntimeException("Failed to create zipped file: '" + file + "'!", e);
		}
	}

	public void info(String message) {
		getMessager().printMessage(Kind.NOTE, "[" + Osmium.OSMIUM + "] " + message);
	}

	public void error(String message) {
		getMessager().printMessage(Kind.ERROR, "[" + Osmium.OSMIUM + "] " + message);
	}

	public Messager getMessager() {
		return this.processingEnv.getMessager();
	}

}

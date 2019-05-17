package com.kmecpp.osmium.ap;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.spongepowered.plugin.meta.McModInfo;
import org.spongepowered.plugin.meta.PluginDependency;
import org.spongepowered.plugin.meta.PluginDependency.LoadOrder;
import org.spongepowered.plugin.meta.PluginMetadata;

import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumMetaContainer;
import com.kmecpp.osmium.api.plugin.Plugin;
import com.kmecpp.osmium.api.plugin.SpongePlugin;
import com.kmecpp.osmium.api.util.StringUtil;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewConstructor;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

@SupportedAnnotationTypes({ "com.kmecpp.osmium.api.plugin.Plugin" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class OsmiumPluginProcessor extends OsmiumAnnotationProcessor {

	//Cannot use direct class references as one will not exist
	public static final String BUKKIT_PARENT = "com.kmecpp.osmium.api.plugin.BukkitPlugin";
	public static final String SPONGE_PARENT = "com.kmecpp.osmium.api.plugin.SpongePlugin";

	//	public static final String SPONGE_PLUGIN_ANNOTATION_CLASS = "org.spongepowered.api.plugin.Plugin";

	private final HashMap<String, OsmiumMetaContainer> plugins = new HashMap<>();

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		info("Generating platform specific files");
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			if (!roundEnv.errorRaised()) {
				finish();
			}
			return true;
		}

		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Plugin.class);
		if (elements.size() == 0) {
			return false;
		}

		for (Element e : elements) {
			try {
				if (e.getKind() != ElementKind.CLASS) {
					error("Invalid element of type " + e.getKind() + " annotated with @" + Plugin.class.getSimpleName());
					continue;
				}

				Plugin annotation = e.getAnnotation(Plugin.class);
				String id = annotation.name().replace(' ', '-').toLowerCase();
				if (!plugins.containsKey(id)) {
					plugins.put(id, new OsmiumMetaContainer(((TypeElement) e).getQualifiedName().toString(),
							annotation.name(), annotation.version(), annotation.description(), annotation.url(),
							annotation.authors(), annotation.dependencies()));
				} else {
					error("Plugin with id '" + id + "' already exists!");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return true;
	}

	private void finish() {
		if (plugins.size() > 1) {
			error("Multiple classes found with @" + Plugin.class.getSimpleName() + " annotation! Only one is permitted for Bukkit compatibility.");
		} else if (plugins.size() < 1) {
			error("Failed to find an @" + Plugin.class.getSimpleName() + " annotated class!");
		}
		if (plugins.size() != 1) {
			return;
		}

		Entry<String, OsmiumMetaContainer> entry = plugins.entrySet().iterator().next();
		OsmiumMetaContainer meta = entry.getValue();

		// GENERATE META FILES
		info("Generating plugin metafiles for annotation: " + StringUtil.toString(meta));

		PluginMetadata modinfo = new PluginMetadata(entry.getKey());
		//				.setVersion(meta.getVersion())
		//				.setDescription(meta.getDescription())
		//				.setUrl(meta.getUrl())
		//				.addAuthors(meta.getAuthors());
		modinfo.setVersion(meta.getVersion());
		modinfo.setDescription(meta.getDescription());
		modinfo.setUrl(meta.getUrl());
		for (String author : meta.getAuthors()) {
			modinfo.addAuthor(author);
		}
		for (String dependency : meta.getSpongeDependencies()) {
			modinfo.addDependency(new PluginDependency(LoadOrder.BEFORE, dependency.toLowerCase(), "", false));
		}
		try {
			McModInfo.builder().build().write(getWriter(Platform.SPONGE.getMetaFile()), modinfo);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write mcmod.info file into jar!", e);
		}

		// Sponge mcmod.info
		//		JsonArray plugins = new JsonArray();
		//		plugins.add(new JsonObject().add("modid", entry.getKey())
		//				.add("name", meta.getName())
		//				.add("version", meta.getVersion())
		//				.add("description", meta.getDescription())
		//				.add("url", meta.getUrl())
		//				.add("authorList", JsonArray.from(meta.getAuthors()))
		//				.add("dependencies", JsonArray.from(meta.getDependencies()))
		//				.add("requiredMods", JsonArray.from(new String[] { "spongeapi@" + AppInfo.SPONGE_VERSION })));
		//		writeRawFile(Platform.SPONGE.getMetaFile(), plugins.getFormatted());

		// Bukkit plugin.yml
		StringBuilder pluginYml = new StringBuilder().append("name: " + meta.getName() + "\n")
				.append("main: " + meta.getName() + "Bukkit" + "\n")
				.append("version: " + meta.getVersion() + "\n")
				.append("description: " + meta.getDescription() + "\n")
				.append("website: " + meta.getUrl() + "\n")
				.append("authors: " + Arrays.toString(meta.getAuthors()) + "\n")
				.append("softdepend: " + Arrays.toString(meta.getBukkitDependencies()) + "\n");
		writeRawFile(Platform.BUKKIT.getMetaFile(), pluginYml.toString());

		StringBuilder osmiumYml = new StringBuilder().append("main: " + meta.getSourceClass() + "\n").append("name: " + meta.getName());
		writeRawFile("osmium.properties", osmiumYml.toString());

		try {
			// GENERATE MAIN CLASSES
			ClassPool pool = ClassPool.getDefault();

			CtClass ctClassBukkit = pool.makeClass(meta.getName() + Platform.BUKKIT.getName());
			ctClassBukkit.setSuperclass(pool.makeClass(BUKKIT_PARENT));
			ctClassBukkit.addConstructor(CtNewConstructor.make(null, null, CtNewConstructor.PASS_PARAMS, null, null, ctClassBukkit));
			writeClassToRoot(ctClassBukkit);

			// Sponge
			pool.insertClassPath(new ClassClassPath(SpongePlugin.class));

			CtClass ctClassSponge = pool.makeClass(meta.getName() + Platform.SPONGE.getName());
			ctClassSponge.setSuperclass(pool.getCtClass(SpongePlugin.class.getName()));
			ClassFile classFile = ctClassSponge.getClassFile();
			ConstPool cpool = classFile.getConstPool();

			// Set annotation
			AnnotationsAttribute attribute = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
			Annotation annotation = new Annotation("org.spongepowered.api.plugin.Plugin", cpool); //CANNOT USE DIRECT CLASS REFERENCE
			annotation.addMemberValue("id", new StringMemberValue(meta.getName().toLowerCase(), cpool));
			//			annotation.addMemberValue("name", new StringMemberValue(meta.getName(), cpool));
			//			annotation.addMemberValue("version", new StringMemberValue(meta.getVersion(), cpool));
			//			annotation.addMemberValue("description", new StringMemberValue(meta.getDescription(), cpool));
			//			annotation.addMemberValue("authors", getAuthors(meta, cpool));
			//			annotation.addMemberValue("dependencies", getDependencies(meta, cpool));
			//			annotation.addMemberValue("url", new StringMemberValue(meta.getUrl(), cpool));
			attribute.addAnnotation(annotation);
			classFile.addAttribute(attribute);

			// Write file
			writeClassToRoot(ctClassSponge);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ArrayMemberValue getAuthors(OsmiumMetaContainer meta, ConstPool cpool) {
		ArrayMemberValue arrayMember = new ArrayMemberValue(cpool);
		String[] authors = meta.getAuthors();
		MemberValue[] elements = new MemberValue[authors.length];
		for (int i = 0; i < authors.length; i++) {
			elements[i] = new StringMemberValue(authors[i], cpool);
		}
		arrayMember.setValue(elements);
		return arrayMember;
	}

}

package com.kmecpp.osmium.ap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.kmecpp.osmium.api.Transient;
import com.kmecpp.osmium.api.config.ConfigClass;
import com.kmecpp.osmium.api.config.ConfigSerializable;
import com.kmecpp.osmium.api.config.MapSerializable;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "com.kmecpp.osmium.api.config.ConfigClass", "com.kmecpp.osmium.api.config.MapSerializable",
		"com.kmecpp.osmium.api.config.ConfigSerializable", })
public class ConfigTypeProcessor extends OsmiumAnnotationProcessor {

	private static StringBuilder data = new StringBuilder();

	private static HashSet<Element> mapSerializableElements = new HashSet<>();

	//	privaet static 

	public void finish() {
		process(mapSerializableElements);

		if (data.length() > 0) {
			writeRawFile("CONFIG_TYPES", data.toString());
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			if (!roundEnv.errorRaised()) {
				finish();
			}
			return true;
		}

		//		Set<? extends Element> plugins = roundEnv.getElementsAnnotatedWith(Plugin.class);
		//		for (Element plugin : plugins) {
		//			if (plugin.getKind() == ElementKind.CLASS) {
		//				String className = String.valueOf(plugin);
		//				Reflection.getPackageName(className);
		//				System.out.println(plugin);
		//				System.out.println(plugin.getSimpleName());
		//			}
		//		}

		Set<? extends Element> mapSerializable = roundEnv.getElementsAnnotatedWith(ConfigSerializable.class);
		mapSerializableElements.addAll(mapSerializable);

		Set<? extends Element> configElements = roundEnv.getElementsAnnotatedWith(ConfigClass.class);
		Set<? extends Element> dataElements = roundEnv.getElementsAnnotatedWith(MapSerializable.class);

		process(configElements);
		process(dataElements);

		if (configElements.isEmpty() && dataElements.isEmpty()) {
			return false;
		}

		return true;
	}

	public static void process(Set<? extends Element> configs) {
		for (Element element : configs) {
			if (data.length() > 0) {
				data.append('\n');
			}
			data.append(getPath(element) + ":\n");
			process(element);
		}
	}

	public static void process(Element parent) {
		final ElementKind[] kinds = { ElementKind.FIELD, ElementKind.CLASS }; //Do this to impose field, nested order

		for (ElementKind kind : kinds) {
			for (Element enclosed : parent.getEnclosedElements()) {
				if (enclosed.getKind() != kind || enclosed.getAnnotation(Transient.class) != null) {
					continue;
				}
				if (enclosed.getKind().isField()) {
					if (enclosed.asType() instanceof DeclaredType && !((DeclaredType) enclosed.asType()).getTypeArguments().isEmpty()) {
						data.append(getPath(enclosed) + "=" + getType(enclosed.asType()) + "\n");
					}
				} else {
					process(enclosed);
				}
			}
		}

	}

	/*
	 * getPath() and getType() get the fully qualified class names of their
	 * respective objects. This is needed because Class.forName() expects $ as
	 * the delimiter for nested classes in the name
	 */
	private static String getPath(Element element) {
		ArrayList<String> parts = new ArrayList<>();
		Element current = element;
		while (true) {
			if (current.getKind() == ElementKind.PACKAGE) {
				parts.add(0, current.toString());
				break;
			}

			boolean nestedClass = current.getKind() == ElementKind.CLASS && current.getEnclosingElement().getKind() == ElementKind.CLASS;
			parts.add(0, current.getSimpleName().toString());
			parts.add(0, nestedClass ? "$" : ".");
			current = current.getEnclosingElement();
		}
		return String.join("", parts);
	}

	private static String getType(TypeMirror typeMirror) {
		if (typeMirror.getKind() != TypeKind.DECLARED) {
			return typeMirror.toString();
		}

		DeclaredType type = (DeclaredType) typeMirror;
		StringBuilder sb = new StringBuilder();

		String baseType = getPath(type.asElement());
		sb.append(baseType);

		List<? extends TypeMirror> arguments = type.getTypeArguments();
		if (!arguments.isEmpty()) {
			sb.append('<');
			for (int i = 0; i < arguments.size(); i++) {
				TypeMirror arg = arguments.get(i);
				sb.append(getType(arg));
				if (i < arguments.size() - 1) {
					sb.append(',');
				}
			}
			sb.append('>');
		}
		return sb.toString();
	}

}

package com.kmecpp.osmium.ap;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.kmecpp.osmium.api.config.ConfigClass;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "com.kmecpp.osmium.api.config.ConfigClass" })
public class ConfigTypeProcessor extends OsmiumAnnotationProcessor {

	private static StringBuilder data = new StringBuilder();

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			if (!roundEnv.errorRaised()) {
				finish();
			}
			return true;
		}

		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ConfigClass.class);
		if (elements.size() == 0) {
			return false;
		}

		for (Element element : elements) {
			if (data.length() > 0) {
				data.append('\n');
			}
			String path = element.toString();
			data.append(path + ":\n");
			process(element);
		}
		return true;
	}

	public static void process(Element parent) {
		final ElementKind[] kinds = { ElementKind.FIELD, ElementKind.CLASS }; //Do this to impose field, nested order

		for (ElementKind kind : kinds) {
			for (Element enclosed : parent.getEnclosedElements()) {
				if (enclosed.getKind() != kind) {
					continue;
				}
				String location = enclosed.getEnclosingElement().toString() + "." + enclosed.getSimpleName();
				//				String subpath = location.substring(start);
				if (enclosed.getKind().isField()) {
					data.append(location + "=" + enclosed.asType() + "\n");
				} else {
					//					data.append(location + ":\n");
					process(enclosed);
				}
			}
		}

	}

	public void finish() {
		if (data.length() > 0) {
			writeRawFile("CONFIG_TYPES", data.toString());
		}
	}

}

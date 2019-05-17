package com.kmecpp.osmium.ap;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.plugin.Startup;
import com.kmecpp.osmium.api.tasks.Schedule;

@SupportedAnnotationTypes({ "com.kmecpp.osmium.api.event.Listener", "com.kmecpp.osmium.api.tasks.Schedule", "com.kmecpp.osmium.api.plugin.Startup" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class OsmiumMethodAnnotationProcessor extends OsmiumAnnotationProcessor {

	private static final HashSet<String> classes = new HashSet<>();

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			if (!roundEnv.errorRaised()) {
				finish();
			}
			return true;
		}

		HashSet<Element> elements = new HashSet<>();
		elements.addAll(roundEnv.getElementsAnnotatedWith(Listener.class));
		elements.addAll(roundEnv.getElementsAnnotatedWith(Schedule.class));
		elements.addAll(roundEnv.getElementsAnnotatedWith(Startup.class));

		if (elements.size() == 0) {
			return false;
		}

		for (Element element : elements) {
			TypeElement cls = (TypeElement) element.getEnclosingElement();
			classes.add(cls.getQualifiedName().toString());
		}
		return true;
	}

	public void finish() {
		writeRawFile("static-load-classes", classes.stream().collect(Collectors.joining("\n")));
	}

}

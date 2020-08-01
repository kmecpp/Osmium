package com.kmecpp.osmium.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.processing.SupportedAnnotationTypes;

import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import com.kmecpp.osmium.ap.ConfigTypeProcessor;
import com.kmecpp.osmium.ap.OsmiumClassMetadataAnnotationProcessor;
import com.kmecpp.osmium.ap.OsmiumPluginProcessor;
import com.kmecpp.osmium.api.config.ConfigClass;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.plugin.Initializer;
import com.kmecpp.osmium.api.plugin.Plugin;
import com.kmecpp.osmium.api.tasks.Schedule;
import com.kmecpp.osmium.api.util.StringUtil;

public class OsmiumTest {

	public static void main(String[] args) {
		//		JsonArray arr = Json.array("t console hi");
		//		String s = arr.toString();
		//		System.out.println(Json.parse(s));
		System.out.println(StringUtil.rjust("hello", 6));
		System.out.println(StringUtil.ljust("hello", 6));
	}

	@Test
	public void testDisable() {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));

		Result result = junit.run(OsmiumTestPlatform.class);

		resultReport(result);
	}

	@Test
	public void testAnnotationProcessors() throws ClassNotFoundException {
		assertTrue(Arrays.asList(OsmiumClassMetadataAnnotationProcessor.class.getAnnotation(SupportedAnnotationTypes.class).value())
				.containsAll(Arrays.asList(Initializer.class.getName(), Schedule.class.getName(), Listener.class.getName())));

		assertTrue(Arrays.asList(ConfigTypeProcessor.class.getAnnotation(SupportedAnnotationTypes.class).value())
				.containsAll(Arrays.asList(ConfigClass.class.getName())));

		assertTrue(Arrays.asList(OsmiumPluginProcessor.class.getAnnotation(SupportedAnnotationTypes.class).value())
				.containsAll(Arrays.asList(Plugin.class.getName())));

		Class.forName(OsmiumPluginProcessor.BUKKIT_PARENT);
		Class.forName(OsmiumPluginProcessor.SPONGE_PARENT);
	}

	public static void resultReport(Result result) {
		System.out.println("Finished. Result: Failures: " +
				result.getFailureCount() + ". Ignored: " +
				result.getIgnoreCount() + ". Tests run: " +
				result.getRunCount() + ". Time: " +
				result.getRunTime() + "ms.");
	}

}

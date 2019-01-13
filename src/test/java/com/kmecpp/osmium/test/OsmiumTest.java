package com.kmecpp.osmium.test;

import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class OsmiumTest {

	@Test
	public void testDisable() {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));

		Result result = junit.run(OsmiumTestPlatform.class);

		resultReport(result);
	}

	public static void resultReport(Result result) {
		System.out.println("Finished. Result: Failures: " +
				result.getFailureCount() + ". Ignored: " +
				result.getIgnoreCount() + ". Tests run: " +
				result.getRunCount() + ". Time: " +
				result.getRunTime() + "ms.");
	}

}

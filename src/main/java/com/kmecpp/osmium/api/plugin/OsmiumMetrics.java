package com.kmecpp.osmium.api.plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.core.OsmiumCoreConfig;

public class OsmiumMetrics {

	private static final int BSTATS_VERSION = 1;

	private static final String BUKKIT_URL = "https://bStats.org/submitData/bukkit";
	private static final String SPONGE_URL = "https://bStats.org/submitData/sponge";

	private final HashMap<OsmiumPlugin, ArrayList<CustomChart>> plugins = new HashMap<>();

	private static boolean active;

	public void register(OsmiumPlugin plugin) {
		plugins.put(plugin, new ArrayList<>());
		if (OsmiumCoreConfig.Metrics.enabled && !active) {
			active = true;
			startSubmitting();
		}
	}

	public boolean isEnabled(OsmiumPlugin plugin) {
		return plugins.get(plugin) != null;
	}

	/**
	 * Adds a custom chart.
	 *
	 * @param plugin
	 *            the plugin to add the chart to
	 * @param chart
	 *            The chart to add.
	 */
	public void addCustomChart(OsmiumPlugin plugin, CustomChart chart) {
		if (chart == null) {
			throw new IllegalArgumentException("Chart cannot be null!");
		}
		ArrayList<CustomChart> charts = plugins.get(plugin);
		if (charts == null) {
			throw new IllegalArgumentException("Plugin '" + plugin.getName() + "' does not have metrics enabled!");
		}
		charts.add(chart);
	}

	/**
	 * Starts the Scheduler which submits our data every 30 minutes.
	 */
	private void startSubmitting() {
		new Timer(true).scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				try {
					sendData(collectData());
				} catch (Exception e) {
					if (OsmiumCoreConfig.Metrics.logErrors) {
						OsmiumLogger.warn("Failed to submit Osmium plugin metrics!");
						e.printStackTrace();
					}
				}
			}
		}, 1000 * 60 * 5, 1000 * 60 * 30); //DON'T SCREW WITH THE TIME VALUES OR THE SERVER/PLUGIN WILL BE BLOCKED

		//		Osmium.schedule(CoreOsmiumPlugin.getInstance())
		//				.setAsync(true)
		//				.setDelay(5, TimeUnit.MINUTE) //DON'T SCREW WITH THE TIME VALUES OR THE SERVER/PLUGIN WILL BE BLOCKED
		//				.setInterval(30, TimeUnit.MINUTE)
		//				.start((task) -> {
		//					
		//				});
	}

	/**
	 * Gets the plugin specific data.
	 * This method is called using Reflection.
	 *
	 * @return The plugin specific data.
	 */
	//	private JsonObject getPluginData(OsmiumPlugin plugin) {
	//
	//
	//		return data;
	//	}

	private JsonObject collectData() {
		JsonObject json = new JsonObject();

		json.set("serverUUID", OsmiumCoreConfig.Metrics.serverId.toString());

		json.set("playerAmount", Osmium.getOnlinePlayers().size());
		json.set("onlineMode", Osmium.getOnlineMode() ? 1 : 0);
		if (Platform.isBukkit()) {
			String bukkitVersion = org.bukkit.Bukkit.getVersion();
			bukkitVersion = bukkitVersion.substring(bukkitVersion.indexOf("MC: ") + 4, bukkitVersion.length() - 1);
			json.set("bukkitVersion", bukkitVersion);
		} else {
			json.set("spongeImplementation", org.spongepowered.api.Sponge.getPlatform().getContainer(org.spongepowered.api.Platform.Component.IMPLEMENTATION).getName());
		}

		json.set("javaVersion", System.getProperty("java.version"));
		json.set("osName", System.getProperty("os.name"));
		json.set("osArch", System.getProperty("os.arch"));
		json.set("osVersion", System.getProperty("os.version"));
		json.set("coreCount", Runtime.getRuntime().availableProcessors());

		JsonArray pluginData = new JsonArray();

		for (Entry<OsmiumPlugin, ArrayList<CustomChart>> entry : plugins.entrySet()) {
			OsmiumPlugin plugin = entry.getKey();

			JsonObject data = new JsonObject();

			data.set("pluginName", plugin.getName()); // Append the name of the plugin
			data.set("pluginVersion", plugin.getVersion()); // Append the version of the plugin
			JsonArray customCharts = new JsonArray();
			for (CustomChart customChart : entry.getValue()) {
				JsonObject chart = customChart.getRequestJsonObject(); // Add the data of the custom charts
				if (chart != null) { // If the chart is null, we skip it
					customCharts.add(chart);
				}
			}
			data.set("customCharts", customCharts);
			pluginData.add(data);
		}

		json.set("plugins", pluginData);
		return json;
	}

	/**
	 * Sends the data to the bStats server.
	 *
	 * @param json
	 *            The data to send.
	 * @throws Exception
	 *             If the request failed.
	 */
	private static void sendData(JsonObject json) throws Exception {
		if (json == null) {
			throw new IllegalArgumentException("Data cannot be null!");
		}
		if (Bukkit.isPrimaryThread()) {
			throw new IllegalAccessException("This method must not be called from the main thread!");
		}
		HttpsURLConnection connection = (HttpsURLConnection) new URL(Platform.isBukkit() ? BUKKIT_URL : SPONGE_URL).openConnection();

		// Compress the data to save bandwidth
		byte[] compressedData = compress(json.toString());

		// Add headers
		connection.setRequestMethod("POST");
		connection.addRequestProperty("Accept", "application/json");
		connection.addRequestProperty("Connection", "close");
		connection.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
		connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
		connection.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
		connection.setRequestProperty("User-Agent", "MC-Server/" + BSTATS_VERSION);

		// Send data
		connection.setDoOutput(true);
		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		outputStream.write(compressedData);
		outputStream.flush();
		outputStream.close();

		connection.getInputStream().close(); // We don't care about the response - Just send our data :)
	}

	/**
	 * Gzips the given String.
	 *
	 * @param str
	 *            The string to gzip.
	 * @return The gzipped String.
	 * @throws IOException
	 *             If the compression failed.
	 */
	private static byte[] compress(final String str) throws IOException {
		if (str == null) {
			return null;
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
			gzip.write(str.getBytes(StandardCharsets.UTF_8));
			return outputStream.toByteArray();
		}
	}

	/**
	 * Represents a custom chart.
	 */
	public static abstract class CustomChart {

		// The id of the chart
		final String chartId;

		/**
		 * Class constructor.
		 *
		 * @param chartId
		 *            The id of the chart.
		 */
		CustomChart(String chartId) {
			if (chartId == null || chartId.isEmpty()) {
				throw new IllegalArgumentException("ChartId cannot be null or empty!");
			}
			this.chartId = chartId;
		}

		private JsonObject getRequestJsonObject() {
			JsonObject chart = new JsonObject();
			chart.set("chartId", chartId);
			try {
				JsonObject data = getChartData();
				if (data == null) {
					// If the data is null we don't send the chart.
					return null;
				}
				chart.set("data", data);
			} catch (Throwable t) {
				if (OsmiumCoreConfig.debug) {
					OsmiumLogger.debug("Failed to get data for custom chart with id " + chartId);
					t.printStackTrace();
				}
				return null;
			}
			return chart;
		}

		protected abstract JsonObject getChartData() throws Exception;

	}

	/**
	 * Represents a custom simple pie.
	 */
	public static class SimplePie extends CustomChart {

		private final Callable<String> callable;

		/**
		 * Class constructor.
		 *
		 * @param chartId
		 *            The id of the chart.
		 * @param callable
		 *            The callable which is used to request the chart data.
		 */
		public SimplePie(String chartId, Callable<String> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			String value = callable.call();
			if (value == null || value.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			data.set("value", value);
			return data;
		}
	}

	/**
	 * Represents a custom advanced pie.
	 */
	public static class AdvancedPie extends CustomChart {

		private final Callable<Map<String, Integer>> callable;

		/**
		 * Class constructor.
		 *
		 * @param chartId
		 *            The id of the chart.
		 * @param callable
		 *            The callable which is used to request the chart data.
		 */
		public AdvancedPie(String chartId, Callable<Map<String, Integer>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			JsonObject values = new JsonObject();
			Map<String, Integer> map = callable.call();
			if (map == null || map.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			boolean allSkipped = true;
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				if (entry.getValue() == 0) {
					continue; // Skip this invalid
				}
				allSkipped = false;
				values.set(entry.getKey(), entry.getValue());
			}
			if (allSkipped) {
				// Null = skip the chart
				return null;
			}
			data.set("values", values);
			return data;
		}
	}

	/**
	 * Represents a custom drilldown pie.
	 */
	public static class DrilldownPie extends CustomChart {

		private final Callable<Map<String, Map<String, Integer>>> callable;

		/**
		 * Class constructor.
		 *
		 * @param chartId
		 *            The id of the chart.
		 * @param callable
		 *            The callable which is used to request the chart data.
		 */
		public DrilldownPie(String chartId, Callable<Map<String, Map<String, Integer>>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		public JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			JsonObject values = new JsonObject();
			Map<String, Map<String, Integer>> map = callable.call();
			if (map == null || map.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			boolean reallyAllSkipped = true;
			for (Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {
				JsonObject value = new JsonObject();
				boolean allSkipped = true;
				for (Map.Entry<String, Integer> valueEntry : map.get(entryValues.getKey()).entrySet()) {
					value.set(valueEntry.getKey(), valueEntry.getValue());
					allSkipped = false;
				}
				if (!allSkipped) {
					reallyAllSkipped = false;
					values.set(entryValues.getKey(), value);
				}
			}
			if (reallyAllSkipped) {
				// Null = skip the chart
				return null;
			}
			data.set("values", values);
			return data;
		}
	}

	/**
	 * Represents a custom single line chart.
	 */
	public static class SingleLineChart extends CustomChart {

		private final Callable<Integer> callable;

		/**
		 * Class constructor.
		 *
		 * @param chartId
		 *            The id of the chart.
		 * @param callable
		 *            The callable which is used to request the chart data.
		 */
		public SingleLineChart(String chartId, Callable<Integer> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			int value = callable.call();
			if (value == 0) {
				// Null = skip the chart
				return null;
			}
			data.set("value", value);
			return data;
		}

	}

	/**
	 * Represents a custom multi line chart.
	 */
	public static class MultiLineChart extends CustomChart {

		private final Callable<Map<String, Integer>> callable;

		/**
		 * Class constructor.
		 *
		 * @param chartId
		 *            The id of the chart.
		 * @param callable
		 *            The callable which is used to request the chart data.
		 */
		public MultiLineChart(String chartId, Callable<Map<String, Integer>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			JsonObject values = new JsonObject();
			Map<String, Integer> map = callable.call();
			if (map == null || map.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			boolean allSkipped = true;
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				if (entry.getValue() == 0) {
					continue; // Skip this invalid
				}
				allSkipped = false;
				values.set(entry.getKey(), entry.getValue());
			}
			if (allSkipped) {
				// Null = skip the chart
				return null;
			}
			data.set("values", values);
			return data;
		}

	}

	/**
	 * Represents a custom simple bar chart.
	 */
	public static class SimpleBarChart extends CustomChart {

		private final Callable<Map<String, Integer>> callable;

		/**
		 * Class constructor.
		 *
		 * @param chartId
		 *            The id of the chart.
		 * @param callable
		 *            The callable which is used to request the chart data.
		 */
		public SimpleBarChart(String chartId, Callable<Map<String, Integer>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			JsonObject values = new JsonObject();
			Map<String, Integer> map = callable.call();
			if (map == null || map.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				JsonArray categoryValues = new JsonArray();
				categoryValues.add(entry.getValue());
				values.set(entry.getKey(), categoryValues);
			}
			data.set("values", values);
			return data;
		}

	}

	/**
	 * Represents a custom advanced bar chart.
	 */
	public static class AdvancedBarChart extends CustomChart {

		private final Callable<Map<String, int[]>> callable;

		/**
		 * Class constructor.
		 *
		 * @param chartId
		 *            The id of the chart.
		 * @param callable
		 *            The callable which is used to request the chart data.
		 */
		public AdvancedBarChart(String chartId, Callable<Map<String, int[]>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@Override
		protected JsonObject getChartData() throws Exception {
			JsonObject data = new JsonObject();
			JsonObject values = new JsonObject();
			Map<String, int[]> map = callable.call();
			if (map == null || map.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			boolean allSkipped = true;
			for (Map.Entry<String, int[]> entry : map.entrySet()) {
				if (entry.getValue().length == 0) {
					continue; // Skip this invalid
				}
				allSkipped = false;
				JsonArray categoryValues = new JsonArray();
				for (int categoryValue : entry.getValue()) {
					categoryValues.add(categoryValue);
				}
				values.set(entry.getKey(), categoryValues);
			}
			if (allSkipped) {
				// Null = skip the chart
				return null;
			}
			data.set("values", values);
			return data;
		}

	}

}

package com.kmecpp.osmium.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Pattern;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

public class WebUtil {

	public static URL getURL(String url, Map<String, String> params) throws MalformedURLException {
		if (params == null) {
			return new URL(url);
		}
		StringBuilder sb = new StringBuilder(url);
		if (!params.isEmpty()) {
			sb.append('?');
			for (Entry<String, String> entry : params.entrySet()) {
				sb.append(entry.getKey() + "=" + entry.getValue() + "&");
			}
			sb.setLength(sb.length() - 1);
		}
		return new URL(sb.toString());
	}

	public static JsonValue get(String url) throws IOException {
		return get(new URL(url));
	}

	public static JsonValue get(URL url) throws IOException {
		HttpURLConnection connection = getConnection(url);
		return Json.parse(new InputStreamReader(connection.getInputStream()));
	}

	public static JsonValue post(URL url, JsonValue json) throws IOException {
		return post(url, json);
	}

	public static JsonValue post(URL url, byte[] bytes) throws IOException {
		HttpURLConnection connection = getConnection(url);
		postFast(connection, bytes);
		return Json.parse(new InputStreamReader(connection.getInputStream()));
	}

	public static String post(String url, Map<String, String> params, Map<String, String> headers, byte[] bytes) throws IOException {
		return post(getURL(url, params), headers, bytes);
	}

	public static String post(URL url, Map<String, String> headers, byte[] bytes) throws IOException {
		//		System.out.println("POST: " + url);
		HttpURLConnection connection = getConnection(url, headers);
		postFast(connection, bytes);
		InputStream inputStream = connection.getResponseCode() < 400 ? connection.getInputStream() : connection.getErrorStream();
		return IOUtil.readString(inputStream);
	}

	public static void postFast(HttpURLConnection connection, byte[] bytes) throws IOException {
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		try (OutputStream os = connection.getOutputStream()) {
			os.write(bytes);
			os.flush();
		}
	}

	public static HttpURLConnection getConnection(URL url) throws IOException {
		return getConnection(url, null);
	}

	public static HttpURLConnection getConnection(URL url, Map<String, String> headers) throws IOException {
		return getHttpConnection(url, headers, 3000, 3000);
	}

	/**
	 * Gets an {@link HttpURLConnection} for the given URL with the given
	 * connection timeout and read timeout. This method also creates the
	 * connection with a default User-Agent of Mozilla/5.0 to avoid being
	 * filtered by many sites.
	 * 
	 * @param url
	 *            the URL to connect to
	 * @param connectTimeout
	 *            the timeout to use when opening a communications link to the
	 *            resource referenced by this URLConnection
	 * @param readTimeout
	 *            the timeout to use when reading from an input stream when a
	 *            connection is established to a resource
	 * @return the HTTP URL connection
	 * @throws IOException
	 *             if an IOException occurs
	 */
	public static HttpURLConnection getHttpConnection(URL url, Map<String, String> headers, int connectTimeout, int readTimeout) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setRequestProperty("Content-Type", "application/json");
		if (headers != null) {
			for (Entry<String, String> header : headers.entrySet()) {
				connection.setRequestProperty(header.getKey(), header.getValue());
			}
		}
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);
		return connection;
	}

	public static URL parseURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

	public static UUID getPlayerUUID(String name) throws IOException {
		return UUID.fromString(UUID_PATTERN
				.matcher(get(new URL("https://api.mojang.com/users/profiles/minecraft/" + name)).asObject().get("id").asString())
				.replaceAll("$1-$2-$3-$4-$5"));
	}

	public static String getPlayerName(UUID uuid) throws IOException {
		String responseString = IOUtil.readString(new URL("https://api.mojang.com/user/profiles/" + String.valueOf(uuid).replace("-", "") + "/names"));
		JsonArray result = Json.parse(responseString).asArray();
		return result.get(result.size() - 1).asObject().get("name").asString();
	}

}

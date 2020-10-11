package com.kmecpp.osmium.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RestService {

	private String baseUrl;
	private LinkedHashMap<String, String> headers;
	private int connectTimeout;
	private int readTimeout;

	public RestService(String baseUrl) {
		this.baseUrl = baseUrl;
		this.headers = new LinkedHashMap<>();
		this.headers.put("User-Agent", "Mozilla/5.0");
		this.headers.put("Content-Type", "application/json");
		this.connectTimeout = 3000;
		this.readTimeout = 3000;
	}

	public void setHeader(String key, String value) {
		headers.put(key, value);
	}

	public String get(String resource) throws IOException {
		return get(resource, null);
	}

	public String get(String resource, Map<String, String> params) throws IOException {
		HttpURLConnection connection = getConnection(resource, params);
		InputStream inputStream = connection.getResponseCode() < 400 ? connection.getInputStream() : connection.getErrorStream();
		return IOUtil.readString(inputStream);
	}

	public String post(String resource) throws IOException {
		return post(resource, new byte[0]);
	}

	public String post(String resource, Map<String, String> params) throws IOException {
		return post(resource, params, new byte[0]);
	}

	public String post(String resource, byte[] bytes) throws IOException {
		return post(resource, null, new byte[0]);
	}

	public String post(String resource, Map<String, String> params, byte[] bytes) throws IOException {
		HttpURLConnection connection = getConnection(resource, params);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		try (OutputStream os = connection.getOutputStream()) {
			os.write(bytes);
			os.flush();
		}
		InputStream inputStream = connection.getResponseCode() < 400 ? connection.getInputStream() : connection.getErrorStream();
		return IOUtil.readString(inputStream);
	}

	private URL getURL(String resource, Map<String, String> params) throws MalformedURLException {
		String baseUrlString = baseUrl + "/" + resource;
		if (params == null) {
			return new URL(baseUrlString);
		}
		StringBuilder sb = new StringBuilder(baseUrlString);
		if (!params.isEmpty()) {
			sb.append('?');
			for (Entry<String, String> entry : params.entrySet()) {
				sb.append(entry.getKey() + "=" + entry.getValue() + "&");
			}
			sb.setLength(sb.length() - 1);
		}
		return new URL(sb.toString());
	}

	public HttpURLConnection getConnection(String resource, Map<String, String> params) throws IOException {
		URL url = getURL(resource, params);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		for (Entry<String, String> header : headers.entrySet()) {
			connection.setRequestProperty(header.getKey(), header.getValue());
		}
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);
		return connection;
	}

}

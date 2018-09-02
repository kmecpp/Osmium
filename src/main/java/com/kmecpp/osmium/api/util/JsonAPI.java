package com.kmecpp.osmium.api.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.eclipsesource.json.JsonValue;

public abstract class JsonAPI {

	private final URL url;

	public JsonAPI(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public URL getUrl() {
		return url;
	}

	public JsonValue post(JsonValue json) throws IOException {
		return WebUtil.post(url, json);
	}

}
